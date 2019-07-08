package cn.stylefeng.guns.modular.core.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.annotion.BussinessLog;
import cn.stylefeng.guns.core.common.constant.PageConstant;
import cn.stylefeng.guns.core.common.constant.PageConstant.PageInfo;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import cn.stylefeng.guns.core.controller.valid.ValidPart2;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.util.DateUtil;
import cn.stylefeng.guns.modular.base.controller.ControllerBase;
import cn.stylefeng.guns.modular.core.entity.ChannelMaster;
import cn.stylefeng.guns.modular.core.repository.JpaChannelMasterRepository;
import cn.stylefeng.guns.modular.system.repository.JpaUserRepository;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Controller
@RequestMapping("/channel_master")
public class ChannelMasterController extends ControllerBase {

	@Autowired
	private JpaChannelMasterRepository jpaChannelMasterRepository;
	
	@Autowired
	private JpaUserRepository jpaUserRepository;

	@Override
	protected String preffix() {
		return "/modular/core/channel_master/";
	}

	@ResponseBody
	@RequestMapping("/list")
	public Object list(@RequestParam(required = false) String name 
			, @RequestParam(required = false) String timeLimit ,@RequestParam(required = false)Integer page
			, @RequestParam(required = false)Integer limit) {
		// 拼接查询条件
		String startTime = null;
		String endTime = null;
		String[] times = null;
		if(timeLimit != null && !timeLimit.equals("")) {
			times = timeLimit.split(" - ");
			if(times == null || times.length != 2) {
				return ResponseData.error(400, "时间参数错误:" + timeLimit);
			}
			startTime = times[0];
			endTime = times[1];
		}
		name = name == null ? null : "%" + name + "%";
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit());
		
		List<ChannelMaster> list = jpaChannelMasterRepository.findPage(name ,startTime ,endTime ,pageable);
		Page<ChannelMaster> res = new Page<ChannelMaster>();
		res.setRecords(list);
		res.setTotal(jpaChannelMasterRepository.count());
		return LayuiPageFactory.createPageInfo(res);
	}
	
	@ResponseBody
	@BussinessLog("添加渠道")
	@RequestMapping("/add")
	public Object add(@Validated(ValidPart1.class)ChannelMaster channelMaster ,Errors error) {
		ShiroUser user = ShiroKit.getUser();
		//参数设置
		channelMaster.setCreateId(user.getId());
		channelMaster.setCreateName(user.getName());
		channelMaster.setCreateTime(DateUtil.currentDate());
		channelMaster.setUpdateTime(channelMaster.getCreateTime());
		channelMaster.setStatus(ChannelMaster.STATUS_NORMAL);
		channelMaster.setUuid(UUID.randomUUID().toString().replace("-", "").toUpperCase());
		
		jpaChannelMasterRepository.saveAndFlush(channelMaster);
		
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("编辑渠道")
	@RequestMapping("/edit")
	public Object edit(@Validated(ValidPart2.class)ChannelMaster channelMaster ,Errors error) {
		ChannelMaster old = jpaChannelMasterRepository.findOneById(channelMaster.getId());
		if(old == null) {
			return ResponseData.error("指定渠道不存在，渠道id：" + channelMaster.getId());
		}
		LogObjectHolder.me().set(old);
		
		String name = channelMaster.getName() == null ? old.getName() : channelMaster.getName();
		String status = channelMaster.getStatus() == null ? old.getStatus() : channelMaster.getStatus();
		jpaChannelMasterRepository.updateById(channelMaster.getId() ,name ,channelMaster.getDescription() 
				, channelMaster.getInfo() ,channelMaster.getMaster() ,channelMaster.getMasterContact() 
				, status ,DateUtil.currentDate());
		
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("编辑状态")
	@RequestMapping("/updateStatus")
	public Object updateStatusById(@Validated(ValidPart2.class)ChannelMaster channelMaster ,Errors error) {
		ChannelMaster old = jpaChannelMasterRepository.findOneById(channelMaster.getId());
		if(old == null) {
			return ResponseData.error("指定渠道不存在，渠道id：" + channelMaster.getId());
		}
		LogObjectHolder.me().set(old);
		jpaChannelMasterRepository.updateStatusById(channelMaster.getId(),channelMaster.getStatus() 
				, DateUtil.currentDate());
		
		if(!"正常".equals(channelMaster.getStatus())) {
			jpaUserRepository.updateStatusByCompany("LOCKED" ,old.getName());
		}
		else if("正常".equals(channelMaster.getStatus())) {
			jpaUserRepository.updateStatusByCompany("ENABLE" ,old.getName());
		}
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("删除渠道")
	@RequestMapping("/delete")
	public Object delete(@Validated(ValidPart2.class)ChannelMaster channelMaster ,Errors error) {
		ChannelMaster old = jpaChannelMasterRepository.findOneById(channelMaster.getId());
		if(old == null) {
			return ResponseData.error("指定渠道不存在，渠道id：" + channelMaster.getId());
		}
		LogObjectHolder.me().set(old);
		jpaChannelMasterRepository.delete(old);
		return ResponseData.success();
	}
	
	@ResponseBody
	@RequestMapping("/searchOne")
	public Object searchOne(@Validated(ValidPart2.class)ChannelMaster channelMaster ,Errors error) {
		ChannelMaster old = jpaChannelMasterRepository.findOneById(channelMaster.getId());
		if(old == null) {
			return ResponseData.error("指定渠道不存在，渠道id：" + channelMaster.getId());
		}
		return ResponseData.success(old);
	}
}
