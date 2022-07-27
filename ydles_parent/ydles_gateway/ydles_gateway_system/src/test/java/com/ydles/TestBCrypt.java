package com.ydles;

import org.springframework.security.crypto.bcrypt.BCrypt;


public class TestBCrypt {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            // 加盐
            String gensalt = BCrypt.gensalt();
            // 加盐加密
            String hashpw = BCrypt.hashpw("123456", gensalt);
            System.out.println("y盐:"+gensalt);
            System.out.println(hashpw);
            // 密码校验
            boolean checkpw = BCrypt.checkpw("123456", hashpw);
            System.out.println(checkpw);
        }

    }
}
