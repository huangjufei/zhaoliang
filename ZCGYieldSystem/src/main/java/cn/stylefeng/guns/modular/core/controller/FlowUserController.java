package cn.stylefeng.guns.modular.core.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.stylefeng.guns.core.common.annotion.BussinessLog;
import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import cn.stylefeng.guns.core.util.BeanUtil;
import cn.stylefeng.guns.core.util.CusAccessObjectUtil;
import cn.stylefeng.guns.core.util.DateUtil;
import cn.stylefeng.guns.core.util.resources.ResourceStore;
import cn.stylefeng.guns.modular.core.entity.ApkInfo;
import cn.stylefeng.guns.modular.core.entity.AppUserAccount;
import cn.stylefeng.guns.modular.core.entity.FlowUser;
import cn.stylefeng.guns.modular.core.entity.FlowUserVisitNumber;
import cn.stylefeng.guns.modular.core.entity.Resources;
import cn.stylefeng.guns.modular.core.repository.JpaAppUserAccountRepository;
import cn.stylefeng.guns.modular.core.repository.JpaFlowUserRepository;
import cn.stylefeng.guns.modular.core.repository.JpaFlowUserVisitNumberRepository;
import cn.stylefeng.guns.modular.core.repository.JpaResourcesRepository;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Controller
@RequestMapping("/fuser")
public class FlowUserController {

	@Autowired
	private JpaFlowUserRepository jpaFlowUserRepository;
	@Autowired
	private JpaResourcesRepository jpaResourcesRepository;
	@Autowired
	private ResourceStore resourceStore;
	@Autowired
	private JpaAppUserAccountRepository jpaAppUserAccountRepository;
	@Autowired
	private JpaFlowUserVisitNumberRepository jpaFlowUserVisitNumberRepository;
	
	final static private String PREFFIX = "/modular/core/flow_user/";
	
	//ip cookie固定名称
	final static private String IP_COOKIE_NAME = "IP_COOKIE_NAME";
	
	@RequestMapping("/register/{resourcesId}/{channel}/{recommendKey}")
	public ModelAndView toRegisterPage(@PathVariable("channel") String channel 
			, @PathVariable("recommendKey")String recommendKey 	
			, @PathVariable("resourcesId")Long resourcesId,ModelAndView mav ,HttpServletRequest req 
			, HttpServletResponse resp) {
		
		//ip记录
		boolean isRepeat = false;
		Cookie[] cookies = req.getCookies();
		if(cookies != null) {
			for(Cookie c : cookies) {
				if(IP_COOKIE_NAME.equals(c.getName())) {
					isRepeat = true;
				}
			}
		}
		
		//判断设备是否是ios,默认android
		String os = "android";
		if(req.getHeader("user-agent").contains("Mac OS")) {
			os = "ios";
		}
	
		//cookie为未记录用户来过
		if(!isRepeat) {
			//判断同一天ip
			String visitIpv4 = CusAccessObjectUtil.getIpAddress(req);
			String currentDate = DateUtil.format(DateUtil.currentDate(), "yyyy-MM-dd"); 
			FlowUserVisitNumber fuvn = jpaFlowUserVisitNumberRepository.findOneByIpv4AndVisitTime(visitIpv4 ,currentDate);
			if(fuvn == null) {
				fuvn = new FlowUserVisitNumber(0L ,visitIpv4 ,null ,DateUtil.currentDate(),channel ,recommendKey ,os);
				jpaFlowUserVisitNumberRepository.save(fuvn);
			}
			//返回ip cookie值
			Cookie cookie = new Cookie(IP_COOKIE_NAME, IP_COOKIE_NAME);
			cookie.setMaxAge(24 * 60 * 60);
			resp.addCookie(cookie);
		}
	
		Resources resources = jpaResourcesRepository.findOneById(resourcesId);
		mav.setViewName(PREFFIX + "register.html");
		mav.addObject("channel", channel);
		mav.addObject("appName", resources == null ? "" : resources.getName());
		mav.addObject("recommendKey", recommendKey);
		mav.addObject("resourcesId", resourcesId);
		return mav;
	} 
	
	
	
	
	
	
	
	
	@RequestMapping("/registerAgreement.html")
	public String registerAgreement() {
		return PREFFIX + "registerAgreement.html";
	}
	
	@ResponseBody
	@BussinessLog(value="h5用户注册" ,key="/register")
	@RequestMapping("/register")
	public Object register(@Validated(ValidPart1.class)FlowUser flowUser ,Errors errors ,HttpServletRequest req
			,@RequestParam(required = true)Long resourcesId) {
		Resources resources = jpaResourcesRepository.findOneById(resourcesId);
		//资源不存在
		if(resources == null) {
			return ResponseData.error(500, "下载资源不存在，请联系客户，资源id:" + resourcesId);
		}
		Map<String ,Object > res = new HashMap<String, Object>();
		String downloadUrl = null;
		//依据平台提供对应平台的下载地址
		if(ApkInfo.isIos(flowUser.getOs())) {
			List<Resources> list = jpaResourcesRepository.findMoreByName(resources.getName());
			String iosHeadUrl = "itms-services://?action=download-manifest&url=";
			for(Resources r : list) {
				if(r.getFileName().endsWith("plist")) {
					downloadUrl = iosHeadUrl + resourceStore.downloadUrl(r);
				}
			}
		}
		else {
			//如果注册平台非法，默认设置为安卓平台
			flowUser.setOs("android");
			downloadUrl = resourceStore.downloadUrl(resources);
		}
		res.put("downloadUrl", downloadUrl);
		
		FlowUser old = jpaFlowUserRepository.findOneByPhone(flowUser.getPhone());
		//已注册
		if(old != null) {
			//查询用户是否通过app注册
			List<AppUserAccount> appUserAccountList = jpaAppUserAccountRepository.findAllByMobilePhone(flowUser.getPhone());
			//用户已注册，不做处理
			if(appUserAccountList != null && !appUserAccountList.isEmpty()) {
				res.putAll(BeanUtil.toMap(old));
				return ResponseData.success(res);
			}
			//用户未注册，删除之前的推广码信息
			jpaFlowUserRepository.delete(old);
		}
		//获取ip地址
		String ipv4 = CusAccessObjectUtil.getIpAddress(req);
		flowUser.setRegisterIpv4(ipv4);
		flowUser.setCreateTime(DateUtil.currentDate());
		
		jpaFlowUserRepository.saveAndFlush(flowUser);
		
		//ip更新
		String visitIpv4 = req.getRemoteHost();
		String currentDate = DateUtil.format(DateUtil.currentDate(), "yyyy-MM-dd"); 
		FlowUserVisitNumber fuvn = jpaFlowUserVisitNumberRepository.findOneByIpv4AndVisitTime(visitIpv4 ,currentDate);
		if(fuvn != null) {
			jpaFlowUserVisitNumberRepository.updateOsById(fuvn.getId() ,flowUser.getOs());
		}
		
		res.putAll(BeanUtil.toMap(flowUser));
		//逻辑实现
		return ResponseData.success(res);
	}
}
