package com.ydles.goods.feign;

import com.ydles.entity.Result;
import com.ydles.goods.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goods")
public interface CategoryFeign {
    @GetMapping("/category/{id}")
     Result<Category> findById(@PathVariable("id") Integer id);
}

