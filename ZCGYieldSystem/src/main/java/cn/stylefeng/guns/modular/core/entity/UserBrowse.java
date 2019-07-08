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
@Table(name="t_user_browse")
public class UserBrowse {
	@Id
	@GeneratedValue
	private Long id;
	@Column(name="app_id")
	private Long appId;
	@Column(name="user_id")
	private Long userId;
	@Column(name="product_id")
	private Long productId;
	private String channel;
	@Column(name="browse_time")
	private Date browseTime;
	@Column(name="show_whether")
	private Long showWhether;
	public UserBrowse(Long id, Long appId, Long userId, Long productId, String channel, Date browseTime,
			Long showWhether) {
		super();
		this.id = id;
		this.appId = appId;
		this.userId = userId;
		this.productId = productId;
		this.channel = channel;
		this.browseTime = browseTime;
		this.showWhether = showWhether;
	}
	public UserBrowse() {
		super();
	}
	
}
