package com.ydles.user.feign;

import com.ydles.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user")
public interface UserFeign {
    @GetMapping("/user/load/{username}")
     User findUserInfo(@PathVariable("username") String username);
}
