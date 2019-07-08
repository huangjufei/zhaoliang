package cn.stylefeng.guns.modular.data.manage.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import cn.stylefeng.guns.core.controller.valid.ValidPart2;
import cn.stylefeng.guns.core.controller.valid.ValidPart3;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.util.DateUtil;
import cn.stylefeng.guns.modular.base.controller.ControllerBase;
import cn.stylefeng.guns.modular.core.entity.DataRestricted;
import cn.stylefeng.guns.modular.core.repository.JpaApkInfoRepository;
import cn.stylefeng.guns.modular.core.repository.JpaBaseChannelRepository;
import cn.stylefeng.guns.modular.core.repository.JpaChannelMasterRepository;
import cn.stylefeng.guns.modular.core.repository.JpaDataRestrictedRepository;
import cn.stylefeng.guns.modular.core.vo.DataRestrictedVo;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Controller
@RequestMapping("/data/buckle_quantity")
public class BuckleQuantityController extends ControllerBase{
	
	@Autowired
	private JpaDataRestrictedRepository jpaDataRestrictedRepository;
	@Autowired
	private JpaChannelMasterRepository jpaChannelMasterRepository;
	@Autowired
	private JpaBaseChannelRepository jpaBaseChannelRepository;
	@Autowired
	private JpaApkInfoRepository jpaApkInfoRepository;

	protected String preffix() {
		return "/modular/data_manage/buckle_quantity/";
	}
	
	@Override
	public ModelAndView index(ModelAndView mav) {
		mav.addObject("admin", ShiroKit.isAdmin());
		mav.addObject("companyList", jpaChannelMasterRepository.findAll());
		mav.addObject("appList", jpaApkInfoRepository.findAllGroupByName());
		mav.addObject("channelList", jpaBaseChannelRepository.findMoreByIdGrthan25());
		return super.index(mav);
	}
	
	@Override
	public ModelAndView toAddPage(ModelAndView mav) {
		mav.addObject("admin", ShiroKit.isAdmin());
		mav.addObject("companyList", jpaChannelMasterRepository.findAll());
		mav.addObject("appList", jpaApkInfoRepository.findAllGroupByName());
		mav.addObject("channelList", jpaBaseChannelRepository.findMoreByIdGrthan25());
		return super.toAddPage(mav);
	}
	
	@ResponseBody
	@RequestMapping("/list")
	public Object list(@RequestParam(required = false) String timeLimit ,@RequestParam(required = false)Integer page
			, @RequestParam(required = false)Integer limit ,@RequestParam(required = false)String name
			, @RequestParam(required = false)String company ,@RequestParam(required = false)String channel
			, @RequestParam(required = false)String defaultStr 
			, @RequestParam(required = false)String appName) {
		// 查询条件整理
		Date startTime = DateUtil.date1970();
		Date endTime = DateUtil.currentDate();
		String[] times = null;
		if(timeLimit != null && !timeLimit.equals("")) {
			times = timeLimit.split(" - ");
			if(times == null || times.length != 2) {
				return ResponseData.error(400, "时间参数错误:" + timeLimit);
			}
			startTime = DateUtil.parse(times[0], startTime);
			endTime = DateUtil.parse(times[1], endTime);
		}
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit() ,Sort.by(Direction.DESC, "createTime"));
		name = name == null || "".equals(name.trim()) ? "%" : "%" + name + "%";
		appName = appName == null || "".equals(appName.trim()) ? "%" : "%" + appName + "%";
		company = company == null || "".equals(company.trim()) ? "%" : company;
		channel = channel == null || "".equals(channel.trim()) ? "%" : channel;
		
