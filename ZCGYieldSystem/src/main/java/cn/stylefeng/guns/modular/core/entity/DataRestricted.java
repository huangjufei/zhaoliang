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
import cn.stylefeng.guns.core.controller.valid.ValidPart3;
import lombok.Data;

@Data
@Entity
@Table(name="t_data_restricted")
public class DataRestricted {
	@Id
	@GeneratedValue
	@NotNull(message="{dataRestricted.id.notnull}" ,groups = {ValidPart2.class ,ValidPart3.class})
	private Integer id; 
	private String creator;
	@Column(name="create_time")
	private Date createTime ;
	@Column(name="update_time")
	private Date updateTime;
	@Column(name="restricted_name")
	@NotNull(message="{dataRestricted.restrictedName.notnull}" ,groups = {ValidPart1.class})
	private String restrictedName;
	@Column(name="show_min_data")
	private Integer showMinData = 0;
	@Column(name="show_max_data")
	private Long showMaxData = Long.MAX_VALUE;
	@Column(name="ratio_value")
	private Integer ratioValue ;
	@Column(name="number_value")
	private Integer numberValue ;
	/**
	 * 	指示扣量规则
	 * 	1： 按数量扣除，即对原数据直接进行加减运算：可使用字符：1 或 number 或 numberValue 或 number_value 等表示；
	 * 	2： 按百分比扣除，可使用字符：2 或 ratio 或 ratioValue 或 ratio_value 等表示
	 */
	@Column(name="work_restricted_field")
	@NotNull(message="{dataRestricted.workRestrictedField.notnull}" ,groups = {ValidPart1.class ,ValidPart2.class})
	private String workRestrictedField ;
	@Column(name="start_time")
	private Date startTime;
	@Column(name="end_time")
	private Date endTime;
	private String info ;
	
	public boolean isWorkFieldOk() {
		return isWorkNumber() || isWorkRatio();
	}
	
	public boolean isWorkNumber() {
		String[] fileds = numberFieldSymbol();
		for(int i = 0 ;i < fileds.length ;++i) {
			if(fileds[i].toUpperCase().equals(this.workRestrictedField.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isWorkRatio() {
		String[] fileds = ratioFieldSymbol();
		for(int i = 0 ;i < fileds.length ;++i) {
			if(fileds[i].toUpperCase().equals(this.workRestrictedField.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	public String[] numberFieldSymbol() {
		return new String[] {"1" ,"number" ,"numberValue" ,"number_value" ,"nv"};
	}
	
	public String[] ratioFieldSymbol() {
		return new String[] {"2" ,"ratio" ,"ratioValue" ,"ratio_value" ,"rv"};
	}

	public long buckleQuantity(Long number) {
		long buckleQuantityNumber = isWorkNumber() ? this.numberValue : (long) (number * ((this.ratioValue / 100.00d)));
		//小于最小显示数量，不扣
		if(number - buckleQuantityNumber <= showMinData) {
			return number;
		}
		return number - buckleQuantityNumber;
	}
}
