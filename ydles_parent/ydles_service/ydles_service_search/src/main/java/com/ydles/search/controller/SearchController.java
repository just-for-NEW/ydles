package com.ydles.search.controller;

import com.ydles.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * es检索
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping
    public Map search(@RequestParam Map<String,String> searchMap){
        // 特殊符号处理
        this.handleSearchMap(searchMap);

        return searchService.search(searchMap);
    }

    // 参数值 key:"spec_"开头的  value: + ---》 %2B
    private void handleSearchMap(Map<String, String> searchMap) {
        Set<Map.Entry<String,String>> entries = searchMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            if (key.startsWith("spec_")){
                String replace = entry.getValue().replace("+", "%2B");
                searchMap.put(key,replace);
            }
        }
    }
}
