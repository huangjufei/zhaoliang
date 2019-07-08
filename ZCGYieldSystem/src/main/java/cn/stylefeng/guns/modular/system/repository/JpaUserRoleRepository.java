package cn.stylefeng.guns.modular.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.stylefeng.guns.modular.system.entity.UserRole;

public interface JpaUserRoleRepository extends JpaRepository<UserRole, Long>{

	@Modifying
	@Transactional
	@Query("delete from UserRole where userId = ?1")
	void deleteAllByUserId(Long userId);

	@Query("select roleId from UserRole where userId = ?1")
	List<Long> findAllRoleIdByUserId(Long userId);

	@Query(value="select userId from UserRole where roleId in ?1")
	List<Long> findMoreUserIdByRoleIds(List<Long> allRoleList);

	
}
