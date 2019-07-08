package cn.stylefeng.guns.modular.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.FlowUser;
import cn.stylefeng.guns.modular.core.vo.DataFlowUserVo;

public interface JpaFlowUserRepository extends JpaRepository<FlowUser, Long> {

	FlowUser findOneByPhone(String phone);

	FlowUser findOneByPhoneAndChannelAndRecommendKey(String phone, String channel, String recommendKey);

	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为所有数据，针对于管理员使用
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey "
		+ " ,fu.channel ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName"
		+ " ,ai.id ,ai.name) "
		+ " from FlowUser as fu "
		+ " inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join ApkInfo as ai on ai.name like ?4 and ai.id = uu.appId "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 and fu.os like ?7 "
		+ " and fu.createTime >= ?5 and fu.createTime <= ?6 ")
	List<DataFlowUserVo> findPage(String phone, String company, String channel, String appName 
			, Date startTime, Date endTime ,String os,Pageable pageable);

	/**
	 *	 通过给定条件查询数据，该方法的查询数据对象为所有数据，针对于管理员使用
	 */
	@Query(value="select count(*) from FlowUser as fu "
			+ " inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join ApkInfo as ai on ai.name like ?4 and ai.id = uu.appId "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 and fu.createTime >= ?5 and fu.createTime <= ?6 ")
		Long findPageCount(String phone, String company, String channel ,String appName, Date startTime, Date endTime);

	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户所管理的数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey "
		+ " ,fu.channel ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?1 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " where (fu.phone like ?2 or u2.name like ?2) and fu.channel like ?3 and fu.createTime >= ?4 and fu.createTime <= ?5 ")
	List<DataFlowUserVo> findPageByUserId(Long userId, String name, String channel, Date startTime, Date endTime,
			Pageable pageable);

	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户所管理的数据
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId = ?1 "
			+ " where fu.phone like ?2 and fu.channel like ?3 and fu.createTime >= ?4 and fu.createTime <= ?5 ")
	Long findPageCountByUserId(Long userId, String name, String channel, Date startTime, Date endTime);

	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey "
			+ " ,fu.channel ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName) "
			+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey " 
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "  
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?1 ) or u.userId = ?3)) "
			+ " inner join User as u2 on u2.userId = uu.userId"
			+ " where (fu.phone like ?4 or u2.name like ?4) and fu.channel like ?5 and fu.createTime >= ?6 and fu.createTime <= ?7 ")
	List<DataFlowUserVo> findPageInRoleListAndByCompanyAndUserId(List<Long> allRoleList, String company, Long userId,
			String name, String channel, Date startTime, Date endTime, Pageable pageable);

	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey " 
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "  
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?1 ) or u.userId = ?3)) "
			+ " where fu.phone like ?4 and fu.channel like ?5 and fu.createTime >= ?6 and fu.createTime <= ?7 ")
	Long findPageCountInRoleListAndByCompanyAndUserId(List<Long> allRoleList, String company, Long userId, String name,
			String channel, Date startTime, Date endTime);

	/**
	 * 	通过给定条件查询数据在每一天的数据量；
	 */
