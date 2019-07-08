package cn.stylefeng.guns.modular.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.stylefeng.guns.modular.core.entity.BaseChannel;

public interface JpaBaseChannelRepository extends JpaRepository<BaseChannel, Integer> {

	BaseChannel findOneById(int id);

	@Query(value="from BaseChannel where id > 25")
	List<BaseChannel> findMoreByIdGrthan25();

}
