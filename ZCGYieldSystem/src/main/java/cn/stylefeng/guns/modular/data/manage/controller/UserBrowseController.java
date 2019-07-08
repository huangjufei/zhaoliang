package cn.stylefeng.guns.modular.data.manage.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.stylefeng.guns.core.util.DateUtil;
import cn.stylefeng.guns.modular.base.controller.ControllerBase;
import cn.stylefeng.guns.modular.core.entity.AppInfo;
import cn.stylefeng.guns.modular.core.entity.BaseChannel;
import cn.stylefeng.guns.modular.core.entity.Template;
import cn.stylefeng.guns.modular.core.repository.JpaAppInfoRepository;
import cn.stylefeng.guns.modular.core.repository.JpaBaseChannelRepository;
import cn.stylefeng.guns.modular.core.repository.JpaTemplateRepository;
import cn.stylefeng.guns.modular.core.vo.UserBrowseRecordVo;
import cn.stylefeng.guns.modular.data.manage.service.UserBrowseService;

@Controller
@RequestMapping("/data/userBrowse")
public class UserBrowseController  extends ControllerBase{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserBrowseService userBrowseService;
	
	@Override
	public ModelAndView index(ModelAndView mav) {
		List<Template> templateList = userBrowseService.findAllTemplate();
		
		if(Objects.nonNull(templateList) && !templateList.isEmpty()) {
			int allAppVersionBrowseCountNumber = userBrowseService.findAllUserBrowseCountNumber();
			
			mav.addObject("allAppVersionBrowseCountNumber", allAppVersionBrowseCountNumber);
			mav.addObject("templateList", templateList);
			mav.addObject("channelList", userBrowseService.findAllBaseChannel());
		}
		return super.index(mav);
	}
	
