package cn.stylefeng.guns.modular.data.manage.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.constant.PageConstant;
import cn.stylefeng.guns.core.common.constant.PageConstant.PageInfo;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.util.DateUtil;
import cn.stylefeng.guns.modular.base.controller.ControllerBase;
import cn.stylefeng.guns.modular.core.entity.AppUserAccount;
import cn.stylefeng.guns.modular.core.entity.DataRestricted;
import cn.stylefeng.guns.modular.core.repository.JpaApkInfoRepository;
import cn.stylefeng.guns.modular.core.repository.JpaAppUserAccountRepository;
import cn.stylefeng.guns.modular.core.repository.JpaBaseChannelRepository;
import cn.stylefeng.guns.modular.core.repository.JpaChannelMasterRepository;
import cn.stylefeng.guns.modular.core.repository.JpaDataRestrictedRepository;
import cn.stylefeng.guns.modular.core.repository.JpaFlowUserRepository;
import cn.stylefeng.guns.modular.core.repository.JpaFlowUserVisitNumberRepository;
import cn.stylefeng.guns.modular.core.repository.JpaUserBrowseRepository;
import cn.stylefeng.guns.modular.core.vo.DataFlowUserVo;
import cn.stylefeng.guns.modular.core.vo.DataStatisticalFlowUserVo;
import cn.stylefeng.guns.modular.core.vo.VisitCountVo;
import cn.stylefeng.guns.modular.core.vo.UserBuckleQuantityInfo;
import cn.stylefeng.guns.modular.system.repository.JpaRoleRepository;
import cn.stylefeng.guns.modular.system.repository.JpaUserRepository;
import cn.stylefeng.guns.modular.system.repository.JpaUserRoleRepository;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Controller
@RequestMapping("/data/flow_user")
public class FlowUserDataManageController extends ControllerBase {

	@Autowired
	private JpaFlowUserRepository jpaFlowUserRepository;
	@Autowired
	private JpaChannelMasterRepository jpaChannelMasterRepository;
	@Autowired
	private JpaRoleRepository jpaRoleRepository;
	@Autowired
	private JpaUserRoleRepository jpaUserRoleRepository;
	@Autowired
	private JpaAppUserAccountRepository jpaAppUserAccountRepository;
	@Autowired
	private JpaDataRestrictedRepository jpaDataRestrictedRepository;
	@Autowired
	private JpaUserRepository JpaUserRepository;
	@Autowired
	private JpaApkInfoRepository jpaApkInfoRepository;
	@Autowired
	private JpaBaseChannelRepository jpaBaseChannelRepository;
	
	@Autowired
	private JpaFlowUserVisitNumberRepository jpaFlowUserVisitNumberRepository;
	
	@Autowired
	private JpaUserBrowseRepository jpaUserBrowseRepository;

	@Override
	protected String preffix() {
		return "/modular/data_manage/flow_user/";
	}

	@Override
	public ModelAndView index(ModelAndView mav) {
		mav.addObject("admin", ShiroKit.isAdmin());
		mav.addObject("companyList", jpaChannelMasterRepository.findAll());
		mav.addObject("appList", jpaApkInfoRepository.findAllGroupByName());
		mav.addObject("channelList", jpaBaseChannelRepository.findMoreByIdGrthan25());
		return super.index(mav);
	}


	@ResponseBody
	@RequestMapping("/list")
	public Object list(@RequestParam(required = false) String name, @RequestParam(required = false) String timeLimit,
			@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit,
			@RequestParam(required = false) String showType, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel, @RequestParam(required = false) String appName,
			@RequestParam(required = false) String os) {
		// 查询条件整理
		Date startTime = DateUtil.date1970();
		Date endTime = DateUtil.currentDate();
		String[] times = null;
		if (timeLimit != null && !timeLimit.equals("")) {
			times = timeLimit.split(" - ");
			if (times == null || times.length != 2) {
				return ResponseData.error(400, "时间参数错误:" + timeLimit);
			}
			startTime = DateUtil.parse(times[0], startTime);
			endTime = DateUtil.parse(times[1], endTime);
		}
		
		name = name == null || "".equals(name) ? "%" : "%" + name + "%";
		company = company == null || "".equals(company) ? "%" : company;
		channel = channel == null || "".equals(channel) ? "%" : channel;
		os = os == null || "".equals(os) ? "%" : os;
		showType = showType == null || "".equals(showType) ? "列表数据" : showType;
		appName = appName == null || "".equals(appName.trim()) ? "%" : appName;
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit(),
				Sort.by(Direction.DESC, "createTime"));

		// 列表数据展示
		List<DataFlowUserVo> flowUserList = null;
		// 统计数据展示
		// 展示数据为：日期 --- h5注册数 --- app注册数 --- 日期转化率(以日期为标准的转化率) --- 注册转化率（不限制日期，注册即可）
		// 该数据需动态组装
		List<DataStatisticalFlowUserVo> resList = new ArrayList<DataStatisticalFlowUserVo>();
		Long total = 0L;

