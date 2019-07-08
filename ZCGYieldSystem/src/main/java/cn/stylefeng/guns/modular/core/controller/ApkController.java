package cn.stylefeng.guns.modular.core.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.annotion.BussinessLog;
import cn.stylefeng.guns.core.common.constant.PageConstant;
import cn.stylefeng.guns.core.common.constant.StatusConstant;
import cn.stylefeng.guns.core.common.constant.PageConstant.PageInfo;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import cn.stylefeng.guns.core.controller.valid.ValidPart2;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.util.BeanUtil;
import cn.stylefeng.guns.core.util.DateUtil;
import cn.stylefeng.guns.core.util.resources.ResourceStore;
import cn.stylefeng.guns.modular.base.controller.ControllerBase;
import cn.stylefeng.guns.modular.core.entity.ApkInfo;
import cn.stylefeng.guns.modular.core.entity.Resources;
import cn.stylefeng.guns.modular.core.repository.JpaApkInfoRepository;
import cn.stylefeng.guns.modular.core.repository.JpaResourcesRepository;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Controller
@RequestMapping("/apk")
public class ApkController extends ControllerBase{
	@Autowired
	private JpaResourcesRepository jpaResourcesRepository;
	@Autowired
	private JpaApkInfoRepository jpaApkInfoRepository;
	@Autowired
	private ResourceStore resourceStore;

	@Override
	protected String preffix() {
		return "/modular/core/apk/";
	}

	
	@ResponseBody
	@RequestMapping("/list")
	public Object list(@RequestParam(required = false) String name 
			, @RequestParam(required = false)Integer page
			, @RequestParam(required = false)Integer limit) {
		name = name == null ? "%" : "%" + name + "%";
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit());
		
