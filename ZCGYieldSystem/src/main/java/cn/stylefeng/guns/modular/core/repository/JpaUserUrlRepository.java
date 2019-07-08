package cn.stylefeng.guns.modular.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.UserUrl;
import cn.stylefeng.guns.modular.core.vo.UserUrlVo;

public interface JpaUserUrlRepository extends JpaRepository<UserUrl, Long>{

	UserUrl findOneByUserIdAndChannelAndAppId(Long id, String channel, Integer appId);

	@Query(value="select new cn.stylefeng.guns.modular.core.vo.UserUrlVo("
			+ " uu.id ,u.id ,apk.id ,uu.recommendKey ,uu.channel "
			+ ", uu.channelName ,uu.url ,uu.createTime ,apk.name ,u.name ,u.company) "
			+ " from UserUrl as uu "
			+ " inner join ApkInfo as apk on apk.id = uu.appId and apk.status = '上架'  and apk.name like ?4 "
			+ " inner join User as u on u.id = uu.userId "
			+ " where uu.channel like ?2 and u.name like ?1 and u.company like ?3")
	List<UserUrlVo> findPage(String name ,String channel ,String company,String appName,Pageable pageable);

	@Query(value="select new cn.stylefeng.guns.modular.core.vo.UserUrlVo(uu.id ,u.id ,apk.id ,uu.recommendKey ,uu.channel "
			+ ", uu.channelName ,uu.url ,uu.createTime ,apk.name ,u.name ,u.company) from UserUrl as uu inner join "
			+ " ApkInfo as apk on apk.id = uu.appId and apk.status = '上架'  inner join User as u on u.id = uu.userId where (uu.channel like ?1 "
			+ " or u.name like ?1 or apk.name like ?1 or uu.channelName like ?1) and (u.id in (select ur.userId from UserRole "
			+ " as ur where ur.roleId in ?2 ) or u.id = ?4) and u.company = ?3")
	List<UserUrlVo> findPageInRoleListAndByCompanyAndUserId(String name, List<Long> allRoleList,String company ,Long userId,Pageable pageable);
	
	@Query(value="select new cn.stylefeng.guns.modular.core.vo.UserUrlVo"
			+ " (uu.id ,u.id ,apk.id ,uu.recommendKey ,uu.channel "
			+ " , uu.channelName ,uu.url ,uu.createTime ,apk.name ,u.name ,u.company) from UserUrl as uu "
			+ " inner join ApkInfo as apk on apk.id = uu.appId and apk.status = '上架' "
			+ " inner join User as u on u.id = uu.userId where (uu.channel like ?1 "
			+ " or u.name like ?1 or apk.name like ?1 or uu.channelName like ?1) "
			+ " and u.company = ?2 and u.id = ?3")
	List<UserUrlVo> findPageByCompanyAndUserId(String name,String company ,Long userId ,Pageable pageable);
}