		if (ShiroKit.isAdmin()) {
			flowUserList = jpaFlowUserRepository.findPage(name, company, channel, appName, startTime, endTime
					, os ,pageable);
			// 列表数据展示
			if ("列表数据".equals(showType)) {
				Page<DataFlowUserVo> res = new Page<DataFlowUserVo>();
				res.setRecords(flowUserList);
				res.setTotal(jpaFlowUserRepository.findPageCount(name, company, channel, appName, startTime, endTime));
				return LayuiPageFactory.createPageInfo(res);
			}
			total = (long) jpaFlowUserRepository
					.findCountGroupByDate(name, company, channel, appName, startTime, endTime ,os).size();
		} else {
			// 暂不开放非管理员使用该功能
		}
		
		
		Long start = System.currentTimeMillis();
		
		
		//sql没有进行分组;如果不带条件查询出全部引流数据;(flow_user表)
		flowUserList = jpaFlowUserRepository.findAllBy(name, company, channel, appName, startTime, endTime,os,pageable);	
		//sql没有进行分组;默认查询全部数据(t_user_account表)
		List<AppUserAccount> appUserList = jpaAppUserAccountRepository.findPageByRegisterBetweenDate(startTime, endTime);				
		// 数据计算
		Map<String, DataStatisticalFlowUserVo> tmpMap = new LinkedHashMap<String, DataStatisticalFlowUserVo>();
		
		
	/**	
	// 1.遍历flowuser的注册用户
		for (int i = 0, len = flowUserList.size(); i < len; ++i) {
			//在map.put之前都是准备map的key
			DataFlowUserVo dfuv = flowUserList.get(i);
			String date = DateUtil.format(dfuv.getCreateTime(), "yyyy-MM-dd");
			//key = 年月日+快借钱包+公用渠道+马上到账+小狗
			//apk_info.getAppName()+user_url.channelName()+sys_user.getCompany()+sys_user.getUserName();			
			String key = date + dfuv.getAppName() + dfuv.getChannelName() + dfuv.getCompany() + dfuv.getUserName();
			
			DataStatisticalFlowUserVo dsfuv = tmpMap.get(key);
			//如果map中没有取到数据就添加key为上面几个值的组合,值为循环中的该对象(作用就是马上到账小狗快借钱包公共渠道按天分组)
			if (dsfuv == null) {
				dsfuv = new DataStatisticalFlowUserVo(date, 0L);
				//下面几个参数可以不需要
				dsfuv.setAppName(dfuv.getAppName());
				dsfuv.setChannelName(dfuv.getChannelName());
				dsfuv.setCompany(dfuv.getCompany());
				dsfuv.setUserName(dfuv.getUserName());
				tmpMap.put(key,dsfuv);
			}						
						
			// 3,flowuser 用户计数加1(作用:分组后引流值加1)
			dsfuv.setFlowUserCount(dsfuv.getFlowUserCount() + 1);						
			
			// 4,遍历t_user_account表的注册用户，比对flowuser的用户是否成功转化
			for (int j = 0, jLen = appUserList.size(); j < jLen; ++j) {				
				AppUserAccount aua = appUserList.get(j);
				String registerDate = DateUtil.format(aua.getRegisterTime(), "yyyy-MM-dd");
				
				// 如果手机号码一致,则 用户转化率加1
				if (dfuv.getPhone().equals(aua.getMobilePhone())) {
					//4.1 则 用户转化率加1
					dsfuv.setRegisterConversionNumber(dsfuv.getRegisterConversionNumber() + 1);				
					// 4.2 用户同一天转化率加1
					if (registerDate.equals(date)) {
						dsfuv.setDateConversionNumber(dsfuv.getDateConversionNumber() + 1);
					}			
					// 4.3 向dsfuv 对象appUsermap(key=手机号码,value=t_user_account表)
					if (dsfuv.getAppUserMap().get(aua.getMobilePhone()) == null) {
						dsfuv.setAppUserCount(dsfuv.getAppUserCount() + 1);
						dsfuv.getAppUserMap().put(aua.getMobilePhone(), aua);
					}					
					// 如果flow_user和t_user_account的手机号码一致就跳出appUserList的for循环;
					break;
				}
				
			}
		}*/
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
			// 1.遍历flowuser的注册用户
				for (int i = 0, len = flowUserList.size(); i < len; ++i) {
					//在map.put之前都是准备map的key
					DataFlowUserVo dfuv = flowUserList.get(i);
					String date = sdf.format(dfuv.getCreateTime());
					//key = 年月日+快借钱包+公用渠道+马上到账+小狗
					//apk_info.getAppName()+user_url.channelName()+sys_user.getCompany()+sys_user.getUserName();	
					
					StringBuilder sb = new StringBuilder();
					String key = sb.append(date).append(dfuv.getAppName()).append(dfuv.getChannelName()).append(dfuv.getCompany()).append(dfuv.getUserName()).toString();
					DataStatisticalFlowUserVo dsfuv = tmpMap.get(key);
					//如果map中没有取到数据就添加key为上面几个值的组合,值为循环中的该对象(作用就是马上到账小狗快借钱包公共渠道按天分组)
					if (dsfuv == null) {
						dsfuv = new DataStatisticalFlowUserVo(date, 0L);
						//下面几个参数可以不需要
						dsfuv.setAppName(dfuv.getAppName());
						dsfuv.setChannelName(dfuv.getChannelName());
						dsfuv.setCompany(dfuv.getCompany());
						dsfuv.setUserName(dfuv.getUserName());
						tmpMap.put(key,dsfuv);
					}						
								
					// 3,flowuser 用户计数加1(作用:分组后引流值加1)
					dsfuv.setFlowUserCount(dfuv.getFlowUserCount());						
					
					// 4,遍历t_user_account表的注册用户，比对flowuser的用户是否成功转化
					for (int j = 0, jLen = appUserList.size(); j < jLen; ++j) {				
						AppUserAccount aua = appUserList.get(j);
						String registerDate = sdf.format(aua.getRegisterTime());
						
						// 如果手机号码一致,则 用户转化率加1
						if (dfuv.getPhone().equals(aua.getMobilePhone())) {
							//4.1 则 用户转化率加1
							dsfuv.setRegisterConversionNumber(dsfuv.getRegisterConversionNumber() + 1);				
							// 4.2 用户同一天转化率加1
							if (registerDate.equals(date)) {
								dsfuv.setDateConversionNumber(dsfuv.getDateConversionNumber() + 1);
							}			
							// 4.3 向dsfuv 对象appUsermap(key=手机号码,value=t_user_account表)
							if (dsfuv.getAppUserMap().get(aua.getMobilePhone()) == null) {
								dsfuv.setAppUserCount(dsfuv.getAppUserCount() + 1);
								dsfuv.getAppUserMap().put(aua.getMobilePhone(), aua);
							}					
							// 如果flow_user和t_user_account的手机号码一致就跳出appUserList的for循环;
							break;
						}
						
					}
				}
		
		
		
