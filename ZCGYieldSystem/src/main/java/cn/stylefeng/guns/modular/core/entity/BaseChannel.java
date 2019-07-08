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
@Table(name="t_base_channel")
public class BaseChannel {

	@Id
	@GeneratedValue
	private Integer id;
	private String name;
	private String value;
	private String remark;
	private Integer modifier;
	@Column(name="update_time")
	private Date updateTime;
	private Integer creator;
	@Column(name="creater_time")
	private String createrTime;
	
}
