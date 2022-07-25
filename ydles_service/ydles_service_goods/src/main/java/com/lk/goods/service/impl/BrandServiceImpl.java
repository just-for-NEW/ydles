package com.lk.goods.service.impl;

import com.github.pagehelper.PageHelper;
import com.lk.goods.dao.BrandMapper;
import com.lk.goods.pojo.Brand;
import com.lk.goods.service.BrandService;
import com.ydles.entity.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        List<Brand> brands = brandMapper.selectAll();
        return brands;
    }

    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Brand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(Brand brand) {
        // update tb_brand set name="?" where id=?
        // 只修改实体类有的数据
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public List<Brand> search(Map<String, String> searchMap) {
        // sql:select * from tb_brand where letter=C and name like "%name%"
        Example example = new Example(Brand.class);
        // criteria 放搜索条件
        Example.Criteria criteria = example.createCriteria();
        // 判空逻辑
        if (searchMap != null){
            if (StringUtils.isNotBlank(searchMap.get("letter"))){
                // letter=C
                criteria.andEqualTo("letter",searchMap.get("letter"));
            }

            if (StringUtils.isNotBlank(searchMap.get("name"))){
                // name like "%name%"
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
        }

        List<Brand> brandList = brandMapper.selectByExample(example);
        return brandList;
    }

    @Override
    public List<Brand> findPage(Integer page, Integer size) {
        // limit 10,5
        PageHelper.startPage(page,size);
        // sql:select * from tb_brand limit 10,5
        return brandMapper.selectAll();
    }

    @Override
    // 需求：http://localhost:9011/brand/searchPage/2/5?lettrt=I&name="奈儿"
    public List<Brand> findPage(Integer page, Integer size, Map<String, String> searchMap) {
        // 1.先执行分页查询
        PageHelper.startPage(page,size);
        // 2.后执行条件查询
        Example example = new Example(Brand.class);
        // criteria 放搜索条件
        Example.Criteria criteria = example.createCriteria();
        // 判空逻辑
        if (searchMap != null){
            if (StringUtils.isNotBlank(searchMap.get("letter"))){
                // letter=C
                criteria.andEqualTo("letter",searchMap.get("letter"));
            }

            if (StringUtils.isNotBlank(searchMap.get("name"))){
                // name like "%name%"
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
        }
        List<Brand> brandList = brandMapper.selectByExample(example);
        return brandList;
    }

}
