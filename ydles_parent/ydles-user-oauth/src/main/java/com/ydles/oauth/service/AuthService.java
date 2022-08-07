package com.ydles.oauth.service;

import com.ydles.oauth.util.AuthToken;

public interface AuthService {
    // 申请令牌
    AuthToken login(String username, String password, String clientId, String clientSecret);

}
