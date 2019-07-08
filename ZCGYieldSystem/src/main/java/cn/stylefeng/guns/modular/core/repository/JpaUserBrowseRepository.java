package cn.stylefeng.guns.modular.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.UserBrowse;
import cn.stylefeng.guns.modular.core.vo.VisitCountVo;

public interface JpaUserBrowseRepository extends JpaRepository<UserBrowse, Long> {

	List<UserBrowse> findByAppId(long id);

	@Query(value="select count(*) from UserBrowse where appId = ?1")
	Integer findUserBrowseCountNumberByAppId(long appId);

	@Query(value="select count(*) from UserBrowse where appId = ?1 and channel = ?2")
	Integer findUserBrowseCountNumberByAppIdAndChannel(long appId, String channel);

	@Query(value="select count(*) from UserBrowse")
	Integer findAllUserBrowseCountNumber();

	List<UserBrowse> findAllByAppIdAndBrowseTimeBetween(long appId, Date startDate, Date endDate);

	List<UserBrowse> findAllByAppIdAndBrowseTimeGreaterThanEqual(long appId, Date startDate);

	List<UserBrowse> findAllByAppIdAndBrowseTimeLessThanEqual(long appId, Date endDate);

	
	/**
	 * 	通过给定条件，查询在每一天内，公司、推广员、渠道、app 的app用户点击数
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.VisitCountVo("
			+ " DATE_FORMAT(ub.browseTime ,'%Y-%m-%d') ,ai.name ,uu.channelName ,u.company ,u.name, count(*)) "
			+ " from UserBrowse as ub "
			+ " inner join AppUserAccount as aua on aua.id = ub.userId "
			+ " inner join FlowUser as fu on fu.phone = aua.mobilePhone and fu.os like ?7 "
			+ " inner join UserUrl as uu on uu.channel like ?3 and uu.channel = fu.channel "
			+ " and uu.recommendKey = fu.recommendKey "
			+ " inner join ApkInfo as ai on ai.name like ?4 and ai.id = uu.appId "
			+ " inner join User as u on (u.name like ?1 or u.phone like ?1) and u.company like ?2 "
			+ " and u.userId = uu.userId "
			+ " where ub.browseTime >= ?5 and ub.browseTime <= ?6 "
			+ " group by DATE_FORMAT(ub.browseTime ,'%Y-%m-%d') ,uu.channel ,u.company ,u.name ,ai.name")
	List<VisitCountVo> findDateCountNumberBy(String name, String company, String channel, String appName,
			Date startTime, Date endTime, String os);
}