		//h5 独立访问数
		List<VisitCountVo> flowUserVisitNumberVoList = 
				jpaFlowUserVisitNumberRepository.findDateCountNumberBy(name, company, channel, appName, startTime, endTime,os);
		
		
		//添加同一条件下h5访问数
		for(int j = 0 ,jLen = flowUserVisitNumberVoList.size() ;j < jLen ;++j) {
			VisitCountVo fuvnvo = flowUserVisitNumberVoList.get(j);
			String fuvnvoKey = fuvnvo.getVisitTime() + fuvnvo.getAppName() + fuvnvo.getChannelName() 
			 + fuvnvo.getCompany() + fuvnvo.getUserName();
			DataStatisticalFlowUserVo dsfuv = tmpMap.get(fuvnvoKey);
			if(dsfuv != null) {
				dsfuv.setFlowUserVisitCount(fuvnvo.getCount());
			}
		}
		
		
		//app浏览记录(删除了里面的分组)
		List<VisitCountVo> appBrowseList = 
				jpaUserBrowseRepository.findDateCountNumberBy(name,company, channel, appName, startTime, endTime, os);
		
		//添加同一条件下app浏览记录
		for(int j = 0 ,jLen = appBrowseList.size() ;j < jLen ;++j) {
			VisitCountVo app = appBrowseList.get(j);
			StringBuilder sb = new StringBuilder();
			String fuvnvoKey = sb.append(app.getVisitTime()).append(app.getAppName()).append(app.getChannelName()).append(app.getCompany() ).append(app.getUserName()).toString();
			DataStatisticalFlowUserVo dsfuv = tmpMap.get(fuvnvoKey);
			if(dsfuv != null) {
				dsfuv.setAppBrowseCount(app.getCount());
			}
		}
		
		// 计算比率值
		for (String date : tmpMap.keySet()) {
			DataStatisticalFlowUserVo dsfuv = tmpMap.get(date);
			dsfuv.calculateRate();
			resList.add(dsfuv);
		}

		Page<DataStatisticalFlowUserVo> res = new Page<DataStatisticalFlowUserVo>();
		res.setRecords(resList);
		res.setTotal(total == null ? 0 : total);
		
