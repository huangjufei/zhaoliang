package cn.stylefeng.guns.modular.system.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import cn.stylefeng.guns.modular.system.entity.User;
import lombok.Data;

@Data
public class UserVo {
    private Long userId;
    private String avatar;
    private String account;
    private String password;
    private String salt;
    private String name;
    private Date birthday;
    private String sexName;
    private String email;
    private String phone;
    private String roleName;
    private String deptName;
    private String statusName;
    private String status;
    private Date createTime;
    private Long createUser;
    private Date updateTime;
    private Long updateUser;
    private Integer version;
    private String qq;
    private String weixin;
    private String createUserName;
    private String company;
	public UserVo(User user) {
		this.userId = user.getUserId();
		this.avatar = user.getAvatar();
		this.account = user.getAccount();
		this.password = user.getPassword();
		this.salt = user.getSalt();
		this.name = user.getName();
		this.birthday = user.getBirthday();
		this.sexName = ConstantFactory.me().getSexName(user.getSex());
		this.email = user.getEmail();
		this.phone = user.getPhone();
		this.roleName = ConstantFactory.me().getRoleName(user.getRoleId());
		this.deptName =  ConstantFactory.me().getDeptName(user.getDeptId());
		this.statusName = ConstantFactory.me().getStatusName(user.getStatus());
		this.status = user.getStatus();
		this.createTime = user.getCreateTime();
		this.createUser = user.getCreateUser();
		this.updateTime = user.getUpdateTime();
		this.updateUser = user.getUpdateUser();
		this.version = user.getVersion();
		this.qq = user.getQq();
		this.weixin = user.getWeixin();
		this.company = user.getCompany();
	}
	public static List<UserVo> create(List<User> userList) {
		List<UserVo> res = new ArrayList<UserVo>();
		for(int i = 0 ,len = userList.size() ;i < len ;++i) {
			res.add(new UserVo(userList.get(i)));
		}
		return res;
	}
    
}
