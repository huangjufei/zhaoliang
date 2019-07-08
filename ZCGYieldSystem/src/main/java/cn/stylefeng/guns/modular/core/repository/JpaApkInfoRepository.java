package cn.stylefeng.guns.modular.core.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.ApkInfo;
import cn.stylefeng.guns.modular.core.entity.Resources;

public interface JpaApkInfoRepository extends JpaRepository<ApkInfo, Integer>{

	@Query(value="from ApkInfo where name like ?1 or channel like ?1 or channelName like ?1")
	List<ApkInfo> findPage(String name, Pageable pageable);

	@Query(value="from Resources as r where r.id in (select ai.resourcesId from ApkInfo as ai "
			+ " where name like ?1 or channel like ?1 or channelName like ?1)")
	List<Resources> findResourcePage(String name ,Pageable pageable);

	ApkInfo findOneByChannelAndName(String channel, String name);

	@Modifying
	@Transactional
	@Query(value="update ApkInfo set name = ?2 ,description = ?3 ,channel = ?4 "
			+ " ,channelName = ?5 ,updateTime = ?6 ,os = ?7 "
			+ " where id = ?1")
	Integer updateById(Integer id, String name, String description, String channel 
			, String channelName, Date updateTime ,String os);

	ApkInfo findOneById(Integer id);

	@Modifying
	@Transactional
	@Query(value="update ApkInfo set status = ?2 where id = ?1")
	Integer updateStatusById(Integer id, String status);

	List<ApkInfo> findMoreByChannel(String channel);

	/**
	 * 查找指定状态的App数据
	 * @param shelves
	 * @return
	 */
	@Query(value="from ApkInfo where status = ?1 group by name")
	List<ApkInfo> findMoreByStatusAndGroupByName(String shelves);

	ApkInfo findOneByNameAndOs(String name, String os);

	@Query(value="from ApkInfo group by name")
	List<ApkInfo> findAllGroupByName();
}
