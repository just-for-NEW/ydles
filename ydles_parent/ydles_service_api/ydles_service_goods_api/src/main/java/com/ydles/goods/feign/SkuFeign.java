package com.ydles.goods.feign;

import com.ydles.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "goods")
public interface SkuFeign {

    // 根据spuId查询skuList 也可全部查询
    @GetMapping("/sku/spu/{spuId}")
     List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

}
