package cn.stylefeng.guns.modular.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * app注册用户
 * @author huwei
 *
 */
@Data
@Entity
@Table(name="t_user_account")
public class AppUserAccount {
	@Id
	@GeneratedValue
	private Long id;
	private String username;
	private String password;
	@Column(name="app_id")
	private Long appId;
	private String platform;
	private String nickname;
	@Column(name="mobile_phone")
	private String mobilePhone;
	private String email;
	private String qq;
	private Integer status;
	private String channel;
	@Column(name="second_channel")
	private String secondChannel;
	@Column(name="register_time")
	private Date registerTime;
	@Column(name="update_time")
	private Date updateTime;
	@Column(name="login_time")
	private Date loginTime;
	@Column(name="login_host")
	private String loginHost;
	private String longitude;
	private String latitude;
	@Column(name="send_spread_sms")
	private Integer sendSpreadSms;
	@Column(name="user_code")
	private String userCode;
	@Column(name="user_img_url")
	private String userImgUrl;
	private String remark;
	@Column(name="id_card")
	private String idCard;
	private Long education;
	@Column(name="job_type")
	private Long jobType;
	private Long credit;
	@Column(name="zhima_score")
	private Long zhimaScore;
	@Column(name="monthly_income_min")
	private Long monthlyIncomeMin;
	@Column(name="monthly_income_max")
	private Long monthlyIncomeMax;
}
