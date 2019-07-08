package cn.stylefeng.guns.modular.core.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.Resources;

public interface JpaResourcesRepository extends JpaRepository<Resources, Long>{

	@Query(value="select * from resources where if(?1 is not null ,name like ?1 , 1 = 1) "
			+ " and if(?2 is not null ,tag like ?2 ,1 = 1)"
			,nativeQuery = true)
	List<Resources> findPage(String name, String tag, Pageable pageable);

	Resources findOneByUserKey(String userKey);

	@Modifying
	@Transactional
	@Query(value="update Resources set name = ?2 ,description = ?3 ,dir = ?4 ,fileName = ?5 ,sign = ?6 "
			+ ", size = ?7 ,storeType = ?8 ,subtype = ?9 ,sysKey = ?10 ,tag = ?11 ,type = ?12 ,updateTime = ?13 "
			+ " where id = ?1")
	Integer updateById(Long id, String name, String description, String dir, String fileName, String sign, Long size,
			String storeType, String subtype, String sysKey, String tag, String type, Date updateTime);

	@Modifying
	@Transactional
	@Query(value="update Resources set status = ?2"
			+ " where id = ?1")
	Integer updateStatusById(Long id, String status);

	Resources findOneById(Long id);

	List<Resources> findMoreByName(String name);

}
