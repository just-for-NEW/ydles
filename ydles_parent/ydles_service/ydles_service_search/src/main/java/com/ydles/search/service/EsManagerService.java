package com.ydles.search.service;

public interface EsManagerService {
    // 1 创建索引库,映射
    void createIndexAndMapping();

    // 2 现有所有sku导入es
     void importData();

    // 3 根据spuId,将他的skuList导入es
     void importDataBySpuId(String spuId);

    // 4 根据spuId删除es索引库中相关的sku数据
    void deleteDataBySpuId(String spuId);

}
