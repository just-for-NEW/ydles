package com.lk.goods.service;

import com.lk.goods.pojo.Brand;
import java.util.List;
import java.util.Map;

public interface BrandService {
    // 查询所有品牌列表接口
    List<Brand> findAll();

    // 根据id查询
    Brand findById(Integer id);

    // 新增商品
    void add(Brand brand);

    // 修改商品信息
    void update(Brand brand);

    // 根据id删除
    void delete(Integer id);

    // 模糊查询
    List<Brand>search(Map<String,String> searchMap);

    // 分页查询
    List<Brand>findPage(Integer page,Integer size);

    // 模糊查询+分页
    List<Brand>findPage(Integer page,Integer size,Map<String,String> searchMap);
}
