package cn.stylefeng.guns.modular.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="t_base_app")
public class AppInfo {

	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="template_id")
	private Integer templateId;
	private String name;
	@Column(name="app_key")
	private String appKey;
	private short type;
	private short status;
	@Column(name="public_key")
	private String publicKey; 
	@Column(name="private_key")
	private String privateKey;
	@Column(name="app_public_key")
	private String appPublicKey;
	@Column(name="app_private_key")
	private String appPrivateKey;
	private String remark;
	private Integer modifier;
	@Column(name="update_time")
	private Date updateTime;
	private Integer creator;
	@Column(name="creater_time")
	private String createrTime;
	@Column(name="jpush_mastersecret")
	private String jpushMastersecret;
	@Column(name="jpush_appKey")
	private String jpushApppkey;
	public AppInfo(Integer id, Integer templateId, String name, String appKey, short type, short status,
			String publicKey, String privateKey, String appPublicKey, String appPrivateKey, String remark,
			Integer modifier, Date updateTime, Integer creator, String createrTime, String jpushMastersecret,
			String jpushApppkey) {
		this.id = id;
		this.templateId = templateId;
		this.name = name;
		this.appKey = appKey;
		this.type = type;
		this.status = status;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.appPublicKey = appPublicKey;
		this.appPrivateKey = appPrivateKey;
		this.remark = remark;
		this.modifier = modifier;
		this.updateTime = updateTime;
		this.creator = creator;
		this.createrTime = createrTime;
		this.jpushMastersecret = jpushMastersecret;
		this.jpushApppkey = jpushApppkey;
	}
	public AppInfo() {
	}
	
}
