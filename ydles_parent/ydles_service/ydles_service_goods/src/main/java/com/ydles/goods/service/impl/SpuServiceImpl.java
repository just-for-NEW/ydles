package com.ydles.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.goods.dao.*;
import com.ydles.goods.pojo.*;
import com.ydles.goods.service.SpuService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ydles.util.IdWorker;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param spuId
     * @return
     */
    @Override
    public Goods findById(String spuId){
        // 1 spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        // 2 skuList

        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",spuId);
        List<Sku> skuList = skuMapper.selectByExample(example);

        // 3 封装goods对象中
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);

        return  goods;
    }


    @Autowired
    IdWorker idWorker;
    /**
     * 增加
     * @param goods
     */
    @Override
    public void add(Goods goods){
        // 1 spu插入
        Spu spu = goods.getSpu();
        long spuId = idWorker.nextId();
        spu.setId(spuId+"");
        // 未上架
        spu.setIsMarketable("0");
        // 启用
        spu.setIsEnableSpec("1");
        // 未删除
        spu.setIsDelete("0");
        // 未审核
        spu.setStatus("0");
        spuMapper.insertSelective(spu);

        // 2 skuList插入
        saveSkuList(goods);
    }

    @Autowired
    SkuMapper skuMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    BrandMapper brandMapper;
    @Autowired
    CategoryBrandMapper categoryBrandMapper;

    // skuList插入
    private void saveSkuList(Goods goods){
        List<Sku> skuList = goods.getSkuList();
        Spu spu = goods.getSpu();
        String spuName = spu.getName();
        String spuId = spu.getId();
        Integer category3Id = spu.getCategory3Id();
        Category category = categoryMapper.selectByPrimaryKey(category3Id);
        Integer brandId = spu.getBrandId();
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        for (Sku sku : skuList) {
            long skuId = idWorker.nextId();
            sku.setId(skuId+"");
            // skuName  spuName+spec 值
            String skuName = spuName;
            String specStr = sku.getSpec();
            if (StringUtils.isEmpty(specStr)){
                specStr = "{}";
            }
            Map specMap = JSON.parseObject(specStr, Map.class);
            if (specMap != null&&specMap.size() > 0){
                for (Object value : specMap.values()) {
                    skuName += "" + value;
                }
            }

            sku.setName(skuName);
            // time
            sku.setCreateTime(new Date());
            sku.setUpdateTime(new Date());
            // spuId
            sku.setSpuId(spuId);
            // category
            sku.setCategoryId(category.getId());
            sku.setCategoryName(category.getName());
            // brand
            sku.setBrandName(brand.getName());
            // 销量
            sku.setSaleNum(0);
            // 评论数
            sku.setCommentNum(0);
            // 通过审核
            sku.setStatus("1");

            skuMapper.insertSelective(sku);
        }

        // 品牌和插入数据 关系表 插入数据了
        // 1 查询 有没有关联
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(category3Id);
        categoryBrand.setBrandId(brandId);

        int selectCount = categoryBrandMapper.selectCount(categoryBrand);

        // 2 没有关联 插入关联
        if (selectCount <= 0){
           categoryBrandMapper.insert(categoryBrand);
        }
    }

    /**
     * 修改
     * @param goods
     */
    @Override
    public void update(Goods goods){

        // 1 spu
        Spu spu = goods.getSpu();
        spuMapper.updateByPrimaryKeySelective(spu);
        // 2 删除原来关联的sku
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",spu.getId());
        skuMapper.deleteByExample(example);
        // 3 插入修改带来的sku
        saveSkuList(goods);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 检查请求的商品是否存在，不存在则抛出运行异常带出信息
        if (spu == null){
            throw new RuntimeException("当前商品不存在！");
        }
         // 逻辑删除
         // 只能对已下架的商品操作
        if (!"0".equals(spu.getIsMarketable())){
            throw new RuntimeException("当前商品为上架状态，不能删除！");
        }

         //  修改spu表is_delete字段为1
        spu.setIsDelete("1");

        spuMapper.updateByPrimaryKeySelective(spu);


    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Spu>)spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spu>)spuMapper.selectByExample(example);
    }

    // 审核通过
    @Override
    public void audit(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 检查请求的商品是否存在，不存在则抛出运行异常带出信息
        if (spu == null){
            throw new RuntimeException("当前商品不存在！");
        }

        // 1 需要校验是否是被删除的商品
        if ("1".equals(spu.getIsDelete())){
            // 已删除则抛出运行异常带出信息
            throw new RuntimeException("当前商品已删除!");
        }
        // 2 如果未删除则修改审核状态为1
        spu.setStatus("1");
        // 3 并自动上架。
        spu.setIsMarketable("1");

        spuMapper.updateByPrimaryKeySelective(spu);

    }

    // 下架商品
    @Override
    public void pull(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 检查请求的商品是否存在，不存在则抛出运行异常带出信息
        if (spu == null){
            throw new RuntimeException("当前商品不存在！");
        }

        // 需要校验是否是被删除的商品
        if ("1".equals(spu.getIsDelete())){
            // 已删除则抛出运行异常带出信息
            throw new RuntimeException("当前商品已删除!");
        }

        // 如果未删除则修改上架状态为0
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);


    }

    // 上架商品
    @Override
    public void put(String id) {
        // 通过审核才能上架

        // 检查请求的商品是否存在，不存在则抛出运行异常带出信息
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            throw new RuntimeException("当前商品不存在！");
        }

        // 审核状态为1即通过审核，状态为0审核失败
        if (!"1".equals(spu.getStatus())){
            throw new RuntimeException("当前商品未通过审核，不能上架！");
        }

        // 审核通过，上架商品
        spu.setIsMarketable("1");

        spuMapper.updateByPrimaryKeySelective(spu);

    }

    // 还原商品
    @Override
    public void restore(String id) {
        // 检查请求的商品是否存在，不存在则抛出运行异常带出信息
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            throw new RuntimeException("当前商品不存在！");
        }

        // 恢复已删除状态的商品0，设置其已删除属性为0，未审核
        spu.setIsDelete("0");
        spu.setStatus("0");

        spuMapper.updateByPrimaryKeySelective(spu);
    }

    // 回收站删除商品(物理删除)
    @Override
    public void realDelete(String id) {
        // 检查请求的商品是否存在，不存在则抛出运行异常带出信息
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            throw new RuntimeException("当前商品不存在！");
        }

        // 判断必须是逻辑删除状态的商品即回收站的商品，才能物理删除
        if (!"1".equals(spu.getIsDelete())){
            throw new RuntimeException("不能删除非回收站的商品!");
        }
        // 执行物理删除
        spuMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Spu findSpuById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
           	}
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
           	}
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
           	}
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
           	}
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
           	}
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
           	}
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
           	}
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andEqualTo("isMarketable",searchMap.get("isMarketable"));
           	}
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
