package cn.stylefeng.guns.modular.base.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public abstract class ControllerBase {
	

	@RequestMapping({"","/","/index"})
	public ModelAndView index(ModelAndView mav) {
		mav.setViewName(preffix() + "/index.html");
		return mav;
	}
	
	@RequestMapping("/toAddPage")
	public ModelAndView toAddPage(ModelAndView mav) {
		mav.setViewName(preffix() + "/add.html");
		return mav;
	}
	
	@RequestMapping("/toEditPage")
	public String toEditPage(@RequestParam(required=false)String id ,@RequestParam(required=false)String uuid) {
		return preffix() + "/edit.html";
	}
	
	@RequestMapping("/toDetailPage")
	public String toDetailPage(@RequestParam(required=false)String id ,@RequestParam(required=false)String uuid) {
		return preffix() + "/detail.html";
	}
	
	abstract protected String preffix();
}
