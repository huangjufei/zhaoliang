package cn.stylefeng.guns.modular.base.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.core.common.annotion.BussinessLog;
import cn.stylefeng.guns.core.common.constant.PageConstant;
import cn.stylefeng.guns.core.common.constant.PageConstant.PageInfo;
import cn.stylefeng.guns.core.common.constant.StatusConstant;
import cn.stylefeng.guns.core.common.page.LayuiPageFactory;
import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import cn.stylefeng.guns.core.log.LogObjectHolder;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.util.DateUtil;
import cn.stylefeng.guns.core.util.resources.ResourceStore;
import cn.stylefeng.guns.modular.core.entity.Resources;
import cn.stylefeng.guns.modular.core.repository.JpaResourcesRepository;
import cn.stylefeng.roses.core.reqres.response.ResponseData;

@Controller
@RequestMapping("/resources")
public class ResourcesController extends ControllerBase{
	
	@Autowired
	private JpaResourcesRepository jpaResourcesRepository;
	@Autowired
	private ResourceStore resourceStore;

	@Override
	protected String preffix() {
		return "/modular/base/resources/";
	}

	
	@ResponseBody
	@RequestMapping("/list")
	public Object list(@RequestParam(required = false) String name 
			, @RequestParam(required = false) String tag ,@RequestParam(required = false)Integer page
			, @RequestParam(required = false)Integer limit) {
		name = name == null ? null : "%" + name + "%";
		tag = tag == null ? null : "%" + tag + "%";
		PageInfo pageInfo = PageConstant.pageInfo(page, limit);
		Pageable pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getLimit());
		
		List<Resources> list = jpaResourcesRepository.findPage(name ,tag,pageable);
		Page<Resources> res = new Page<Resources>();
		res.setRecords(list);
		res.setTotal(jpaResourcesRepository.count());
		return LayuiPageFactory.createPageInfo(res);
	}
	
	@ResponseBody
	@BussinessLog("上传资源")
	@RequestMapping("/upload")
	public Object add(Resources resources, @RequestParam(required = true)MultipartFile file) {
		if(resources.getUserKey() != null && !"".equals(resources.getUserKey())) {
			Resources old = jpaResourcesRepository.findOneByUserKey(resources.getUserKey());
			if(old != null ) {
				return ResponseData.error("key值以存在，请从新设置,key： " + resources.getUserKey());
			}
		}
		String[] types = file.getContentType().split(",");
		resources.setType(types[0]);
		resources.setSubtype(types[1]);
		resources.setFileName(file.getOriginalFilename());
		resources.setSize(file.getSize());
		resources.setSysKey(UUID.randomUUID().toString().replace("-", "").toUpperCase());
		try {
			resourceStore.saveResource(resources, file.getBytes());
		} catch (IOException e) {
			return ResponseData.error("未知异常，资源上传失败，请重试");
		}
		ShiroUser user = ShiroKit.getUser();
		resources.setCreateId(user.getId());
		resources.setCreateName(user.getName());
		resources.setCreateTime(DateUtil.currentDate());
		resources.setUpdateTime(resources.getCreateTime());
		resources.setStatus(StatusConstant.SHELVES);
		
		if(resources.getUserKey() == null || "".equals(resources.getUserKey())) {
			resources.setUserKey(resources.getSysKey());
		}
		
		jpaResourcesRepository.save(resources);
		
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("更新资源")
	@RequestMapping("/update")
	public Object update(@Validated(ValidPart1.class)Resources resources, Errors error 
			, @RequestParam(required = true)MultipartFile file) {
		Resources old = jpaResourcesRepository.findOneByUserKey(resources.getUserKey());
		if(old == null) {
			return ResponseData.error("更新资源不存在，资源key：" + resources.getUserKey());
		}
		//保存
		String[] types = file.getContentType().split(",");
		resources.setType(types[0]);
		resources.setSubtype(types[1]);
		resources.setFileName(file.getOriginalFilename());
		resources.setSize(file.getSize());
		resources.setSysKey(UUID.randomUUID().toString().replace("-", "").toUpperCase());
		try {
			resourceStore.saveResource(resources, file.getBytes());
		} catch (IOException e) {
			return ResponseData.error("未知异常，资源上传失败，请重试");
		}
		//删除
		resourceStore.delResource(old);
		
		resources.setUpdateTime(DateUtil.currentDate());
		//更新db
		jpaResourcesRepository.updateById(old.getId() ,resources.getName() ,resources.getDescription() 
				, resources.getDir() ,resources.getFileName() ,resources.getSign() ,resources.getSize() 
				, resources.getStoreType() ,resources.getSubtype() ,resources.getSysKey() ,resources.getTag() 
				, resources.getType() ,resources.getUpdateTime());
		
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("编辑状态")
	@RequestMapping("/updateStatus")
	public Object updateStatusById(@Validated(ValidPart1.class)Resources resources, Errors error) {
		Resources old = jpaResourcesRepository.findOneByUserKey(resources.getUserKey());
		if(old == null) {
			return ResponseData.error("更新资源不存在，资源key：" + resources.getUserKey());
		}
		LogObjectHolder.me().set(old);
		jpaResourcesRepository.updateStatusById(old.getId() ,resources.getStatus());
		return ResponseData.success();
	}
	
	@ResponseBody
	@BussinessLog("删除渠道")
	@RequestMapping("/delete")
	public Object delete(@Validated(ValidPart1.class)Resources resources, Errors error) {
		Resources old = jpaResourcesRepository.findOneByUserKey(resources.getUserKey());
		if(old == null) {
			return ResponseData.error("删除资源不存在，资源key：" + resources.getUserKey());
		}
		LogObjectHolder.me().set(old);
		//删除
		resourceStore.delResource(old);
		jpaResourcesRepository.delete(old);
		return ResponseData.success();
	}
	
	@ResponseBody
	@RequestMapping("/searchOne")
	public Object searchOne(@Validated(ValidPart1.class)Resources resources, Errors error) {
		Resources old = jpaResourcesRepository.findOneByUserKey(resources.getUserKey());
		if(old == null) {
			return ResponseData.error("查找资源不存在，资源key：" + resources.getUserKey());
		}
		return ResponseData.success(old);
	}
	
	@RequestMapping("/download/{userKey}")
	public void download(@PathVariable("userKey")String userKey ,HttpServletResponse resp) throws IOException {
		Resources old = jpaResourcesRepository.findOneByUserKey(userKey);
		if(old == null) {
			IOUtils.write(("删除资源不存在，资源key：" + userKey), resp.getOutputStream());
			return;
		}
		resp.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(old.getFileName(), "UTF-8"));
		byte[] res = resourceStore.findResource(old);
		IOUtils.write(res, resp.getOutputStream());
	}
}
