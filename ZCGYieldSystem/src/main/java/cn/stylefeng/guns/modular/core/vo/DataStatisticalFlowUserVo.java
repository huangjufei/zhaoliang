package cn.stylefeng.guns.modular.core.vo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cn.stylefeng.guns.modular.core.entity.AppUserAccount;
import lombok.Data;

@Data
public class DataStatisticalFlowUserVo {

	private String date;
	private Long flowUserCount = Long.valueOf(0L);
	private Long appUserCount = Long.valueOf(0L);
	private Long dateConversionNumber = Long.valueOf(0L);
	private Long registerConversionNumber = Long.valueOf(0L);
	private String dateConversionRate = "0.0%";
	private  String registerConversionRate = "0.0%";
	private String appName;
	private String channelName;
	private String company;
	private String userName;
	private String os;
	
	//独立访问数
	private Long flowUserVisitCount = 0L;
	
	//app浏览数
	private Long appBrowseCount = 0L;
	private String appBrowseRate = "0.0%";
	
	//app用户注册列表
	private Map<String ,AppUserAccount> appUserMap = new HashMap<String ,AppUserAccount>();
	
	public DataStatisticalFlowUserVo(String date, Long flowUserCount) {
		this.date = date;
		this.flowUserCount = flowUserCount;
	}
	
	public void calculateRate() {
		this.dateConversionRate = decimal3(Double.valueOf(dateConversionNumber) / flowUserCount * 100);
		this.registerConversionRate = decimal3(Double.valueOf(registerConversionNumber) / flowUserCount * 100);
		if(appBrowseCount != null && appUserCount != null && appBrowseCount != 0 && appUserCount != 0) {
			this.appBrowseRate = decimal3(Double.valueOf(appBrowseCount) / appUserCount * 100);
		}
	}
	
	public void calculateRate2() {
		this.dateConversionRate = decimal3(Double.valueOf(appUserCount) / flowUserCount * 100);
		
		this.registerConversionRate = decimal3(Double.valueOf(registerConversionNumber) / flowUserCount * 100);
		
		if(appBrowseCount != null && appUserCount != null && appBrowseCount != 0 && appUserCount != 0) {
			this.appBrowseRate = decimal3(Double.valueOf(appBrowseCount) / appUserCount * 100);
		}
	}
	
	
	private String decimal3(Double f) {
		 return new BigDecimal(f).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "%";
	}
}
