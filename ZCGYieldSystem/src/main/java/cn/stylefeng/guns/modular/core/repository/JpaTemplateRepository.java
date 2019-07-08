package cn.stylefeng.guns.modular.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.stylefeng.guns.modular.core.entity.Template;

public interface JpaTemplateRepository extends JpaRepository<Template, Integer> {

	Template findOneById(int id);

}
