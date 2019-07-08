package cn.stylefeng.guns.modular.system.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.system.entity.User;

public interface JpaUserRepository extends JpaRepository<User, Long>{

	@Query(value="select * from sys_user where STATUS != 'DELETED' and (ROLE_ID in ?1 or CREATE_USER = ?6)"
			+ " and if(?2 is not null ,NAME like ?2 or ACCOUNT like ?2 or PHONE like ?2 ,1=1) "
			+ " and if(?3 is not null ,DEPT_ID = ?3 ,1=1) and if(?4 is not null ,CREATE_TIME >= ?4 ,1=1) "
			+ " and if(?5 is not null,CREATE_TIME <= ?5 ,1=1)"
			, nativeQuery=true)
	List<User> findAllByConditions(List<Long> allRoleList, String name, Long deptId, String beginTime
			, String endTime ,Long createUser);

	User findOneByUserId(Long userId);
	
	@Query(value="select * from sys_user where STATUS != 'DELETED' and company is not null "
			+ " and (ROLE_ID in ?1 or CREATE_USER = ?6)"
			+ " and if(?2 is not null ,NAME like ?2 or ACCOUNT like ?2 or PHONE like ?2 ,1=1) "
			+ " and if(?3 is not null ,company = ?3 ,1=1) and if(?4 is not null ,CREATE_TIME >= ?4 ,1=1) "
			+ " and if(?5 is not null,CREATE_TIME <= ?5 ,1=1)"
			, nativeQuery=true)
	List<User> findAllByByConditonsAndCompany(List<Long> allRoleList, String name,String company 
			, String beginTime, String endTime ,Long createUser);
	
	@Query(value="select * from sys_user where STATUS != 'DELETED' and company is not null "
			+ " and if(?1 is not null ,NAME like ?1 or ACCOUNT like ?1 or PHONE like ?1 ,1=1) "
			+ " and if(?2 is not null ,company = ?2 ,1=1) and if(?3 is not null ,CREATE_TIME >= ?3 ,1=1) "
			+ " and if(?4 is not null,CREATE_TIME <= ?4 ,1=1)"
			, nativeQuery=true)
	List<User> findAllByByConditonsAndCompany(String name,String company 
			, String beginTime, String endTime);

	@Modifying
	@Transactional
	@Query("update User set status = ?1 where company = ?2")
	Integer updateStatusByCompany(String status, String company);

	@Query(value="select u.userId from User as u where (u.name like ?1 or u.phone like ?1 or u.account like ?1 ) "
			+ " and u.company like ?2")
	List<Long> findMoreIdListByConditions(String name, String company);

	List<User> findAllByCompany(String company);
}
