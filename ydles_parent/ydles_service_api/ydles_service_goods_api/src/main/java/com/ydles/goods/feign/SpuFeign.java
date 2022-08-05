package com.ydles.goods.feign;

import com.ydles.entity.Result;
import com.ydles.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goods")
public interface SpuFeign {
    @GetMapping("/spu/findSpuById/{id}")
    Result<Spu> findSpuById(@PathVariable String id);
}
