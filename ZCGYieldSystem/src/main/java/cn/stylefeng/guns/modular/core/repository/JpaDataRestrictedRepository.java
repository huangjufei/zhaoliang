package cn.stylefeng.guns.modular.core.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.DataRestricted;
import cn.stylefeng.guns.modular.core.vo.DataRestrictedVo;

public interface JpaDataRestrictedRepository extends JpaRepository<DataRestricted, Integer> {

	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataRestrictedVo(dr.id ,dr.creator ,dr.createTime "
			+ " ,dr.updateTime ,dr.restrictedName ,dr.showMinData ,dr.showMaxData ,dr.ratioValue ,dr.numberValue "
			+ " ,dr.workRestrictedField ,dr.startTime ,dr.endTime ,dr.info ,uu.url ,uu.channel ,uu.channelName "
			+ " ,u.userId ,u.name ,u.phone ,u.company ,u.account) "
			+ " from DataRestricted as dr "
			+ " inner join UserUrl as uu on uu.id = dr.restrictedName and (uu.channel like ?3 or uu.channelName like ?3) "
			+ " inner join User as u on uu.userId = u.userId and u.company like ?2 "
			+ " and (u.phone like ?1 or u.name like ?1 or u.account like ?1) "
			+ " inner join ApkInfo as ai on ai.name like ?6 and ai.id = uu.appId "
			+ " where dr.createTime >= ?4 and dr.createTime <= ?5 ")
	List<DataRestrictedVo> findPageInDate( String name, String company, String channel, Date startTime 
			, Date endTime, String appName, Pageable pageable);

	@Query(value="select count(*) from DataRestricted as dr "
			+ " inner join UserUrl as uu on uu.id = dr.restrictedName and (uu.channel like ?3 or uu.channelName like ?3) "
			+ " and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.phone like ?1 or u.name like ?1 or u.account like ?1) ) "
			+ " inner join ApkInfo as ai on ai.name like ?6 and ai.id = uu.appId "
			+ " where dr.createTime >= ?4 and dr.createTime <= ?5 ")
	Long findPageCountInDate( String name, String company, String channel, Date startTime, Date endTime
			, String appName);

	DataRestricted findOneById(Integer id);

	@Modifying
	@Transactional
	@Query(value="update DataRestricted set numberValue = ?2 ,ratioValue = ?2 , workRestrictedField = ?3 "
			+ " ,startTime = ?4 ,endTime = ?5 ,updateTime = ?6 ,info = ?7 ,showMinData = ?8 where id = ?1")
	Integer updateById(Integer id, Integer value ,String workField ,Date startTime, Date endTime, Date updateTime, String info, Integer showMinData);

	DataRestricted findOneByRestrictedNameAndStartTimeAndEndTime(String restrictedName, Date startTime, Date endTime);

	/**
	 * 查找扣量规则在给定时间内的数据
	 */
	@Query(value="from DataRestricted where restrictedName != 'default' ")
	List<DataRestricted> findAllNotDefault();

	/**
	 * 查找默认扣量规则
	 */
	@Query(value="from DataRestricted where restrictedName = 'default' ")
	List<DataRestricted> findAllDefault();

	/**
	 * 查找默认扣量规则
	 * public DataRestrictedVo(Integer id, String creator, Date createTime, Date updateTime, String restrictedName,
			Integer showMinData, Long showMaxData, Integer ratioValue, Integer numberValue, String workRestrictedField,
			Date startTime, Date endTime, String info) {
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataRestrictedVo(dr.id ,dr.creator ,dr.createTime "
			+ " ,dr.updateTime ,dr.restrictedName ,dr.showMinData ,dr.showMaxData ,dr.ratioValue ,dr.numberValue "
			+ " ,dr.workRestrictedField ,dr.startTime ,dr.endTime ,dr.info) "
			+ " from DataRestricted as dr where dr.restrictedName = 'default' and dr.createTime >= ?1 "
			+ " and dr.createTime <= ?2")
	List<DataRestrictedVo> findPageByDefaultAndInDate(Date startTime, Date endTime, Pageable pageable);
	
	/**
	 * 查找默认扣量规则
	 */
	@Query(value="select count(*) from DataRestricted where restrictedName = 'default' and createTime >= ?1 "
			+ " and createTime <= ?2")
	Long findPageCountByDefaultAndInDate(Date startTime, Date endTime);
}
