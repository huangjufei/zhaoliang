package cn.stylefeng.guns.modular.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import cn.stylefeng.guns.core.controller.valid.ValidPart2;
import cn.stylefeng.guns.core.util.CollectionUtil;
import lombok.Data;

@Data
@Entity
@Table(name="apk_info")
public class ApkInfo {

	@Id
	@GeneratedValue
	@NotNull(message="{apkInfo.id.notnull}" ,groups = {ValidPart2.class})
	private Integer id;
	@Column(name="resources_id")
	private Long resourcesId;
	@NotNull(message="{apkInfo.name.notnull}" ,groups = {ValidPart1.class})
	@Length(min = 1 ,max = 255 ,message="{apkInfo.name.notnull}" ,groups= {ValidPart1.class})
	private String name;
	private String description;
	@NotNull(message="{apkInfo.os.notnull}" ,groups = {ValidPart1.class})
	private String os;
	private String channel;
	@Column(name="channel_name")
	private String channelName;
	@Column(name="status")
	private String status;
	@Column(name="create_time")
	private Date createTime;
	@Column(name="update_time")
	private Date updateTime;
	@Column(name="create_id")
	private Long createId;
	@Column(name="create_name")
	private String createName;
	
	static public boolean isIllegalOfOs(String os) {
		return !(isAndroid(os) || isIos(os));
	}
	
	static public boolean isAndroid(String os) {
		String[] androids = new String[] {"android" ,"ANDROID" ,"安卓"};
		return CollectionUtil.contains(androids, os);
	}
	
	static public boolean isIos(String os) {
		String[] ios = new String[] {"ios" ,"IOS" ,"苹果"};
		return CollectionUtil.contains(ios, os);
	}
}
