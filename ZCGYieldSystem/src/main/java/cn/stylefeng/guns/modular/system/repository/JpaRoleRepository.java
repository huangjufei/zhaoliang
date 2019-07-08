package cn.stylefeng.guns.modular.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.system.entity.Role;

public interface JpaRoleRepository extends JpaRepository<Role, Long>{

	@Query("select roleId from Role where pid in ?1")
	List<Long> findAllChildIdByPid(List<Long> roleIdList);

	@Query("from Role where pid in ?1")
	List<Role> findAllChildrenByPid(List<Long> roleIdList);

}
