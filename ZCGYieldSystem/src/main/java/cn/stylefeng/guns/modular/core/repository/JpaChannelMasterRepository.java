package cn.stylefeng.guns.modular.core.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.ChannelMaster;

public interface JpaChannelMasterRepository extends JpaRepository<ChannelMaster, Integer>{

	@Query(value="select * from channel_master where status != '删除' and "
			+ " if(?1 is not null ,name like ?1 or master like ?1 ,1=1) "
			+ " and if(?2 is not null , create_time >= ?2 ,1=1) and if(?3 is not null ,create_time <= ?3 ,1=1) "
			, nativeQuery = true)
	List<ChannelMaster> findPage(String name, String startTime, String endTime, Pageable pageable);

	ChannelMaster findOneById(Integer id);

	@Modifying
	@Transactional
	@Query(value="update ChannelMaster set name = ?2 ,description = ?3 ,info = ?4 ,master = ?5 "
			+ ", masterContact = ?6 ,status = ?7 ,updateTime = ?8 where id = ?1")
	Integer updateById(Integer id, String name, String description, String info, String master, String masterContact,
			String status, Date udpateTime);

	@Modifying
	@Transactional
	@Query(value="update ChannelMaster set status = ?2 , updateTime = ?3 where id = ?1")
	Integer updateStatusById(Integer id, String status, Date updateTime);

}
