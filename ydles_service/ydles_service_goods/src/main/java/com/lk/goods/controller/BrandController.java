package com.lk.goods.controller;

import com.lk.goods.pojo.Brand;
import com.lk.goods.service.BrandService;
import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Component
@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    BrandService brandService;

    @GetMapping("findAll")
    public Result<List<Brand>> findAll(){
            List<Brand> brandList = brandService.findAll();
            return new Result<>(true, StatusCode.OK, "查询成功", brandList);
        }

    @GetMapping("{id}")
    public Result<Brand> findById(@PathVariable("id") Integer id){
        Brand brand = brandService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", brand);
    }

    @PostMapping("insert")
    public Result insert(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result<>(true,StatusCode.OK,"新增成功");
    }

    @PutMapping("update/{id}")
    public Result update(@RequestBody Brand brand,@PathVariable("id") Integer id){
        brand.setId(id);
        brandService.update(brand);
        return new Result<>(true,StatusCode.OK,"修改成功");
    }

    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable("id") Integer id){
        brandService.delete(id);
        return new Result<>(true,StatusCode.OK,"删除成功");
    }

    @GetMapping("search")
    public Result<List<Brand>> search(@RequestParam Map<String,String> searchMap){
        List<Brand> search = brandService.search(searchMap);
        return new Result<>(true,StatusCode.OK,"条件查询成功",search);
    }

    @GetMapping("search/{page}/{size}")
    public Result<List<Brand>> findPage(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        List<Brand> brandServicePage = brandService.findPage(page, size);
        return new Result<>(true,StatusCode.OK,"分页查询成功",brandServicePage);
    }

    @GetMapping("searchPage/{page}/{size}")
    public Result<List<Brand>>searchPage(@PathVariable("page") Integer page,@PathVariable("size") Integer size,@RequestParam Map<String,String> searchMap){
        List<Brand> pageSearch = brandService.findPage(page, size, searchMap);
        return new Result<>(true,StatusCode.OK,"分页条件查询成功",pageSearch);
    }
}
