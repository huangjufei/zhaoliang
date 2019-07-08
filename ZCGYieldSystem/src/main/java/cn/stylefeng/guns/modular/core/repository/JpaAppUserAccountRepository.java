package cn.stylefeng.guns.modular.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.AppUserAccount;
import cn.stylefeng.guns.modular.core.vo.UserBuckleQuantityInfo;

public interface JpaAppUserAccountRepository extends JpaRepository<AppUserAccount, Long>{

	List<AppUserAccount> findAllByMobilePhone(String phone);

	@Query(value = " select date_format(registerTime ,'%Y-%m-%d') ,count(*) from AppUserAccount "
			+ " group by date_format(registerTime ,'%Y-%m-%d') order by registerTime desc")
	List<Object[]> findCountGroupByDate(Date startTime, Date endTime, Pageable appUserPageable);

	@Query(value="from AppUserAccount where registerTime between ?1 and ?2 ")
	List<AppUserAccount> findPageByRegisterBetweenDate(Date startTime, Date endTime);

	@Query(value="select new cn.stylefeng.guns.modular.core.vo.UserBuckleQuantityInfo"
			+ " (ai.name ,uu.channelName ,u.company ,u.name ,DATE_FORMAT(aua.registerTime,'%Y-%m-%d') as date"
			+ " , count(*) as number ,fu.os) "
			+ " from AppUserAccount as aua "
			+ " inner join FlowUser as fu on aua.mobilePhone = fu.phone and fu.os like ?7 "
			+ " inner join UserUrl as uu on uu.recommendKey = fu.recommendKey and uu.channel = fu.channel "
			+ " and uu.channel like ?3 "
			+ " inner join User as u on uu.userId = u.userId and u.company like ?2 and (u.name like ?1 or u.account like ?1) "
			+ " inner join ApkInfo as ai on ai.name like ?4 and  ai.id = uu.appId "
			+ " where aua.registerTime >= ?5 and aua.registerTime <= ?6  and aua.channel = uu.channel "
			+ " group by DATE_FORMAT(aua.registerTime,'%Y-%m-%d') , ai.name ,u.name ,u.company ,fu.os ")
	List<UserBuckleQuantityInfo> findStatistics(String name, String company,String channel 
			, String appName, Date startTime ,Date endTime, String os,Pageable pageable);

	@Query(value="select count(*) from AppUserAccount as aua "
			+ " inner join FlowUser as fu on aua.mobilePhone = fu.phone and fu.os like ?7 "
			+ " inner join UserUrl as uu on uu.recommendKey = fu.recommendKey and uu.channel = fu.channel "
			+ " and uu.channel like ?3 "
			+ " inner join User as u on uu.userId = u.userId and u.company like ?2 "
			+ " and (u.name like ?1 or u.account like ?1) "
			+ " inner join ApkInfo as ai on ai.name like ?4 and  ai.id = uu.appId "
			+ " where aua.registerTime >= ?5 and aua.registerTime <= ?6 and aua.channel = uu.channel "
			+ " group by DATE_FORMAT(aua.registerTime,'%Y-%m-%d') , ai.name ,u.name ,u.company")
	List<Long> findStatisticsCount(String name, String company ,String channel, String appName 
			,Date startTime, Date endTime ,String os);
	
	List<AppUserAccount> findMoreByMobilePhone(String phone);
	
	
//	@Query(value="select count(*) from AppUserAccount as tua group by DATE_FORMAT(tua.registerTime,'%Y-%m-%d') ")
	@Query(value="select count(*) from AppUserAccount as aua "
			+ " inner join FlowUser as fu on aua.mobilePhone = fu.phone and fu.os like ?6 "
			+ " inner join UserUrl as uu on uu.recommendKey = fu.recommendKey and uu.channel = fu.channel "
			+ " and uu.channel like ?2 "
			+ " and uu.userId = ?1 "
			+ " inner join ApkInfo as ai on ai.name like ?3 and ai.id = uu.appId "
			+ " inner join User as u on u.userId = ?1 "
			+ " where aua.registerTime >= ?4 and aua.registerTime <= ?5 "
			+ " group by DATE_FORMAT(aua.registerTime,'%Y-%m-%d') ,ai.name,uu.channel")
	List<Long> findCountInDateByGroupByDate(Long userId, String channel
			 , String appName, Date startTime ,Date endTime ,String os);

	/**
	 *	 查找指定用户在指定时间内的扣量数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.UserBuckleQuantityInfo"
			+ " (ai.name ,uu.channelName ,u.company ,u.name ,DATE_FORMAT(aua.registerTime,'%Y-%m-%d') as date "
			+ " , count(*) as number , fu.os ,uu.id ) "
			+ " from AppUserAccount as aua "
			+ " inner join FlowUser as fu on aua.mobilePhone = fu.phone and fu.os like ?6 "
			+ " inner join UserUrl as uu on uu.recommendKey = fu.recommendKey and uu.channel = fu.channel "
			+ " and uu.channel like ?2 "
			+ " and uu.userId = ?1 and uu.id in (select dr.restrictedName from DataRestricted as dr) "
			+ " inner join ApkInfo as ai on ai.name like ?3 and ai.id = uu.appId "
			+ " inner join User as u on u.userId = ?1 "
			+ " where aua.registerTime >= ?4 and aua.registerTime <= ?5  and aua.channel = uu.channel  "
			+ " group by DATE_FORMAT(aua.registerTime,'%Y-%m-%d') ,ai.name ,uu.channel ,fu.os")
	List<UserBuckleQuantityInfo> findBuckleQuantityInDate(Long userId, String channel ,String appName, Date startTime,
			Date endTime,String os, Pageable pageable);

	/**
	 * 	查找指定用户在指定时间内的不扣量数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.UserBuckleQuantityInfo"
			+ " (ai.name ,uu.channelName ,u.company ,u.name ,DATE_FORMAT(aua.registerTime,'%Y-%m-%d') as date "
			+ " , count(*) as number ,fu.os ,uu.id ) "
			+ " from AppUserAccount as aua "
			+ " inner join FlowUser as fu on aua.mobilePhone = fu.phone and fu.os like ?6 "
			+ " inner join UserUrl as uu on uu.recommendKey = fu.recommendKey and uu.channel = fu.channel "
			+ " and uu.channel like ?2 "
			+ " and uu.userId = ?1 and uu.id not in (select dr.restrictedName from DataRestricted as dr) "
			+ " inner join ApkInfo as ai on ai.name like ?3 and ai.id = uu.appId "
			+ " inner join User as u on u.userId = ?1 "
			+ " where aua.registerTime >= ?4 and aua.registerTime <= ?5 and aua.channel = uu.channel  "
			+ " group by DATE_FORMAT(aua.registerTime,'%Y-%m-%d') ,ai.name ,uu.channel ,fu.os")
	List<UserBuckleQuantityInfo> findNoBuckleQuantityInDate(Long userId,String channel ,String appName 
			, Date startTime ,Date endTime, String os,Pageable pageable);
}
