package com.lk.goods.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/demo")
@RestController //组合注解
public class DemoController {
    @GetMapping("/test")
    public String demo(){
        return "demo mesage";
    }
}