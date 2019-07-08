package cn.stylefeng.guns.modular.core.vo;

import java.util.Date;

import cn.stylefeng.guns.modular.core.entity.DataRestricted;
import lombok.Data;

@Data
public class DataRestrictedVo {
	private Integer id; 
	private String creator;
	private Date createTime ;
	private Date updateTime;
	//该字段为约束的key值，如推广员的id，或推广商的id、uuid等
	private String restrictedName;
	private Integer showMinData ;
	private Long showMaxData;
	private Integer ratioValue ;
	private Integer numberValue ;
	private String workRestrictedField ;
	private Date startTime;
	private Date endTime;
	private String info ;
	
	//推广专员推广链接----表示该扣量为链接设置的
	private String userUrl;
	private String channel;
	private String channelName;
	
	//推广专员信息
	private Long urlCreatorId;
	private String urlCreatorName;
	private String urlCreatorPhone;
	private String urlCreatorCompany;
	private String urlCreatorAccount;

	public DataRestrictedVo() {
		super();
	}

	public DataRestrictedVo(Integer id, String creator, Date createTime, Date updateTime, String restrictedName,
			Integer showMinData, Long showMaxData, Integer ratioValue, Integer numberValue, String workRestrictedField,
			Date startTime, Date endTime, String info, String userUrl) {
		super();
		this.id = id;
		this.creator = creator;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.restrictedName = restrictedName;
		this.showMinData = showMinData;
		this.showMaxData = showMaxData;
		this.ratioValue = ratioValue;
		this.numberValue = numberValue;
		DataRestricted dr = new DataRestricted();
		dr.setWorkRestrictedField(workRestrictedField);
		this.workRestrictedField = dr.isWorkNumber() ? "数量" : "百分比";
		this.startTime = startTime;
		this.endTime = endTime;
		this.info = info;
		this.userUrl = userUrl;
	}

	public DataRestrictedVo(Integer id, String creator, Date createTime, Date updateTime, String restrictedName,
			Integer showMinData, Long showMaxData, Integer ratioValue, Integer numberValue, String workRestrictedField,
			Date startTime, Date endTime, String info, String userUrl, String channel, String channelName,
			Long urlCreatorId, String urlCreatorName, String urlCreatorPhone, String urlCreatorCompany,
			String urlCreatorAccount) {
		super();
		this.id = id;
		this.creator = creator;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.restrictedName = restrictedName;
		this.showMinData = showMinData;
		this.showMaxData = showMaxData;
		this.ratioValue = ratioValue;
		this.numberValue = numberValue;
		DataRestricted dr = new DataRestricted();
		dr.setWorkRestrictedField(workRestrictedField);
		this.workRestrictedField = dr.isWorkNumber() ? "数量" : "百分比";
		this.startTime = startTime;
		this.endTime = endTime;
		this.info = info;
		this.userUrl = userUrl;
		this.channel = channel;
		this.channelName = channelName;
		this.urlCreatorId = urlCreatorId;
		this.urlCreatorName = urlCreatorName;
		this.urlCreatorPhone = urlCreatorPhone;
		this.urlCreatorCompany = urlCreatorCompany;
		this.urlCreatorAccount = urlCreatorAccount;
	}

	public DataRestrictedVo(Integer id, String creator, Date createTime, Date updateTime, String restrictedName,
			Integer showMinData, Long showMaxData, Integer ratioValue, Integer numberValue, String workRestrictedField,
			Date startTime, Date endTime, String info) {
		this.id = id;
		this.creator = creator;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.restrictedName = restrictedName;
		this.showMinData = showMinData;
		this.showMaxData = showMaxData;
		this.ratioValue = ratioValue;
		this.numberValue = numberValue;
		this.workRestrictedField = workRestrictedField;
		this.startTime = startTime;
		this.endTime = endTime;
		this.info = info;
	}
	
	
}
