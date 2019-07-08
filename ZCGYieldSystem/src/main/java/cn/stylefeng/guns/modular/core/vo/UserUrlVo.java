package cn.stylefeng.guns.modular.core.vo;

import java.util.Date;

import lombok.Data;

@Data
public class UserUrlVo {
	private Long id;
	private Long userId;
	private Integer appId;
	private String recommendKey;
	private String channel;
	private String channelName;
	private String url;
	private Date createTime;
	
	private String appName;
	private String userName;
	private String company;
	public UserUrlVo(Long id, Long userId, Integer appId, String recommendKey, String channel, String channelName,
			String url, Date createTime, String appName, String userName, String company) {
		super();
		this.id = id;
		this.userId = userId;
		this.appId = appId;
		this.recommendKey = recommendKey;
		this.channel = channel;
		this.channelName = channelName;
		this.url = url;
		this.createTime = createTime;
		this.appName = appName;
		this.userName = userName;
		this.company = company;
	}
	public UserUrlVo() {
		super();
	}
	
}
