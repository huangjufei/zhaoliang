package cn.stylefeng.guns.modular.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import cn.stylefeng.guns.core.controller.valid.ValidPart2;
import lombok.Data;

@Data
@Entity
@Table(name="channel_master")
public class ChannelMaster {
	@Id
	@GeneratedValue
	@NotNull(message="{channelMaster.name.notnull}" ,groups = {ValidPart2.class})
	private Integer id;
	private String uuid;
	@NotNull(message="{channelMaster.name.notnull}" ,groups = {ValidPart1.class})
	private String name;
	private String description;
	private String master;
	@Column(name="master_contact")
	private String masterContact;
	private String info;
	/**
	 * 	状态
	 * 	正常：
	 * 	禁用：不可使用，不能登录
	 */
	private String status;
	@Column(name="create_time")
	private Date createTime;
	@Column(name="update_time")
	private Date updateTime;
	@Column(name="create_id")
	private Long createId;
	@Column(name="create_name")
	private String createName;
	
	static final public String STATUS_NORMAL = "正常";
	static final public String STATUS_FORBID = "禁止";
}
