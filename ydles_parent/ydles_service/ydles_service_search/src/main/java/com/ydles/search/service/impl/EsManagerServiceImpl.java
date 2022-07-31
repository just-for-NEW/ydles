package com.ydles.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.ydles.goods.feign.SkuFeign;
import com.ydles.goods.pojo.Sku;
import com.ydles.search.dao.ESManagerMapper;
import com.ydles.search.pojo.SkuInfo;
import com.ydles.search.service.EsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EsManagerServiceImpl implements EsManagerService {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    SkuFeign skuFeign;
    @Autowired
    ESManagerMapper esManagerMapper;
    // 1创建索引库，映射
    @Override
    public void createIndexAndMapping() {
        // 创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);
        // 创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    // 2现有所有sku导入es
    @Override
    public void importData() {
        // 查到现有所有sku(启用后门)
        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        System.out.println("查询出的skuList多少："+skuList.size());

        // 2导入es
        List<SkuInfo> skuinfoList = new ArrayList<>();
        for (Sku sku : skuList) {
            String skuStr = JSON.toJSONString(sku);
            SkuInfo skuInfo = JSON.parseObject(skuStr, SkuInfo.class);

            // 需求 Sku.spec string ------>SkuInfo.specMap Map
            Map map = JSON.parseObject(sku.getSpec(), Map.class);
            skuInfo.setSpecMap(map);

            skuinfoList.add(skuInfo);
        }
        esManagerMapper.saveAll(skuinfoList);



    }

    // 3 根据spuId,将他的skuList导入es
    @Override
    public void importDataBySpuId(String spuId) {

        // 1 查到spuId对应的sku
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        System.out.println("查询出的skuList多少："+skuList.size());

        // 2导入es
        List<SkuInfo> skuinfoList = new ArrayList<>();
        for (Sku sku : skuList) {
            String skuStr = JSON.toJSONString(sku);
            SkuInfo skuInfo = JSON.parseObject(skuStr, SkuInfo.class);

            // 需求 Sku.spec string ------>SkuInfo.specMap Map
            Map map = JSON.parseObject(sku.getSpec(), Map.class);
            skuInfo.setSpecMap(map);

            skuinfoList.add(skuInfo);
        }
        esManagerMapper.saveAll(skuinfoList);
    }

    @Override
    public void deleteDataBySpuId(String spuId) {
        // 1 查到spuId对应的sku
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        // 2 根据id删除
        for (Sku sku : skuList) {
            System.out.println("要删除的sku:" + sku);
            esManagerMapper.deleteById(Long.parseLong(sku.getId()));
        }
    }
}
