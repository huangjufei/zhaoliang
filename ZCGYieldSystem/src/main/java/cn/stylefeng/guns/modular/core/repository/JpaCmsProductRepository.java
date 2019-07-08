package cn.stylefeng.guns.modular.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.stylefeng.guns.modular.core.entity.CmsProduct;

public interface JpaCmsProductRepository extends JpaRepository<CmsProduct, Integer> {

}
