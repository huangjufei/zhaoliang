package cn.stylefeng.guns.modular.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import lombok.Data;

@Data
@Entity
@Table(name="user_url")
public class UserUrl {
	@Id
	@GeneratedValue
	private Long id;
	@Column(name="user_id")
	private Long userId;
	@NotNull(message="{userUser.appId.notnull}" ,groups= {ValidPart1.class})
	@Column(name="app_id")
	private Integer appId;
	@Column(name="recommend_key")
	private String recommendKey;
	@NotNull(message="{userUser.channel.notnull}" ,groups= {ValidPart1.class})
	private String channel;
	@Column(name="channel_name")
	@NotNull(message="{userUser.channelName.notnull}" ,groups= {ValidPart1.class})
	private String channelName;
	@Column(name="url")
	private String url;
	@Column(name="create_time")
	private Date createTime;
}
