package cn.stylefeng.guns.modular.core.vo;

import java.util.Date;

import lombok.Data;

@Data
public class DataFlowUserVo {
	private Long id;
	private String phone;
	private String recommendKey;
	private String channel;
	private Date createTime;
	private String registerIpv4;
	private String os;
	
	//user info
	private Long userId;
	private String userName;

	//company info
	private String company;
	
	//channel name
	private String channelName;
	
	//是否转化
	private String isConversion = "否";
	//转化时间
	private Date conversionTime;
	
	//app info
	private Integer appId;
	private String appName;
	private Long flowUserCount;
	

	public DataFlowUserVo(Long id, String phone, String recommendKey, String channel, Date createTime,
			String registerIpv4, String os, Long userId, String userName, String company,
			String channelName) {
		super();
		this.id = id;
		this.phone = phone;
		this.recommendKey = recommendKey;
		this.channel = channel;
		this.createTime = createTime;
		this.registerIpv4 = registerIpv4;
		this.os = os;
		this.userId = userId;
		this.userName = userName;
		this.company = company;
		this.channelName = channelName;
	}

	public DataFlowUserVo() {
		super();
	}

	public DataFlowUserVo(Long id, String phone, String recommendKey, String channel, Date createTime,
			String registerIpv4, String os, Long userId, String userName, String company, String channelName,
			Date conversionTime) {
		super();
		this.id = id;
		this.phone = phone;
		this.recommendKey = recommendKey;
		this.channel = channel;
		this.createTime = createTime;
		this.registerIpv4 = registerIpv4;
		this.os = os;
		this.userId = userId;
		this.userName = userName;
		this.company = company;
		this.channelName = channelName;
		this.conversionTime = conversionTime;
		this.isConversion = conversionTime == null ? "否" : "是";
	}

	public DataFlowUserVo(Long id, String phone, String recommendKey, String channel, Date createTime,
			String registerIpv4, String os, Long userId, String userName, String company, String channelName
			,Integer appId, String appName) {
		super();
		this.id = id;
		this.phone = phone;
		this.recommendKey = recommendKey;
		this.channel = channel;
		this.createTime = createTime;
		this.registerIpv4 = registerIpv4;
		this.os = os;
		this.userId = userId;
		this.userName = userName;
		this.company = company;
		this.channelName = channelName;
		this.appId = appId;
		this.appName = appName;
	}
	
	public DataFlowUserVo(Long id, String phone, String recommendKey, String channel, Date createTime,
			String registerIpv4, String os, Long userId, String userName, String company, String channelName
			,Integer appId, String appName,Long flowUserCount) {
		super();
		this.id = id;
		this.phone = phone;
		this.recommendKey = recommendKey;
		this.channel = channel;
		this.createTime = createTime;
		this.registerIpv4 = registerIpv4;
		this.os = os;
		this.userId = userId;
		this.userName = userName;
		this.company = company;
		this.channelName = channelName;
		this.appId = appId;
		this.appName = appName;
		this.flowUserCount = flowUserCount;
	}
	
	
}
