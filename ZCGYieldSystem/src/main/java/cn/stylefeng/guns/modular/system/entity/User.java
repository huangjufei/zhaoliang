package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@TableName("sys_user")
@Data
@Entity
@Table(name="sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue
    @TableId(value = "USER_ID", type = IdType.ID_WORKER)
    @Column(name="USER_ID")
    private Long userId;
    /**
     * 头像
     */
    @TableField("AVATAR")
    @Column(name="AVATAR")
    private String avatar;
    /**
     * 账号
     */
    @TableField("ACCOUNT")
    @Column(name="ACCOUNT")
    private String account;
    /**
     * 密码
     */
    @TableField("PASSWORD")
    @Column(name="PASSWORD")
    private String password;
    /**
     * md5密码盐
     */
    @TableField("SALT")
    @Column(name="SALT")
    private String salt;
    /**
     * 名字
     */
    @TableField("NAME")
    @Column(name="NAME")
    private String name;
    /**
     * 生日
     */
    @TableField("BIRTHDAY")
    @Column(name="BIRTHDAY")
    private Date birthday;
    /**
     * 性别(字典)
     */
    @TableField("SEX")
    @Column(name="SEX")
    private String sex;
    /**
     * 电子邮件
     */
    @TableField("EMAIL")
    @Column(name="EMAIL")
    private String email;
    /**
     * 电话
     */
    @TableField("PHONE")
    @Column(name="PHONE")
    private String phone;
    /**
     * 角色id(多个逗号隔开)
     */
    @TableField("ROLE_ID")
    @Column(name="ROLE_ID")
    private String roleId;
    /**
     * 部门id(多个逗号隔开)
     */
    @TableField("DEPT_ID")
    @Column(name="DEPT_ID")
    private Long deptId;
    /**
     * 状态(字典)
     */
    @TableField("STATUS")
    @Column(name="STATUS")
    private String status;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    @Column(name="CREATE_TIME")
    private Date createTime;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    @Column(name="CREATE_USER")
    private Long createUser;
    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    @Column(name="UPDATE_TIME")
    private Date updateTime;
    /**
     * 更新人
     */
    @TableField(value = "UPDATE_USER", fill = FieldFill.UPDATE)
    @Column(name="UPDATE_USER")
    private Long updateUser;
    /**
     * 乐观锁
     */
    @TableField("VERSION")
    @Column(name="VERSION")
    private Integer version;
    @TableField("qq")
    @Column(name="qq")
    private String qq;
    @TableField("weixin")
    @Column(name="weixin")
    private String weixin;
    @TableField("company")
    @Column(name="company")
    private String company;
}
