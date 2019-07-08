package cn.stylefeng.guns.modular.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import cn.stylefeng.guns.core.controller.valid.ValidPart1;
import lombok.Data;

@Data
@Entity
@Table(name="flow_user")
public class FlowUser {
	
	@Id
	@GeneratedValue
	private Long id;
	/**
	 * 注册手机号码，不可为空
	 */
	@NotNull(message= "{flowUser.phone.notnull}" ,groups= {ValidPart1.class})
	@Pattern(message= "{flowUser.phone.format}" ,regexp="^1[3456789]\\d{9}$" ,groups = {ValidPart1.class})
	private String phone;
	/**
	 * 推广码，从请求地址中获取，不可为空
	 */
	@Column(name="recommend_key")
	@NotNull(message= "{flowUser.recomendKey.notnull}" ,groups= {ValidPart1.class})
	private String recommendKey;
	/**
	 * 注册渠道号码，从请求地址中获取 ，不可为空
	 */
	@NotNull(message= "{flowUser.channel.notnull}" ,groups= {ValidPart1.class})
	private String channel;
	/**
	 * 注册时间不可为空
	 */
	@Column(name="create_time")
	private Date createTime;
	/**
	 * 注册时的ipv4地址，不可为空，最大长度16个字符
	 */
	@Column(name="register_ipv4")
	private String registerIpv4;
	/**
	 * 注册平台，可为空
	 */
	private String os;
	public FlowUser(Long id,
			@NotNull(message = "{flowUser.phone.notnull}", groups = ValidPart1.class) @Pattern(message = "{flowUser.phone.format}", regexp = "^1[3456789]\\d{9}$", groups = ValidPart1.class) String phone,
			@NotNull(message = "{flowUser.recomendKey.notnull}", groups = ValidPart1.class) String recommendKey,
			@NotNull(message = "{flowUser.channel.notnull}", groups = ValidPart1.class) String channel, Date createTime,
			String registerIpv4, String os) {
		super();
		this.id = id;
		this.phone = phone;
		this.recommendKey = recommendKey;
		this.channel = channel;
		this.createTime = createTime;
		this.registerIpv4 = registerIpv4;
		this.os = os;
	}
	public FlowUser() {
		super();
	}

}
