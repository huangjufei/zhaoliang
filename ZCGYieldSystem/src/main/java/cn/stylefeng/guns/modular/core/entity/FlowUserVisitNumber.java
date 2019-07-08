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
@Table(name="flow_user_visit_number")
public class FlowUserVisitNumber {

	@Id
	@GeneratedValue
	private Long id;
	private String ipv4;
	private String ipv6;
	@Column(name="visit_time")
	private Date visitTime;
	
	private String channel;
	@Column(name="recommend_key")
	private String recommendKey;
	private String os;
	
	public FlowUserVisitNumber() {
		super();
	}

	public FlowUserVisitNumber(Long id, String ipv4, String ipv6, Date visitTime, String channel, String recommendKey,
			String os) {
		super();
		this.id = id;
		this.ipv4 = ipv4;
		this.ipv6 = ipv6;
		this.visitTime = visitTime;
		this.channel = channel;
		this.recommendKey = recommendKey;
		this.os = os;
	}

	
}
