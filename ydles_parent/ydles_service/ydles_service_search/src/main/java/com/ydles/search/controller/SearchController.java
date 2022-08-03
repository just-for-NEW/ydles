package com.ydles.search.controller;

import com.ydles.entity.Page;
import com.ydles.search.pojo.SkuInfo;
import com.ydles.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * es检索
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    // 搜索数据返回
    @GetMapping
    @ResponseBody
    public Map search(@RequestParam Map<String,String> searchMap){
        // 特殊符号处理
        this.handleSearchMap(searchMap);

        return searchService.search(searchMap);
    }

    // 承接搜索页面的请求
    @GetMapping("/list")
    public String list(@RequestParam Map<String,String>searchMap, Model model){
        // 特殊符号处理
        this.handleSearchMap(searchMap);

        // 获取查询结果
        Map<String,Object> resultMap = searchService.search(searchMap);
        model.addAttribute("result",resultMap);
        // 用户搜索条件
        model.addAttribute("searchMap",searchMap);

        // 记录url 记录这次的搜索条件，前端不会丢失上一次的条件
        StringBuilder url = new StringBuilder("/search/list");
        // 判断有没有搜索条件
        if (searchMap != null&& searchMap.size() > 0){
            url.append("?");

            for (String key : searchMap.keySet()) {
                // 排除一些不需要的搜索条件
                if (!key.equals("sortField")&&!key.equals("sortRule")
                &&!key.equals("pageNum")&&!key.equals("pageSize")){
                    // searchMap keywords=包包 spec_颜色=黑色
                    url.append(key).append("=").append(searchMap.get(key)).append("&");
                }

            }
            String urlString = url.toString();
            // 截掉最后一个字符：&
            urlString = urlString.substring(0, urlString.length() - 1);
            model.addAttribute("url",urlString);
        }else {
            model.addAttribute("url",url);
        }

         // 分页
        Page<SkuInfo> page = new Page<>(Long.parseLong(String.valueOf(resultMap.get("total")))
        ,Integer.parseInt(String.valueOf(resultMap.get("pageNum"))),Integer.parseInt(String
                .valueOf(resultMap.get("pageSize"))));
        model.addAttribute("page",page);

        return "search";
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
