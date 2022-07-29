package com.ydles.goods.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

// 前端给我们后端传 spu skuList
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;
}
