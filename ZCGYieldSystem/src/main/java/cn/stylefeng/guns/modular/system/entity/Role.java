package cn.stylefeng.guns.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@Entity
@Table(name="sys_role")
@TableName("sys_role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "ROLE_ID", type = IdType.ID_WORKER)
    @Id
    @GeneratedValue
    @Column(name="ROLE_ID")
    private Long roleId;
    /**
     * 父角色id
     */
    @TableField("PID")
    @Column(name="PID")
    private Long pid;
    /**
     * 角色名称
     */
    @TableField("NAME")
    @Column(name="NAME")
    private String name;
    /**
     * 提示
     */
    @Column(name="DESCRIPTION")
    @TableField("DESCRIPTION")
    private String description;
    /**
     * 序号
     */
    @TableField("SORT")
    @Column(name="SORT")
    private Integer sort;
    /**
     * 乐观锁
     */
    @TableField("VERSION")
    @Column(name="VERSION")
    private Integer version;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    @Column(name="CREATE_TIME")
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    @Column(name="UPDATE_TIME")
    private Date updateTime;
    /**
     * 创建用户
     */
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    @Column(name="CREATE_USER")
    private Long createUser;
    /**
     * 修改用户
     */
    @TableField(value = "UPDATE_USER", fill = FieldFill.UPDATE)
    @Column(name="UPDATE_USER")
    private Long updateUser;


    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    @Override
    public String toString() {
        return "Role{" +
                ", roleId=" + roleId +
                ", pid=" + pid +
                ", name=" + name +
                ", description=" + description +
                ", sort=" + sort +
                ", version=" + version +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createUser=" + createUser +
                ", updateUser=" + updateUser +
                "}";
    }
}
