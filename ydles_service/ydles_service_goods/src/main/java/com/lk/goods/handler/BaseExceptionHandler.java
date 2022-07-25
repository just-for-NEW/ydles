package com.lk.goods.handler;

import com.lk.goods.pojo.Brand;
import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

// 统一异常处理类
@ControllerAdvice // controller 增强类
public class BaseExceptionHandler {

    // 捕获IOException类型错误
    @ExceptionHandler(value = IOException.class)
    @ResponseBody
    public Result<Brand> error(IOException e){
        e.printStackTrace();
        return new Result<>(false, StatusCode.ERROR, e.getMessage());
    }


    // 常见错误均无法匹配，则捕获Exception类型错误
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<Brand> error(Exception e){
        e.printStackTrace();
        return new Result<>(false, StatusCode.ERROR, e.getMessage());
    }

}
