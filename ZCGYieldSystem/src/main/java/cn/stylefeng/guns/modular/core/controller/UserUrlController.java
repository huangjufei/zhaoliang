package cn.stylefeng.guns.modular.core.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.annotion.BussinessLog;
import cn.stylefeng.guns.core.common.constant.PageConstant;
import cn.stylefeng.guns.core.common.constant.PageConstant.PageInfo;
import cn.stylefeng.guns.core.common.constant.StatusConstant;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.DateUtil;
import cn.stylefeng.guns.modular.base.controller.ControllerBase;
import cn.stylefeng.guns.modular.core.entity.ApkInfo;
import cn.stylefeng.guns.modular.core.entity.UserUrl;
import cn.stylefeng.guns.modular.core.repository.JpaApkInfoRepository;
import cn.stylefeng.guns.modular.core.repository.JpaBaseChannelRepository;
import cn.stylefeng.guns.modular.core.repository.JpaChannelMasterRepository;
import cn.stylefeng.guns.modular.core.repository.JpaUserUrlRepository;
import cn.stylefeng.guns.modular.core.vo.UserUrlVo;
import cn.stylefeng.guns.modular.system.repository.JpaRoleRepository;
import cn.stylefeng.guns.modular.system.repository.JpaUserRoleRepository;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Controller
@RequestMapping("/user_url")
public class UserUrlController extends ControllerBase{

	@Autowired
	private JpaUserUrlRepository jpaUserUrlRepository;
	@Autowired
	private JpaApkInfoRepository jpaApkInfoRepository;
	@Autowired
	private JpaUserRoleRepository jpaUserRoleRepository;
	@Autowired
	private JpaRoleRepository jpaRoleRepository;
	@Autowired
	private JpaChannelMasterRepository jpaChannelMasterRepository;
	@Autowired
	private JpaBaseChannelRepository jpaBaseChannelRepository;
	
	static final private String BASE_PROMOTE_LINK = "/fuser/register/";

	@Override
	protected String preffix() {
		return "/modular/core/user_url/";
	}
	
	public ModelAndView index(ModelAndView mav) {
		mav.setViewName(preffix() + "/index.html");
		mav.addObject("admin", ShiroKit.isAdmin());
		mav.addObject("companyList", jpaChannelMasterRepository.findAll());
		mav.addObject("appList", jpaApkInfoRepository.findAllGroupByName());
		mav.addObject("channelList", jpaBaseChannelRepository.findMoreByIdGrthan25());
		return mav;
	}
	
	public ModelAndView toAddPage(ModelAndView mav) {
		mav.setViewName(preffix() + "/add.html");
		mav.addObject("apkList", jpaApkInfoRepository.findMoreByStatusAndGroupByName(StatusConstant.SHELVES));
		mav.addObject("companyList", jpaChannelMasterRepository.findAll());
		mav.addObject("channelList", jpaBaseChannelRepository.findMoreByIdGrthan25());
		return mav;
	}
	
	@ResponseBody
	@RequestMapping("/list")
	public Object list(@RequestParam(required = false) String name 
			, @RequestParam(required = false) Integer page
			, @RequestParam(required = false) Integer limit
			, @RequestParam(required = false) String company
			, @RequestParam(required = false) String channel
			, @RequestParam(required = false) String appName) {
		//模糊查询
		name = name == null || "".equals(name) ? "%" : "%" + name + "%";
		//固定查询
		channel = channel == null || "".equals(channel) ? "%" : channel;
		appName = appName == null || "".equals(appName) ? "%" : appName;
		company = company == null || "".equals(company) ? "%" : company;
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit());
		
		List<UserUrlVo> list = null;
		if(ShiroKit.isAdmin()) {
			list = jpaUserUrlRepository.findPage(name ,channel ,company, appName,pageable);
		}
		else {
			String currentCompany = ShiroKit.getUser().getCompany();
        	if(currentCompany == null) {
        		return ResponseData.error("异常用户，无对应渠道！！");
        	}
	    	//不是超级管理员，只能看见该用户角色之下的所有用户信息
	    	Long userId = ShiroKit.getUser().getId();
	    	List<Long> roleList = jpaUserRoleRepository.findAllRoleIdByUserId(userId);
	    	List<Long> allRoleList = new ArrayList<Long>();
	    	//查找当前role的所有孩子节点
	    	while(roleList != null && (roleList=jpaRoleRepository.findAllChildIdByPid(roleList)) != null) {
	    		allRoleList.addAll(roleList);
	    		roleList = null;
	    	}
	    	if(allRoleList.isEmpty()) {
	    		list = jpaUserUrlRepository.findPageByCompanyAndUserId(name, currentCompany, userId, pageable);
	    	}else {
	    		list = jpaUserUrlRepository.findPageInRoleListAndByCompanyAndUserId(name ,allRoleList ,currentCompany ,userId,pageable);
	    	}
		}
		
		Page<UserUrlVo> res = new Page<UserUrlVo>();
		res.setRecords(list);
		res.setTotal(jpaUserUrlRepository.count());
		return LayuiPageFactory.createPageInfo(res);
	}
	
	@ResponseBody
	@BussinessLog("添加用户推广链接")
	@RequestMapping("/add")
	public Object add(@Validated(ValidPart1.class)UserUrl userUrl ,Errors error ,HttpServletRequest res) {
		//查询app存在
		ApkInfo apkInfo = jpaApkInfoRepository.findOneById(userUrl.getAppId());
		if(apkInfo == null) {
			return ResponseData.error("指定生成app不存在，appid：" + userUrl.getAppId());
		}
		Long userId = userUrl.getUserId() == null ? ShiroKit.getUser().getId() : userUrl.getUserId();
		//已创建链接
		UserUrl old = jpaUserUrlRepository.findOneByUserIdAndChannelAndAppId(userId ,userUrl.getChannel() ,userUrl.getAppId());
		if(old != null) {
			return ResponseData.error("已创建该app，该渠道的推广链接，appId：" + userUrl.getAppId() + " ,channel: " + userUrl.getChannel());
		}
		//参数设置
		userUrl.setUserId(userId);
		userUrl.setRecommendKey(UUID.randomUUID().toString().replace("-", "").toUpperCase());
		//创建推广地址
		String urlHeader = res.getRequestURL().substring(0, res.getRequestURL().indexOf(res.getRequestURI()));
		String url = urlHeader + "/" + res.getContextPath()
			+ BASE_PROMOTE_LINK + apkInfo.getResourcesId() + "/" + userUrl.getChannel() + "/" + userUrl.getRecommendKey();
		userUrl.setUrl(url);
		userUrl.setCreateTime(DateUtil.currentDate());
		jpaUserUrlRepository.saveAndFlush(userUrl);
		return ResponseData.success();
	}
}
