package cn.stylefeng.guns.modular.core.vo;


import lombok.Data;

@Data
public class UserBuckleQuantityInfo {
	private String appName;
	private String channelName;
	private String company;
	//推广专员
	private String userName;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 日期的注册数量
	 */
	private Long number;
	private String os;
	/**
	 * 推广链接id对应扣量的关键字RestrictedName
	 */
	private Long userUrlAsRestrictedName;
	public UserBuckleQuantityInfo(String date, Long number, Long userUrlAsRestrictedName) {
		super();
		this.date = date;
		this.number = number;
		this.userUrlAsRestrictedName = userUrlAsRestrictedName;
	}
	public UserBuckleQuantityInfo(String date, Long number) {
		super();
		this.date = date;
		this.number = number;
	}
	public UserBuckleQuantityInfo() {
		super();
	}
	public UserBuckleQuantityInfo(String channelName, String company, String userName, String date, Long number) {
		super();
		this.channelName = channelName;
		this.company = company;
		this.userName = userName;
		this.date = date;
		this.number = number;
	}
	public UserBuckleQuantityInfo(String appName, String channelName, String company, String userName, String date,
			Long number) {
		this.appName = appName;
		this.channelName = channelName;
		this.company = company;
		this.userName = userName;
		this.date = date;
		this.number = number;
	}
	public UserBuckleQuantityInfo(String appName, String channelName, String company, String userName, String date,
			Long number, Long userUrlAsRestrictedName) {
		this.appName = appName;
		this.channelName = channelName;
		this.company = company;
		this.userName = userName;
		this.date = date;
		this.number = number;
		this.userUrlAsRestrictedName = userUrlAsRestrictedName;
	}
	public UserBuckleQuantityInfo(String appName, String channelName, String company, String userName, String date,
			Long number, String os ) {
		super();
		this.appName = appName;
		this.channelName = channelName;
		this.company = company;
		this.userName = userName;
		this.date = date;
		this.number = number;
		this.os = os;
	}
	public UserBuckleQuantityInfo(String appName, String channelName, String company, String userName, String date,
			Long number, String os, Long userUrlAsRestrictedName) {
		super();
		this.appName = appName;
		this.channelName = channelName;
		this.company = company;
		this.userName = userName;
		this.date = date;
		this.number = number;
		this.os = os;
		this.userUrlAsRestrictedName = userUrlAsRestrictedName;
	}
	
	
}