	@ResponseBody
	@RequestMapping(value = "/appBrowseByAppId.html" ,method=RequestMethod.GET ,produces={"text/html;charset=UTF-8;","application/json;"})
	public Map<String ,Object> getUserBrowseByAppId(Integer appId) {
		List<UserBrowseRecordVo> userBrowse = userBrowseService.appBrowseRecordForList(appId);
		int currentAppVersionBrowseCountNumber = userBrowseService.findUserBrowseCountNumberByAppId(appId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("browseData", userBrowse);
		result.put("browseNumber", currentAppVersionBrowseCountNumber);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getAppVersion.html" ,method=RequestMethod.GET ,produces={"text/html;charset=UTF-8;","application/json;"})
	public List<AppInfo> getAppVersionByTemplateId(int templateId) {
		return userBrowseService.getAppVersionByTemplateId(templateId);
	}
	
	@ResponseBody
	@RequestMapping(value="/searchBrowse.html" ,method=RequestMethod.GET ,produces={"text/html;charset=UTF-8;","application/json;"})
	public Map<String ,Object> searchBrowse(Long appId ,String startDate ,String endDate ,String templateIdStr ,String channel) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(Objects.isNull(templateIdStr) || "".equals(templateIdStr.trim()) || templateIdStr.indexOf("_") == -1) {
			result.put("error", "templateId 不能为空或-1");
			return result;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date sDate = null;
		Date eDate = null;
		try {
			sDate = sdf.parse(startDate);
		} catch (ParseException e) {
		}
		try {
			eDate = sdf.parse(endDate);
		} catch (ParseException e) {
		}
		
		List<UserBrowseRecordVo> userBrowse = null;
		if(Objects.isNull(appId) || -1 == appId) {
			String[] temps = templateIdStr.substring(0, templateIdStr.length()-1).split("_");
			int[] templateIds = new int[temps.length];
			for(int i = 0 ;i < temps.length ;++i) {
				try {
					templateIds[i] = Integer.parseInt(temps[i]);
				} catch (Exception e) {
				}
			}
			logger.debug(Arrays.toString(templateIds));
			
			if(null == channel || "-1".equals(channel)) {
				userBrowse = userBrowseService.searchBrowseByTemplateIds(templateIds ,sDate ,eDate);
			}else {
				userBrowse = userBrowseService.searchBrowseByTemplateIds(templateIds ,sDate ,eDate ,channel);
			}
		}
		else {
			if(null == channel || "-1".equals(channel)) {
				userBrowse = userBrowseService.searchBrowse(appId ,sDate ,eDate);
			}
			else {
				userBrowse = userBrowseService.searchBrowse(appId ,sDate ,eDate ,channel);
			}
		}
		
		int currentSearchConditionBrowseCountNumber = userBrowseService.calculateBrowseCountNumber(userBrowse);
		
		int currentAppVersionBrowseCountNumber = null == channel || "-1".equals(channel) 
				? userBrowseService.findUserBrowseCountNumberByAppId(appId)
						: userBrowseService.findUserBrowseCountNumberByAppIdAndChannel(appId ,channel);
		
		result.put("browseData", userBrowse);
		result.put("browseNumber", currentAppVersionBrowseCountNumber);
		result.put("searchConditonBrowseNumber", currentSearchConditionBrowseCountNumber);
		return result;
	}
	
	@Autowired
	private JpaBaseChannelRepository jpaBaseChannelRepository;
	
	@Autowired
	private JpaTemplateRepository jpaTemplate;
	
	@Autowired
	private JpaAppInfoRepository appMapper;
	
	@RequestMapping(value="/exportExcel.xls" ,method=RequestMethod.GET ,produces={"text/html;charset=UTF-8;","application/json;"})
	public void exportExcel(Long appId ,String startDate ,String endDate ,String templateIdStr ,String channel ,HttpServletResponse resp) throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(Objects.isNull(templateIdStr) || "".equals(templateIdStr.trim()) || templateIdStr.indexOf("_") == -1) {
			IOUtils.write("templateId 不能为空或-1", resp.getWriter());
		}
		
		result = searchBrowse(appId, startDate, endDate, templateIdStr, channel);
		
		//创建excel表格
		@SuppressWarnings("resource")
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		setNameAndValue(sheet ,"APP模板",findTemplateNames(templateIdStr) ,0 ,0);
		setNameAndValue(sheet, "APP版本", findApp(appId), 1, 0);
		setNameAndValue(sheet, "渠道", findChannelName(channel), 2, 0);
		setNameAndValue(sheet, "时间", 
				(startDate== null || "".equals(startDate)?"1970":startDate) + 
				" 至 " + (endDate == null || "".equals(endDate)? DateUtil.format(new Date()) : endDate), 3, 0);
		setNameAndValue(sheet, "总数", (Integer)result.get("searchConditonBrowseNumber") , 4, 0);
		@SuppressWarnings("unchecked")
		List<UserBrowseRecordVo> userBrowse = (List<UserBrowseRecordVo>) result.get("browseData");
		for (int i = 0 ,len = userBrowse.size(); i < len; i++) {
			UserBrowseRecordVo t = userBrowse.get(i);
			setNameAndValue(sheet,t.getName() ,t.getCount(), i+5, 0);
		}
		OutputStream os = resp.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	}
	
	private String findChannelName(String channel) {
		try {
			BaseChannel bc = jpaBaseChannelRepository.findOneById(Integer.parseInt(channel));
			return bc.getName();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	private void setNameAndValue(HSSFSheet sheet ,String name ,String value ,int row ,int col) {
		HSSFRow rows = sheet.createRow(row);
		rows.createCell(col).setCellValue(name);;
		rows.createCell(col+1).setCellValue(value);
	}
	
	private void setNameAndValue(HSSFSheet sheet ,String name ,Integer value ,int row ,int col) {
		HSSFRow rows = sheet.createRow(row);
		rows.createCell(col).setCellValue(name);;
		rows.createCell(col+1).setCellValue(value);
	}
	
	private String findTemplateNames(String templateIdStr) {
		String result = "";
		String[] temps = templateIdStr.substring(0, templateIdStr.length()-1).split("_");
		for(int i = 0 ;i < temps.length ;++i) {
			try {
				Template template = jpaTemplate.findOneById(Integer.parseInt(temps[i]));
				result = result + "_" + template.getName();
			} catch (Exception e) {
			}
		}
		return result;
	}
	
	private String findApp(Long appid) {
		try {
			int id = Integer.parseInt(appid+"");
			AppInfo app = appMapper.findOneById(id);
			return app.getName();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	@ResponseBody
	@RequestMapping("/getBaseChannel.html")
	public List<BaseChannel> getBaseChannel() {
		return userBrowseService.findAllBaseChannel();
	}

	@Override
	protected String preffix() {
		return "/modular/data_manage/user_browse/";
	}
	
}