//	@Query(value="select count(*) from FlowUser as fu group by date_format(fu.createTime ,'%Y-%m-%d') ")
	@Query(value="select count(*) from FlowUser as fu "
			+ " inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in "
			+ " (select u.userId from User as u where u.company like ?2) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join ApkInfo as ai on ai.name like ?4 and ai.id = uu.appId "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and fu.createTime >= ?5 and fu.createTime <= ?6 and fu.os like ?7 "
			+ " group by date_format(fu.createTime ,'%Y-%m-%d') ,u2.name ,u2.company ,uu.channel ,ai.name ")
	List<Long> findCountGroupByDate(String name, String company, String channel, String appName
			, Date startTime, Date endTime ,String os);

	/**
	 * 	通过给定条件查询用户在指定时间内的转化数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
		+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone and date_format(aua.registerTime ,'%Y-%m-%d') = ?4"
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	List<DataFlowUserVo> findDateConversionByConditionsInDate(String name, String company, String channel, String date ,Pageable pageable);
	
	/**
	 * 	通过给定条件查询用户在指定时间内的转化数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone and date_format(aua.registerTime ,'%Y-%m-%d') = ?4"
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	Long findDateConversionCountByConditionsInDate(String name, String company, String channel, String date);
	
	/**
	 * 	通过给定条件查询指定时间内的用户的注册转化数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
		+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	List<DataFlowUserVo> findRegisterConversionByConditionsInDate(String name, String company, String channel,
			String date, Pageable pageable);
	
	/**
	 * 	通过给定条件查询指定时间内的用户的注册转化数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	Long findRegisterConversionCountByConditionsInDate(String name, String company, String channel,
			String date);

	/**
	 * 	通过给定条件查询指定时间内的用户在指定时间之外的转化数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
		+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone and date_format(aua.registerTime ,'%Y-%m-%d') != ?4"
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	List<DataFlowUserVo> findDifferenceConversionByConditionsInDate(String name, String company, String channel,
			String date, Pageable pageable);
	
	/**
	 * 	通过给定条件查询指定时间内的用户在指定时间之外的转化数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone and date_format(aua.registerTime ,'%Y-%m-%d') != ?4"
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	Long findDifferenceConversionCountByConditionsInDate(String name, String company, String channel,
			String date);

	/**
	 * 	通过给定条件查询指定时间内的用户未转化数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
		+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4 "
		+ " and fu.phone not in (select aua.mobilePhone from AppUserAccount as aua)")
	List<DataFlowUserVo> findFailedConversionByConditionsInDate(String name, String company, String channel,
			String date, Pageable pageable);

	/**
	 * 	通过给定条件查询指定时间内的用户未转化数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4 "
		+ " and fu.phone not in (select aua.mobilePhone from AppUserAccount as aua)")
	Long findFailedConversionCountByConditionsInDate(String name, String company, String channel, String date);

	/**
	 * 	通过给定条件查询指定时间内的指定推广员的用户的注册转化数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
		+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?4 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?2 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?3")
	List<DataFlowUserVo> findRegisterConversionByUserIdAndConditionsInDate(String name, String channel,
			String date, Long userId, Pageable pageable);
	
	/**
	 * 	通过给定条件查询指定时间内的指定推广员的用户的注册转化数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?4 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?2 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?3")
	Long findRegisterConversionCountByUserIdAndConditionsInDate(String name, String channel,
			String date, Long userId);
	
	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
			+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
			+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?6 ) or u.userId = ?5)) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	List<DataFlowUserVo> findRegisterConversionByInRoleList(String name, String company, String channel,
			String date, Long userId ,List<Long> allRoleList, Pageable pageable);
	
	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?6 ) or u.userId = ?5)) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	Long findRegisterConversionCountByInRoleList(String name, String company, String channel,
			String date, Long userId ,List<Long> allRoleList);
	
	/**
	 * 	通过给定条件查询指定时间内的指定推广员的用户的注册转化数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
		+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?4 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
		+ " and date_format(aua.registerTime ,'%Y-%m-%d') = ?3 "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?2 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?3 ")
	List<DataFlowUserVo> findDateConversionByUserIdAndConditionsInDate(String name, String channel,
			String date, Long userId, Pageable pageable);
	
	/**
	 * 	通过给定条件查询指定时间内的指定推广员的用户的注册转化数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?4 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
		+ " and date_format(aua.registerTime ,'%Y-%m-%d') = ?3 "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?2 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?3")
	Long findDateConversionCountByUserIdAndConditionsInDate(String name, String channel,
			String date, Long userId);
	
	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
			+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
			+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?6 ) or u.userId = ?5)) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
			+ " and date_format(aua.registerTime ,'%Y-%m-%d') = ?4 "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	List<DataFlowUserVo> findDateConversionByInRoleList(String name, String company, String channel,
			String date, Long userId ,List<Long> allRoleList, Pageable pageable);
	
	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?6 ) or u.userId = ?5)) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
			+ " and date_format(aua.registerTime ,'%Y-%m-%d') = ?4"
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	Long findDateConversionCountByInRoleList(String name, String company, String channel,
			String date, Long userId ,List<Long> allRoleList);
	

	/**
	 * 	通过给定条件查询指定时间内的指定推广员的用户的注册转化数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
		+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?4 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
		+ " and date_format(aua.registerTime ,'%Y-%m-%d') != ?3 "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?2 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?3")
	List<DataFlowUserVo> findDifferenceConversionByUserIdAndConditionsInDate(String name, String channel,
			String date, Long userId, Pageable pageable);
	
	/**
	 * 	通过给定条件查询指定时间内的指定推广员的用户的注册转化数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?4 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
		+ " and date_format(aua.registerTime ,'%Y-%m-%d') != ?3 "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?2 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?3")
	Long findDifferenceConversionCountByUserIdAndConditionsInDate(String name, String channel,
			String date, Long userId);
	
	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
			+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ,aua.registerTime) "
			+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?6 ) or u.userId = ?5)) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
			+ " and date_format(aua.registerTime ,'%Y-%m-%d') != ?4 "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	List<DataFlowUserVo> findDifferenceConversionByInRoleList(String name, String company, String channel,
			String date, Long userId ,List<Long> allRoleList, Pageable pageable);
	
	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?6 ) or u.userId = ?5)) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join AppUserAccount as aua on aua.mobilePhone = fu.phone "
			+ " and date_format(aua.registerTime ,'%Y-%m-%d') != ?4 "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4")
	Long findDifferenceConversionCountByInRoleList(String name, String company, String channel,
			String date, Long userId ,List<Long> allRoleList);
	
	
	/**
	 * 	通过给定条件查询指定时间内的指定推广员的用户的注册转化数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
		+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName) "
		+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?4 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?2 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?3 "
		+ " and fu.phone not in (select aua.mobilePhone from AppUserAccount as aua)")
	List<DataFlowUserVo> findFailedConversionByUserIdAndConditionsInDate(String name, String channel,
			String date, Long userId, Pageable pageable);
	
	/**
	 * 	通过给定条件查询指定时间内的指定推广员的用户的注册转化数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
		+ " and fu.channel = uu.channel and uu.userId = ?4 "
		+ " inner join User as u2 on u2.userId = uu.userId "
		+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?2 "
		+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?3 "
		+ " and fu.phone not in (select aua.mobilePhone from AppUserAccount as aua)")
	Long findFailedConversionCountByUserIdAndConditionsInDate(String name, String channel,
			String date, Long userId);
	
	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey ,fu.channel "
			+ " ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName ) "
			+ " from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?6 ) or u.userId = ?5)) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4 "
			+ " and fu.phone not in (select aua.mobilePhone from AppUserAccount as aua)")
	List<DataFlowUserVo> findFailedConversionByInRoleList(String name, String company, String channel,
			String date, Long userId ,List<Long> allRoleList, Pageable pageable);
	
	/**
	 * 	通过给定条件查询数据，该方法的查询数据对象为当前给对应userId用户和给定role用户所管理的数据的数量
	 */
	@Query(value="select count(*) from FlowUser as fu inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2 "
			+ " and (u.userId in (select ur.userId from UserRole as ur where ur.roleId in ?6 ) or u.userId = ?5)) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 "
			+ " and date_format(fu.createTime ,'%Y-%m-%d') = ?4 "
			+ " and fu.phone not in (select aua.mobilePhone from AppUserAccount as aua)")
	Long findFailedConversionCountByInRoleList(String name, String company, String channel,
			String date, Long userId ,List<Long> allRoleList);

	

	
	/*
	 * @Query(
	 * value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey "
	 * +
	 * " ,fu.channel ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName"
	 * + " ,ai.id ,ai.name,count(*)) " + " from FlowUser as fu " +
	 * " inner join UserUrl as uu on fu.recommendKey = uu.recommendKey " +
	 * " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
	 * + " inner join User as u2 on u2.userId = uu.userId " +
	 * " inner join ApkInfo as ai on ai.name like ?4 and ai.id = uu.appId " +
	 * " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 and fu.os like ?7 "
	 * + " and fu.createTime >= ?5 and fu.createTime <= ?6" +
	 * "	group by DATE_FORMAT(fu.createTime,'%Y-%m-%d'),ai.name,uu.channelName,u2.company,u2.mame"
	 * ) List<DataFlowUserVo> findAllBy(String name, String company, String channel,
	 * String appName, Date startTime, Date endTime, String os);
	 */

	
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.DataFlowUserVo(fu.id ,fu.phone ,fu.recommendKey "
			+ " ,fu.channel ,fu.createTime ,fu.registerIpv4 ,fu.os ,u2.userId ,u2.name ,u2.company ,uu.channelName"
			+ " ,ai.id ,ai.name,count(*)) "
			+ " from FlowUser as fu "
			+ " inner join UserUrl as uu on fu.recommendKey = uu.recommendKey "
			+ " and fu.channel = uu.channel and uu.userId in (select u.userId from User as u where u.company like ?2) "
			+ " inner join User as u2 on u2.userId = uu.userId "
			+ " inner join ApkInfo as ai on ai.name like ?4 and ai.id = uu.appId "
			+ " where (fu.phone like ?1 or u2.name like ?1) and fu.channel like ?3 and fu.os like ?7 "
			+ " and fu.createTime >= ?5 and fu.createTime <= ?6 "
			+ "	group by DATE_FORMAT(fu.createTime,'%Y-%m-%d'),ai.name,uu.channelName,u2.company,u2.name")
	
	List<DataFlowUserVo> findAllBy(String name, String company, String channel, String appName, Date startTime,
			Date endTime, String os,Pageable pageable);


	
	
	
	
	
}
