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
@Table(name="resources")
public class Resources {
	@Id
	@GeneratedValue
	@NotNull(message="{resources.id.notnull}" ,groups = {ValidPart2.class})
	private Long id;
	@Column(name="user_key")
	@NotNull(message="{resources.userKey.notnull}" ,groups = {ValidPart1.class})
	private String userKey;
	@Column(name="sys_key")
	private String sysKey;
	private String name;
	private String description;
	private String dir;
	private String sign;
	@Column(name="file_name")
	private String fileName;
	private String type;
	private String subtype;
	private Long size;
	@Column(name="store_type")
	private String storeType;
	private String tag;
	private String status;
	@Column(name="create_time")
	private Date createTime;
	@Column(name="update_time")
	private Date updateTime;
	@Column(name="create_id")
	private Long createId;
	@Column(name="create_name")
	private String createName;
}
