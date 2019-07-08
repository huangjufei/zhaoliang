package cn.stylefeng.guns.modular.system.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="sys_user_role")
public class UserRole {

	@Id
	@GeneratedValue
	private Long id;
	@Column(name="user_id")
	private Long userId;
	@Column(name="role_id")
	private Long roleId;
	public UserRole(Long userId, Long roleId) {
		this.userId = userId;
		this.roleId = roleId;
	}
	public UserRole() {
	}
	
}
