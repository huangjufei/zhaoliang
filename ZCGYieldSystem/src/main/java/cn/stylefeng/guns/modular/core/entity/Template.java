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
@Table(name="t_base_template")
public class Template {
	@Id
	@GeneratedValue
	private Integer id;
	private String name;
	private String remark;
	private Integer modifier;
	@Column(name="update_time")
	private Date updateTime;
	private Integer creator;
	@Column(name="creater_time")
	private String createrTime;
	public Template(Integer id, String name, String remark, Integer modifier, Date updateTime, Integer creator,
			String createrTime) {
		super();
		this.id = id;
		this.name = name;
		this.remark = remark;
		this.modifier = modifier;
		this.updateTime = updateTime;
		this.creator = creator;
		this.createrTime = createrTime;
	}
	public Template() {
		super();
	}
	
}
