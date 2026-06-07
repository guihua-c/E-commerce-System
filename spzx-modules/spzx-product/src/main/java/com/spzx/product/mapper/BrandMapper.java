package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.api.domain.Brand;

import java.util.List;

//@Repository
public interface BrandMapper extends BaseMapper<Brand> {
    List<Brand> list(/*@Param("brand") */Brand brand);

    Brand getById(Long id);

    int insert(Brand brand);

    int update(Brand brand);

    int deleteBatch(/*@Param("ids")*/ Long[] ids);

    List<Brand> selectBrandAll();
}