		List<ApkInfo> list = jpaApkInfoRepository.findPage(name, pageable);
		Page<ApkInfo> res = new Page<ApkInfo>();
		res.setRecords(list);
		res.setTotal(jpaApkInfoRepository.count());
		return LayuiPageFactory.createPageInfo(res);
	}
	
	@ResponseBody
	@BussinessLog("上传资源")
	@RequestMapping("/upload")
	public Object add(@Validated(ValidPart1.class)ApkInfo apkInfo, @RequestParam(required = true)MultipartFile file) {
		if(ApkInfo.isIllegalOfOs(apkInfo.getOs())) {
			return ResponseData.error("app平台只能为：android或ios ,os：" + apkInfo.getOs());
		}
		ShiroUser user = ShiroKit.getUser();
		ApkInfo old = jpaApkInfoRepository.findOneByNameAndOs(apkInfo.getName() ,apkInfo.getOs());
		if(old != null) {
			return ResponseData.error("更新资源已存在，资源名称：" + apkInfo.getName());
		}
		//存储app资源
		Resources resources = new Resources();
		//apk资源包标识
		resources.setTag("apk");
		String[] types = file.getContentType().split("/");
		resources.setType(types[0]);
		resources.setSubtype(types[1]);
		resources.setFileName(file.getOriginalFilename());
		resources.setSize(file.getSize());
		try {
			boolean b = resourceStore.saveResource(resources, file.getBytes());
			if(!b) {
				throw new IOException("上传文件失败");
			}
			//ios更新下载地址为plist
			if(ApkInfo.isIos(apkInfo.getOs())) {
				String ipaUrl = resourceStore.downloadUrl(resources);
				resources.setFileName(file.getName()+".plist");
				b = resourceStore.saveResource(resources, iosPlist(ipaUrl, apkInfo.getName()));
			}
			if(!b) {
				throw new IOException("上传文件失败");
			}
		} catch (IOException e) {
			return ResponseData.error(e.getMessage());
		}
		resources.setName(apkInfo.getName());
		resources.setDescription(apkInfo.getDescription());
		resources.setCreateId(user.getId());
		resources.setCreateName(user.getName());
		resources.setCreateTime(DateUtil.currentDate());
		resources.setUpdateTime(resources.getCreateTime());
		resources.setStatus(StatusConstant.SHELVES);
		if(resources.getUserKey() == null || "".equals(resources.getUserKey())) {
			resources.setUserKey(resources.getSysKey());
		}
		jpaResourcesRepository.saveAndFlush(resources);
		
		//存储app信息
		apkInfo.setResourcesId(resources.getId());
		apkInfo.setCreateId(user.getId());
		apkInfo.setCreateName(user.getName());
		apkInfo.setCreateTime(DateUtil.currentDate());
		apkInfo.setUpdateTime(apkInfo.getCreateTime());
		apkInfo.setStatus(StatusConstant.SHELVES);
		jpaApkInfoRepository.saveAndFlush(apkInfo);
		
		return ResponseData.success();
	}
	
	private String  iosPlist(String appDwonLoad ,String name ) {
		return "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\r\n" + 
				"<plist version=\"1.0\">\r\n" + 
				"<dict>\r\n" + 
				"<key>items</key>\r\n" + 
				"<array>\r\n" + 
				"<dict>\r\n" + 
				"<key>assets</key>\r\n" + 
				"<array>\r\n" + 
				"<dict>\r\n" + 
				"<key>kind</key>\r\n" + 
				"<string>software-package</string>\r\n" + 
				"<key>url</key>\r\n" + 
				"<string>" + appDwonLoad + "</string>\r\n" + 
				"</dict>\r\n" + 
				"</array>\r\n" + 
				"<key>metadata</key>\r\n" + 
				"<dict>\r\n" + 
				"<key>bundle-identifier</key>\r\n" + 
				"<string>MGXD.MGXD</string>\r\n" + 
				"<key>bundle-version</key>\r\n" + 
				"<string>1.0</string>\r\n" + 
				"<key>kind</key>\r\n" + 
				"<string>software</string>\r\n" + 
				"<key>subtitle</key>\r\n" + 
				"<string>" + name + "</string>\r\n" + 
				"<key>title</key>\r\n" + 
				"<string>" + name + "</string>\r\n" + 
				"</dict>\r\n" + 
				"</dict>\r\n" + 
				"</array>\r\n" + 
				"</dict>\r\n" + 
				"</plist>";
	}
	
	@ResponseBody
	@BussinessLog("更新app信息")
	@RequestMapping("/update")
	public Object update(@Validated(ValidPart2.class)ApkInfo apkInfo, Errors error 
			, @RequestParam(required = true)MultipartFile file) {
		if(ApkInfo.isIllegalOfOs(apkInfo.getOs())) {
			return ResponseData.error("app平台只能为：android或ios ,os：" + apkInfo.getOs());
		}
		
		ApkInfo old = jpaApkInfoRepository.findOneById(apkInfo.getId());
		if(old == null) {
			return ResponseData.error("更新app信息不存在，appId：" + apkInfo.getId());
		}
		ApkInfo other = jpaApkInfoRepository.findOneByNameAndOs(apkInfo.getName() ,apkInfo.getOs());
		if(other != null && other.getId() != old.getId()) {
			return ResponseData.error("更新app信息已存在，app名称：" + apkInfo.getName());
		}
		//app资源更新
		Resources resources = new Resources();
		Resources oldResources = jpaResourcesRepository.findOneById(old.getResourcesId());
		//保存
		String[] types = file.getContentType().split("/");
		resources.setType(types[0]);
		resources.setSubtype(types[1]);
		resources.setFileName(file.getOriginalFilename());
		resources.setSize(file.getSize());
		try {
			boolean b = resourceStore.saveResource(resources, file.getBytes());
			if(!b) {
				throw new IOException("上传文件失败");
			}
			//ios更新下载地址为plist
			if(ApkInfo.isIos(apkInfo.getOs())) {
				String ipaUrl = resourceStore.downloadUrl(resources);
				resources.setFileName(file.getName()+".plist");
				b = resourceStore.saveResource(resources, iosPlist(ipaUrl, apkInfo.getName()));
			}
			if(!b) {
				throw new IOException("上传文件失败");
			}
		} catch (IOException e) {
			return ResponseData.error(e.getMessage());
		}
		//删除
		resourceStore.delResource(oldResources);
		resources.setUpdateTime(DateUtil.currentDate());
		//更新db
		jpaResourcesRepository.updateById(oldResources.getId() ,apkInfo.getName() ,apkInfo.getDescription() 
				, resources.getDir() ,resources.getFileName() ,resources.getSign() ,resources.getSize() 
				, resources.getStoreType() ,resources.getSubtype() ,resources.getSysKey() ,resources.getTag() 
				, resources.getType() ,resources.getUpdateTime());
		//app信息更新
		jpaApkInfoRepository.updateById(old.getId() ,apkInfo.getName() ,apkInfo.getDescription() ,apkInfo.getChannel() 
				, apkInfo.getChannelName() ,DateUtil.currentDate() ,apkInfo.getOs());
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("编辑状态")
	@RequestMapping("/updateStatus")
	public Object updateStatusById(@Validated(ValidPart2.class)ApkInfo apkInfo, Errors error) {
		ApkInfo old = jpaApkInfoRepository.findOneById(apkInfo.getId());
		if(old == null) {
			return ResponseData.error("更新app信息不存在，appId：" + apkInfo.getId());
		}
		Resources oldResources = jpaResourcesRepository.findOneById(old.getResourcesId());
		LogObjectHolder.me().set(old);
		jpaResourcesRepository.updateStatusById(oldResources.getId() ,apkInfo.getStatus());
		jpaApkInfoRepository.updateStatusById(old.getId() ,apkInfo.getStatus());
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("删除渠道")
	@RequestMapping("/delete")
	public Object delete(@Validated(ValidPart2.class)ApkInfo apkInfo, Errors error) {
		ApkInfo old = jpaApkInfoRepository.findOneById(apkInfo.getId());
		if(old == null) {
			return ResponseData.error("删除app信息不存在，appId：" + apkInfo.getId());
		}
		Resources oldResources = jpaResourcesRepository.findOneById(old.getResourcesId());
		LogObjectHolder.me().set(old);
		//删除
		resourceStore.delResource(oldResources);
		jpaResourcesRepository.delete(oldResources);
		jpaApkInfoRepository.delete(old);
		return ResponseData.success();
	}
	
	@ResponseBody
	@RequestMapping("/searchOne")
	public Object searchOne(@Validated(ValidPart2.class)ApkInfo apkInfo, Errors error) {
		ApkInfo old = jpaApkInfoRepository.findOneById(apkInfo.getId());
		if(old == null) {
			return ResponseData.error("查询app信息不存在，appId：" + apkInfo.getId());
		}
		Resources oldResources = jpaResourcesRepository.findOneById(old.getResourcesId());
		Map<String ,Object> res = BeanUtil.toMap(old);
		res.putAll(BeanUtil.toMap(oldResources));
		res.put("downloadUrl", resourceStore.downloadUrl(oldResources));
		return ResponseData.success(res);
	}
	
	@ResponseBody
	@RequestMapping("/searchByChannel")
	public Object searchByChannel(@RequestParam(required = true)String channel) {
		List<ApkInfo> list = jpaApkInfoRepository.findMoreByChannel(channel);
		return ResponseData.success(list);
	}
}
