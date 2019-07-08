package cn.stylefeng.guns.modular.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.AppInfo;

public interface JpaAppInfoRepository extends JpaRepository<AppInfo, Integer>{

	@Query(value="from AppInfo where templateId = ?1")
	List<AppInfo> findAppVersionByTemplateId(int templateId);

	AppInfo findOneById(int id);

}