		Long end = System.currentTimeMillis();
		System.out.print("sj");
		System.out.print(end - start);
		return LayuiPageFactory.createPageInfo(res);
	}
	
	
	


	@RequestMapping("/toRegisterConversionList")
	public String toRegisterConversionList(@RequestParam(required = true) String date,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel) {
		return preffix() + "/registerConversionList.html";
	}

	@ResponseBody
	@RequestMapping("/registerConversionList")
	public Object registerConversionList(@RequestParam(required = true) String date,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel, @RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		// 参数整理
		name = name == null || "".equals(name) ? "%" : "%" + name + "%";
		company = company == null || "".equals(company) ? "%" : company;
		channel = channel == null || "".equals(channel) ? "%" : channel;
		try {
			DateUtil.parse(date, "yyyy-MM-dd");
		} catch (Exception e) {
			return ResponseData.error("日期格式错误，正确格式：yyyy-MM-dd ,参数：日期：" + date);
		}
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit(),
				Sort.by(Direction.DESC, "createTime"));

		// 结果
		List<DataFlowUserVo> list = null;
		Long total = 0L;
		if (ShiroKit.isAdmin()) {
			list = jpaFlowUserRepository.findRegisterConversionByConditionsInDate(name, company, channel, date,
					pageable);
			total = jpaFlowUserRepository.findRegisterConversionCountByConditionsInDate(name, company, channel, date);
		} else {
			String currentCompany = ShiroKit.getUser().getCompany();
			if (currentCompany == null) {
				return ResponseData.error("异常用户，无对应渠道！！");
			}
			// 不是超级管理员，只能看见该用户角色之下的所有用户信息
			Long userId = ShiroKit.getUser().getId();
			List<Long> roleList = jpaUserRoleRepository.findAllRoleIdByUserId(userId);
			List<Long> allRoleList = new ArrayList<Long>();
			// 查找当前role的所有孩子节点
			while (roleList != null && (roleList = jpaRoleRepository.findAllChildIdByPid(roleList)) != null) {
				allRoleList.addAll(roleList);
				roleList = null;
			}

			if (allRoleList.isEmpty()) {
				list = jpaFlowUserRepository.findRegisterConversionByUserIdAndConditionsInDate(name, channel, date,
						userId, pageable);
				total = jpaFlowUserRepository.findRegisterConversionCountByUserIdAndConditionsInDate(name, channel,
						date, userId);
			} else {
				list = jpaFlowUserRepository.findRegisterConversionByInRoleList(name, currentCompany, channel, date,
						userId, allRoleList, pageable);
				total = jpaFlowUserRepository.findRegisterConversionCountByInRoleList(name, currentCompany, channel,
						date, userId, allRoleList);
			}

		}
		Page<DataFlowUserVo> res = new Page<DataFlowUserVo>();
		res.setRecords(list);
		res.setTotal(total == null ? 0 : total);
		return LayuiPageFactory.createPageInfo(res);
	}

	@RequestMapping("/toDifferenceConversionList")
	public String toDifferenceConversionList(@RequestParam(required = true) String date,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel) {
		return preffix() + "/differenceConversionList.html";
	}

	@ResponseBody
	@RequestMapping("/differenceConversionList")
	public Object differenceConversionList(@RequestParam(required = true) String date,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel, @RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		// 参数整理
		name = name == null || "".equals(name) ? "%" : "%" + name + "%";
		company = company == null || "".equals(company) ? "%" : company;
		channel = channel == null || "".equals(channel) ? "%" : channel;
		try {
			DateUtil.parse(date, "yyyy-MM-dd");
		} catch (Exception e) {
			return ResponseData.error("日期格式错误，正确格式：yyyy-MM-dd ,参数：日期：" + date);
		}
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit(),
				Sort.by(Direction.DESC, "createTime"));

		// 结果
		List<DataFlowUserVo> list = null;
		Long total = 0L;
		if (ShiroKit.isAdmin()) {
			list = jpaFlowUserRepository.findDifferenceConversionByConditionsInDate(name, company, channel, date,
					pageable);
			total = jpaFlowUserRepository.findDifferenceConversionCountByConditionsInDate(name, company, channel, date);
		} else {
			String currentCompany = ShiroKit.getUser().getCompany();
			if (currentCompany == null) {
				return ResponseData.error("异常用户，无对应渠道！！");
			}
			// 不是超级管理员，只能看见该用户角色之下的所有用户信息
			Long userId = ShiroKit.getUser().getId();
			List<Long> roleList = jpaUserRoleRepository.findAllRoleIdByUserId(userId);
			List<Long> allRoleList = new ArrayList<Long>();
			// 查找当前role的所有孩子节点
			while (roleList != null && (roleList = jpaRoleRepository.findAllChildIdByPid(roleList)) != null) {
				allRoleList.addAll(roleList);
				roleList = null;
			}
			if (allRoleList.isEmpty()) {
				list = jpaFlowUserRepository.findDifferenceConversionByUserIdAndConditionsInDate(name, channel, date,
						userId, pageable);
				total = jpaFlowUserRepository.findDifferenceConversionCountByUserIdAndConditionsInDate(name, channel,
						date, userId);
			} else {
				list = jpaFlowUserRepository.findDifferenceConversionByInRoleList(name, currentCompany, channel, date,
						userId, allRoleList, pageable);
				total = jpaFlowUserRepository.findDifferenceConversionCountByInRoleList(name, currentCompany, channel,
						date, userId, allRoleList);
			}

		}
		Page<DataFlowUserVo> res = new Page<DataFlowUserVo>();
		res.setRecords(list);
		res.setTotal(total == null ? 0 : total);
		return LayuiPageFactory.createPageInfo(res);
	}

	@RequestMapping("/toFailedConversionList")
	public String toFailedConversionList(@RequestParam(required = true) String date,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel) {
		return preffix() + "/failedConversionList.html";
	}

	@ResponseBody
	@RequestMapping("/failedConversionList")
	public Object failedConversionList(@RequestParam(required = true) String date,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel, @RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		// 参数整理
		name = name == null || "".equals(name) ? "%" : "%" + name + "%";
		company = company == null || "".equals(company) ? "%" : company;
		channel = channel == null || "".equals(channel) ? "%" : channel;
		try {
			DateUtil.parse(date, "yyyy-MM-dd");
		} catch (Exception e) {
			return ResponseData.error("日期格式错误，正确格式：yyyy-MM-dd ,参数：日期：" + date);
		}
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit(),
				Sort.by(Direction.DESC, "createTime"));

		// 结果
		List<DataFlowUserVo> list = null;
		Long total = 0L;
		if (ShiroKit.isAdmin()) {
			list = jpaFlowUserRepository.findFailedConversionByConditionsInDate(name, company, channel, date, pageable);
			total = jpaFlowUserRepository.findFailedConversionCountByConditionsInDate(name, company, channel, date);
		} else {
			String currentCompany = ShiroKit.getUser().getCompany();
			if (currentCompany == null) {
				return ResponseData.error("异常用户，无对应渠道！！");
			}
			// 不是超级管理员，只能看见该用户角色之下的所有用户信息
			Long userId = ShiroKit.getUser().getId();
			List<Long> roleList = jpaUserRoleRepository.findAllRoleIdByUserId(userId);
			List<Long> allRoleList = new ArrayList<Long>();
			// 查找当前role的所有孩子节点
			while (roleList != null && (roleList = jpaRoleRepository.findAllChildIdByPid(roleList)) != null) {
				allRoleList.addAll(roleList);
				roleList = null;
			}
			if (allRoleList.isEmpty()) {
				list = jpaFlowUserRepository.findFailedConversionByUserIdAndConditionsInDate(name, channel, date,
						userId, pageable);
				total = jpaFlowUserRepository.findFailedConversionCountByUserIdAndConditionsInDate(name, channel, date,
						userId);
			} else {
				list = jpaFlowUserRepository.findFailedConversionByInRoleList(name, currentCompany, channel, date,
						userId, allRoleList, pageable);
				total = jpaFlowUserRepository.findFailedConversionCountByInRoleList(name, currentCompany, channel, date,
						userId, allRoleList);
			}

		}
		Page<DataFlowUserVo> res = new Page<DataFlowUserVo>();
		res.setRecords(list);
		res.setTotal(total == null ? 0 : total);
		return LayuiPageFactory.createPageInfo(res);
	}

	@RequestMapping("/toDateConversionList")
	public String toDateConversionList(@RequestParam(required = true) String date,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel) {
		return preffix() + "/dateConversionList.html";
	}

	@ResponseBody
	@RequestMapping("/dateConversionList")
	public Object dateConversionList(@RequestParam(required = true) String date,
			@RequestParam(required = false) String name, @RequestParam(required = false) String company,
			@RequestParam(required = false) String channel, @RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		// 参数整理
		name = name == null || "".equals(name) ? "%" : "%" + name + "%";
		company = company == null || "".equals(company) ? "%" : company;
		channel = channel == null || "".equals(channel) ? "%" : channel;
		try {
			DateUtil.parse(date, "yyyy-MM-dd");
		} catch (Exception e) {
			return ResponseData.error("日期格式错误，正确格式：yyyy-MM-dd ,参数：日期：" + date);
		}
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit(),
				Sort.by(Direction.DESC, "createTime"));

		// 结果
		List<DataFlowUserVo> list = null;
		Long total = 0L;
		if (ShiroKit.isAdmin()) {
			list = jpaFlowUserRepository.findDateConversionByConditionsInDate(name, company, channel, date, pageable);
			total = jpaFlowUserRepository.findDateConversionCountByConditionsInDate(name, company, channel, date);
		} else {
			String currentCompany = ShiroKit.getUser().getCompany();
			if (currentCompany == null) {
				return ResponseData.error("异常用户，无对应渠道！！");
			}
			// 不是超级管理员，只能看见该用户角色之下的所有用户信息
			Long userId = ShiroKit.getUser().getId();
			List<Long> roleList = jpaUserRoleRepository.findAllRoleIdByUserId(userId);
			List<Long> allRoleList = new ArrayList<Long>();
			// 查找当前role的所有孩子节点
			while (roleList != null && (roleList = jpaRoleRepository.findAllChildIdByPid(roleList)) != null) {
				allRoleList.addAll(roleList);
				roleList = null;
			}
			if (allRoleList.isEmpty()) {
				list = jpaFlowUserRepository.findDateConversionByUserIdAndConditionsInDate(name, channel, date, userId,
						pageable);
				total = jpaFlowUserRepository.findDateConversionCountByUserIdAndConditionsInDate(name, channel, date,
						userId);
			} else {
				list = jpaFlowUserRepository.findDateConversionByInRoleList(name, currentCompany, channel, date, userId,
						allRoleList, pageable);
				total = jpaFlowUserRepository.findDateConversionCountByInRoleList(name, currentCompany, channel, date,
						userId, allRoleList);
			}
		}
		Page<DataFlowUserVo> res = new Page<DataFlowUserVo>();
		res.setRecords(list);
		res.setTotal(total == null ? 0 : total);
		return LayuiPageFactory.createPageInfo(res);
	}

	@ResponseBody
	@RequestMapping("/buckle_quantity_list")
	public Object buckleQuantityList(@RequestParam(required = false) String name,
			@RequestParam(required = false) String timeLimit, @RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) String buckleStr,
			@RequestParam(required = false) String company, @RequestParam(required = false) String channel,
			@RequestParam(required = false) String appName ,@RequestParam(required = false) String os) {
		// 查询条件整理
		Date startTime = DateUtil.date1970();
		Date endTime = DateUtil.currentDate();
		String[] times = null;
		if (timeLimit != null && !timeLimit.equals("")) {
			times = timeLimit.split(" - ");
			if (times == null || times.length != 2) {
				return ResponseData.error(400, "时间参数错误:" + timeLimit);
			}
			startTime = DateUtil.parse(times[0], "yyyy-MM-dd", startTime);
			endTime = DateUtil.parse(times[1], "yyyy-MM-dd", endTime);
		}
		name = name == null || "".equals(name) ? "%" : "%" + name + "%";
		company = company == null || "".equals(company) ? "%" : company;
		channel = channel == null || "".equals(channel) ? "%" : channel;
		appName = appName == null || "".equals(appName) ? "%" : appName;
		os = os == null || "".equals(os) ? "%" : os;
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit(),
				Sort.by(Direction.DESC, "registerTime"));
		if (ShiroKit.isAdmin() && buckleStr != null && "扣量显示".equals(buckleStr.trim())) {

			// 合并相同日期的数据
			List<UserBuckleQuantityInfo> list = new ArrayList<UserBuckleQuantityInfo>();
			//总数
			Long total = 0L;
			
			List<Long> userIdList = JpaUserRepository.findMoreIdListByConditions(name, company);
			for (int k = 0; k < userIdList.size(); ++k) {
				
				Long currentTotal = (long) jpaAppUserAccountRepository.findCountInDateByGroupByDate(
						userIdList.get(k), channel, appName, startTime, endTime ,os).size();
				total += currentTotal;
				// 查找扣量数据
				List<UserBuckleQuantityInfo> buckleQuantityList = jpaAppUserAccountRepository
						.findBuckleQuantityInDate(userIdList.get(k), channel, appName, startTime, endTime,os, pageable);
				// 不扣量或默认扣量数据
				List<UserBuckleQuantityInfo> noBuckleQuantityInfoList = jpaAppUserAccountRepository
						.findNoBuckleQuantityInDate(userIdList.get(k), channel, appName, startTime, endTime,os, pageable);
				// 默认扣量规则
				List<DataRestricted> defaultDataRestrictedList = jpaDataRestrictedRepository.findAllDefault();
				// 指定时间内所有扣量规则
				List<DataRestricted> dataRestrictedList = jpaDataRestrictedRepository.findAllNotDefault();
				// 扣量计算
				buckleQuantity(buckleQuantityList, dataRestrictedList, defaultDataRestrictedList);
				buckleQuantity(noBuckleQuantityInfoList, null, defaultDataRestrictedList);

				// 合并相同日期的数据
				List<UserBuckleQuantityInfo> resList = noBuckleQuantityInfoList;
				start: for (int i = 0, len = buckleQuantityList.size(); i < len; ++i) {
					UserBuckleQuantityInfo iTmp = buckleQuantityList.get(i);
					for (int j = 0, jLen = resList.size(); j < jLen; ++j) {
						UserBuckleQuantityInfo jTmp = resList.get(j);
						if (iTmp.getDate() == null || !iTmp.getDate().equals(jTmp.getDate())) {
							continue;
						}
						if ((iTmp.getAppName() == null && iTmp.getAppName() != jTmp.getAppName())
								|| !iTmp.getAppName().equals(jTmp.getAppName())) {
							continue;
						}
						if ((iTmp.getChannelName() == null && iTmp.getChannelName() != jTmp.getChannelName())
								|| !iTmp.getChannelName().equals(jTmp.getChannelName())) {
							continue;
						}
						if ((iTmp.getCompany() == null && iTmp.getCompany() != jTmp.getCompany())
								|| !iTmp.getCompany().equals(jTmp.getCompany())) {
							continue;
						}
						if ((iTmp.getUserName() == null && iTmp.getUserName() != jTmp.getUserName())
								|| !iTmp.getUserName().equals(jTmp.getUserName())) {
							continue;
						}
						if ((iTmp.getOs() == null && iTmp.getOs() != jTmp.getOs())
								|| !iTmp.getOs().equals(jTmp.getOs())) {
							continue;
						}
						resList.get(j).setNumber(resList.get(j).getNumber() + buckleQuantityList.get(i).getNumber());
						continue start;
					}
					resList.add(buckleQuantityList.get(i));
				}

				list.addAll(resList);
			}

			Page<UserBuckleQuantityInfo> res = new Page<UserBuckleQuantityInfo>();
			res.setRecords(list);
			res.setTotal(total == null ? 0 : total);
			return LayuiPageFactory.createPageInfo(res);

		} else if (ShiroKit.isAdmin()) {
			// 管理员不扣量，只是使用以下该数据结构
			List<UserBuckleQuantityInfo> list = jpaAppUserAccountRepository.findStatistics(name, company, channel,
					appName, startTime, endTime, os,pageable);
			Long total = (long) jpaAppUserAccountRepository
					.findStatisticsCount(name, company,channel, appName,startTime, endTime ,os).size();
			Page<UserBuckleQuantityInfo> res = new Page<UserBuckleQuantityInfo>();
			res.setRecords(list);
			res.setTotal(total == null ? 0 : total);
			return LayuiPageFactory.createPageInfo(res);
		} else {
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
    			// 查找扣量数据
    			List<UserBuckleQuantityInfo> buckleQuantityList = jpaAppUserAccountRepository
    					.findBuckleQuantityInDate(userId, channel, appName, startTime, endTime, os,pageable);
    			// 不扣量或默认扣量数据
    			List<UserBuckleQuantityInfo> noBuckleQuantityInfoList = jpaAppUserAccountRepository
    					.findNoBuckleQuantityInDate(userId,channel, appName, startTime, endTime, os,pageable);
    			// 默认扣量规则
    			List<DataRestricted> defaultDataRestrictedList = jpaDataRestrictedRepository.findAllDefault();
    			// 指定时间内所有扣量规则
    			List<DataRestricted> dataRestrictedList = jpaDataRestrictedRepository.findAllNotDefault();
    			// 扣量计算
    			buckleQuantity(buckleQuantityList, dataRestrictedList, defaultDataRestrictedList);
    			buckleQuantity(noBuckleQuantityInfoList, null, defaultDataRestrictedList);

    			// 合并相同日期的数据
    			List<UserBuckleQuantityInfo> resList = noBuckleQuantityInfoList;
    			start: for (int i = 0, len = buckleQuantityList.size(); i < len; ++i) {
    				UserBuckleQuantityInfo iTmp = buckleQuantityList.get(i);
    				for (int j = 0, jLen = resList.size(); j < jLen; ++j) {
    					UserBuckleQuantityInfo jTmp = resList.get(j);
    					if (iTmp.getDate() == null || !iTmp.getDate().equals(jTmp.getDate())) {
    						continue;
    					}
    					if ((iTmp.getAppName() == null && iTmp.getAppName() != jTmp.getAppName())
    							|| !iTmp.getAppName().equals(jTmp.getAppName())) {
    						continue;
    					}
    					if ((iTmp.getChannelName() == null && iTmp.getChannelName() != jTmp.getChannelName())
    							|| !iTmp.getChannelName().equals(jTmp.getChannelName())) {
    						continue;
    					}
    					if ((iTmp.getCompany() == null && iTmp.getCompany() != jTmp.getCompany())
    							|| !iTmp.getCompany().equals(jTmp.getCompany())) {
    						continue;
    					}
    					if ((iTmp.getUserName() == null && iTmp.getUserName() != jTmp.getUserName())
    							|| !iTmp.getUserName().equals(jTmp.getUserName())) {
    						continue;
    					}
    					if ((iTmp.getOs() == null && iTmp.getOs() != jTmp.getOs())
    							|| !iTmp.getOs().equals(jTmp.getOs())) {
    						continue;
    					}
    					resList.get(j).setNumber(resList.get(j).getNumber() + buckleQuantityList.get(i).getNumber());
    					continue start;
    				}
    				resList.add(buckleQuantityList.get(i));
    			}
    			// 日期数量即当前查询总数
    			Long total = (long) jpaAppUserAccountRepository.findCountInDateByGroupByDate(
    					userId, channel, appName, startTime, endTime ,os).size();
    			Page<UserBuckleQuantityInfo> res = new Page<UserBuckleQuantityInfo>();
    			res.setRecords(resList);
    			res.setTotal(total == null ? 0 : total);
    			return LayuiPageFactory.createPageInfo(res);
    		}
    		else {
    			// 合并相同日期的数据
    			List<UserBuckleQuantityInfo> list = new ArrayList<UserBuckleQuantityInfo>();
    			//总数
    			Long total = 0L;
    			
    			List<Long> userIdList = jpaUserRoleRepository.findMoreUserIdByRoleIds(allRoleList);
    			for (int k = 0; k < userIdList.size(); ++k) {
    				
    				Long currentTotal = (long) jpaAppUserAccountRepository.findCountInDateByGroupByDate(
    						userIdList.get(k), channel, appName, startTime, endTime ,os).size();
    				total += currentTotal;
    				// 查找扣量数据
    				List<UserBuckleQuantityInfo> buckleQuantityList = jpaAppUserAccountRepository
    						.findBuckleQuantityInDate(userIdList.get(k), channel, appName, startTime, endTime,os, pageable);
    				// 不扣量或默认扣量数据
    				List<UserBuckleQuantityInfo> noBuckleQuantityInfoList = jpaAppUserAccountRepository
    						.findNoBuckleQuantityInDate(userIdList.get(k), channel, appName, startTime, endTime,os, pageable);
    				// 默认扣量规则
    				List<DataRestricted> defaultDataRestrictedList = jpaDataRestrictedRepository.findAllDefault();
    				// 指定时间内所有扣量规则
    				List<DataRestricted> dataRestrictedList = jpaDataRestrictedRepository.findAllNotDefault();
    				// 扣量计算
    				buckleQuantity(buckleQuantityList, dataRestrictedList, defaultDataRestrictedList);
    				buckleQuantity(noBuckleQuantityInfoList, null, defaultDataRestrictedList);

    				// 合并相同日期的数据
    				List<UserBuckleQuantityInfo> resList = noBuckleQuantityInfoList;
    				start: for (int i = 0, len = buckleQuantityList.size(); i < len; ++i) {
    					UserBuckleQuantityInfo iTmp = buckleQuantityList.get(i);
    					for (int j = 0, jLen = resList.size(); j < jLen; ++j) {
    						UserBuckleQuantityInfo jTmp = resList.get(j);
    						if (iTmp.getDate() == null || !iTmp.getDate().equals(jTmp.getDate())) {
    							continue;
    						}
    						if ((iTmp.getAppName() == null && iTmp.getAppName() != jTmp.getAppName())
    								|| !iTmp.getAppName().equals(jTmp.getAppName())) {
    							continue;
    						}
    						if ((iTmp.getChannelName() == null && iTmp.getChannelName() != jTmp.getChannelName())
    								|| !iTmp.getChannelName().equals(jTmp.getChannelName())) {
    							continue;
    						}
    						if ((iTmp.getCompany() == null && iTmp.getCompany() != jTmp.getCompany())
    								|| !iTmp.getCompany().equals(jTmp.getCompany())) {
    							continue;
    						}
    						if ((iTmp.getUserName() == null && iTmp.getUserName() != jTmp.getUserName())
    								|| !iTmp.getUserName().equals(jTmp.getUserName())) {
    							continue;
    						}
    						if ((iTmp.getOs() == null && iTmp.getOs() != jTmp.getOs())
    								|| !iTmp.getOs().equals(jTmp.getOs())) {
    							continue;
    						}
    						resList.get(j).setNumber(resList.get(j).getNumber() + buckleQuantityList.get(i).getNumber());
    						continue start;
    					}
    					resList.add(buckleQuantityList.get(i));
    				}

    				list.addAll(resList);
    			}

    			Page<UserBuckleQuantityInfo> res = new Page<UserBuckleQuantityInfo>();
    			res.setRecords(list);
    			res.setTotal(total == null ? 0 : total);
    			return LayuiPageFactory.createPageInfo(res);
    		}
			
		}

	}

	@RequestMapping("/buckle_quantity_index")
	public ModelAndView buckleQuantityIndex(ModelAndView mav) {
		mav.addObject("admin", ShiroKit.isAdmin());
		mav.addObject("companyList", jpaChannelMasterRepository.findAll());
		mav.setViewName("/modular/data_manage/flow_user_buckle_quantity/index.html");
		mav.addObject("appList", jpaApkInfoRepository.findAllGroupByName());
		mav.addObject("channelList", jpaBaseChannelRepository.findMoreByIdGrthan25());
		return mav;
	}

	/**
	 * 执行扣量计算
	 * 
	 * @param list                      扣量体
	 * @param dataRestrictedList        专属扣量列表
	 * @param defaultDataRestrictedList 默认扣量列表
	 */
	private void buckleQuantity(List<UserBuckleQuantityInfo> list, List<DataRestricted> dataRestrictedList,
			List<DataRestricted> defaultDataRestrictedList) {
		// 查找扣量规则，并扣量计算
		for (int i = 0, len = list.size(); i < len; ++i) {
			UserBuckleQuantityInfo ubqi = list.get(i);

			List<DataRestricted> tmp = new ArrayList<DataRestricted>();
			if (dataRestrictedList != null) {
				// 查找专属的扣量规则
				for (int j = 0, jLen = dataRestrictedList.size(); j < jLen; ++j) {
					DataRestricted dr = dataRestrictedList.get(j);
					if (dr.getRestrictedName().equals(ubqi.getUserUrlAsRestrictedName() + "")) {
						tmp.add(dr);
					}
				}
			}
			// 无扣量规则，无默认规则，跳出当次
			if (tmp.isEmpty() && (defaultDataRestrictedList == null || defaultDataRestrictedList.isEmpty())) {
				continue;
			}
			// 无扣量规则,使用默认
			if (tmp.isEmpty()) {
				tmp = defaultDataRestrictedList;
			}
			// 查找合适的规则
			long timestamp = DateUtil.parse(ubqi.getDate(), "yyyy-MM-dd").getTime();
			int index = 0;
			long startTimeDiff = timestamp - tmp.get(0).getStartTime().getTime();
			long endTimeDiff = timestamp - tmp.get(0).getEndTime().getTime();
			for (int j = 1, jLen = tmp.size(); j < jLen; ++j) {
				long startTimestamp = tmp.get(j).getStartTime().getTime();
				long endTimestamp = tmp.get(j).getEndTime().getTime();
				if (timestamp - startTimestamp < 0) {
					continue;
				}
				if (timestamp - startTimestamp <= startTimeDiff) {
					long etmp = timestamp - endTimestamp;
					if ((etmp <= 0 && (endTimeDiff > 0 || etmp > endTimeDiff)) || (etmp > 0 && etmp < endTimeDiff)) {
						index = j;
						startTimeDiff = timestamp - startTimestamp;
					}
				}
			}
			// 扣量
			DataRestricted d = tmp.get(index);
			ubqi.setNumber(d.buckleQuantity(ubqi.getNumber()));
		}
	}
}