		List<DataRestrictedVo> list =null;
		Long total = 0L;
		//查询默认扣量
		if(defaultStr != null && !defaultStr.trim().equals("")) {
			list = jpaDataRestrictedRepository.findPageByDefaultAndInDate(startTime ,endTime ,pageable);
			total = jpaDataRestrictedRepository.findPageCountByDefaultAndInDate(startTime ,endTime);
		}
		else {
			list = jpaDataRestrictedRepository.findPageInDate(name ,company ,channel ,startTime ,endTime ,appName,pageable);
			total = jpaDataRestrictedRepository.findPageCountInDate(name ,company ,channel ,startTime, endTime,appName);
		}
		Page<DataRestrictedVo> res = new Page<DataRestrictedVo>();
		res.setRecords(list);
		res.setTotal(total == null ? 0L : total);
		return LayuiPageFactory.createPageInfo(res);
	}
	
	@ResponseBody
	@BussinessLog("添加扣量")
	@RequestMapping("/add")
	public Object add(@Validated(ValidPart1.class)DataRestricted dataRestricted ,Errors error 
			, @RequestParam(required = true) Integer value ,@RequestParam(required = true) String timeLimit) {
		if(!dataRestricted.isWorkFieldOk()) {
			return ResponseData.error("指定扣量规则错误，数据扣除规则可选字段：" + dataRestricted.getNumberValue() 
				+ " ;百分比扣量规则可选字段：" + dataRestricted.getRatioValue());
		}
		
		if(timeLimit == null || "".equals(timeLimit.trim())) {
			return ResponseData.error("扣量时间未指定");
		}
		
		Date startTime = null;
		Date endTime = null;
		String[] times = timeLimit.split(" - ");
		if(times == null || times.length != 2) {
			return ResponseData.error(400, "时间参数错误:" + timeLimit);
		}
		startTime = DateUtil.parse(times[0], "yyyy-MM-dd");
		endTime = DateUtil.parse(times[1], "yyyy-MM-dd");
		DataRestricted old = jpaDataRestrictedRepository.findOneByRestrictedNameAndStartTimeAndEndTime(dataRestricted.getRestrictedName()
				, startTime ,endTime);
		if(old != null) {
			return ResponseData.error("已存在该链接当前时间段扣量规则，请更新，扣量规则id：" + old.getId());
		}
		ShiroUser user = ShiroKit.getUser();
		dataRestricted.setNumberValue(value);
		dataRestricted.setRatioValue(value);
		dataRestricted.setCreateTime(DateUtil.currentDate());
		dataRestricted.setUpdateTime(dataRestricted.getCreateTime());
		dataRestricted.setCreator(user.getName());
		dataRestricted.setStartTime(startTime);
		dataRestricted.setEndTime(endTime);
		
		jpaDataRestrictedRepository.saveAndFlush(dataRestricted);
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("编辑扣量")
	@RequestMapping("/edit")
	public Object edit(@Validated(ValidPart2.class)DataRestricted dataRestricted ,Errors error 
			, @RequestParam(required = true) Integer value ,@RequestParam(required = true) String timeLimit) {
		if(!dataRestricted.isWorkFieldOk()) {
			return ResponseData.error("指定扣量规则错误，数据扣除规则可选字段：" + dataRestricted.getNumberValue() 
				+ " ;百分比扣量规则可选字段：" + dataRestricted.getRatioValue());
		}
		if(timeLimit == null || "".equals(timeLimit.trim())) {
			return ResponseData.error("扣量时间未指定");
		}
		
		DataRestricted old = jpaDataRestrictedRepository.findOneById(dataRestricted.getId());
		if(old == null) {
			return ResponseData.error("编辑扣量规则不存在，扣量id：" + dataRestricted.getId());
		}
		Date startTime = null;
		Date endTime = null;
		String[] times = timeLimit.split(" - ");
		if(times == null || times.length != 2) {
			return ResponseData.error(400, "时间参数错误:" + timeLimit);
		}
		startTime = DateUtil.parse(times[0], "yyyy-MM-dd");
		endTime = DateUtil.parse(times[1], "yyyy-MM-dd");
		startTime = startTime == null ? old.getStartTime() : startTime;
		endTime = endTime == null ? old.getEndTime() : endTime;
		LogObjectHolder.me().set(old);
		jpaDataRestrictedRepository.updateById(dataRestricted.getId() ,value 
				, dataRestricted.getWorkRestrictedField() ,startTime ,endTime ,DateUtil.currentDate() ,dataRestricted.getInfo()
				, dataRestricted.getShowMinData());
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("删除渠道")
	@RequestMapping("/delete")
	public Object delete(@Validated(ValidPart3.class) DataRestricted dataRestricted,Errors error) {
		DataRestricted old = jpaDataRestrictedRepository.findOneById(dataRestricted.getId());
		if(old == null) {
			return ResponseData.error("删除扣量规则不存在，扣量id：" + dataRestricted.getId());
		}
		LogObjectHolder.me().set(old);
		jpaDataRestrictedRepository.delete(old);
		return ResponseData.success();
	}
	
	@ResponseBody
	@RequestMapping("/searchOne")
	public Object searchOne(@Validated(ValidPart3.class) DataRestricted dataRestricted,Errors error) {
		DataRestricted old = jpaDataRestrictedRepository.findOneById(dataRestricted.getId());
		if(old == null) {
			return ResponseData.error("删除扣量规则不存在，扣量id：" + dataRestricted.getId());
		}
		return ResponseData.success(old);
	}

	@RequestMapping("/toSettingPage")
	public ModelAndView toSettingPage(ModelAndView mav ,Long id ,String uuid) {
		mav.setViewName(preffix() + "/setting.html");
		return mav;
	}
	
	@RequestMapping("/toDefaultSettingPage")
	public ModelAndView toDefaultSettingPage(ModelAndView mav) {
		mav.setViewName(preffix() + "/defaultSetting.html");
		return mav;
	}
}
