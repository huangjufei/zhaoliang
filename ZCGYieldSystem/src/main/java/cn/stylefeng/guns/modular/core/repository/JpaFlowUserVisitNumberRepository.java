package cn.stylefeng.guns.modular.core.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.FlowUserVisitNumber;
import cn.stylefeng.guns.modular.core.vo.VisitCountVo;

public interface JpaFlowUserVisitNumberRepository extends JpaRepository<FlowUserVisitNumber, Long>{

	@Query(value="from FlowUserVisitNumber where ipv4 = ?1 and DATE_FORMAT(visitTime ,'%Y-%m-%d') = ?2")
	FlowUserVisitNumber findOneByIpv4AndVisitTime(String visitIpv4, String currentDate);

	/**
			select DATE_FORMAT(fuvn.visit_time ,'%Y-%m-%d') ,ai.name ,uu.channel_name ,u.company ,u.name ,count(*)
			from flow_user_visit_number as fuvn
			inner join user_url as uu on uu.channel = fuvn.channel and uu.recommend_key = fuvn.recommend_key
			inner join apk_info as ai on ai.name like '%' and ai.id = uu.app_id
			inner join sys_user as u on (u.name like '%' or u.phone like '%') and u.company like '%'
			and u.user_id = uu.user_id
			group by DATE_FORMAT(fuvn.visit_time ,'%Y-%m-%d'),fuvn.channel ,u.company ,u.name ,ai.name
			ORDER BY DATE_FORMAT(fuvn.visit_time ,'%Y-%m-%d') desc
	 * 	通过给定条件，查询在每一天内，公司、推广员、渠道、app的h5访问数量
	 * 
	 */
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.VisitCountVo"
			+ " (DATE_FORMAT(fuvn.visitTime ,'%Y-%m-%d') ,ai.name ,uu.channelName ,u.company ,u.name ,count(*)) "
			+ " from FlowUserVisitNumber as fuvn "
			+ " inner join UserUrl as uu on uu.channel = fuvn.channel and uu.recommendKey = fuvn.recommendKey "
			+ " inner join ApkInfo as ai on ai.name like ?4 and ai.id = uu.appId "
			+ " inner join User as u on (u.name like ?1 or u.phone like ?1) and u.company like ?2 "
			+ " and u.userId = uu.userId "
			+ " where fuvn.channel like ?3 and fuvn.visitTime >= ?5 and fuvn.visitTime <= ?6 and fuvn.os like ?7 "
			+ " group by DATE_FORMAT(fuvn.visitTime ,'%Y-%m-%d') ,fuvn.channel ,u.company ,u.name ,ai.name")
	List<VisitCountVo> findDateCountNumberBy(String name, String company, String channel, String appName,
			Date startTime, Date endTime,String os);

	@Modifying
	@Transactional
	@Query(value="update FlowUserVisitNumber set os = ?2 where id = ?1 ")
	Integer updateOsById(Long id, String os);

}
