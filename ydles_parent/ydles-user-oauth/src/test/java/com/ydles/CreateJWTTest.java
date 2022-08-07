package com.ydles;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class CreateJWTTest {
    @Test
    public void createJWT() {
        // 基于私钥生成jwt
        // 1创建秘钥工厂

        // 1.1秘钥位置
        ClassPathResource classPathResource = new ClassPathResource("ydlershe.jks");
        //1.2秘钥库密码
        String keyPass = "ydlershe";
        /**
         * 1秘钥位置
         * 2秘钥库密码
         */
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, keyPass.toCharArray());

        // 2基于工厂拿到私钥
        String alias="ydlershe";
        String password="ydlershe";
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, password.toCharArray());

        //转化为rsa私钥
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyPair.getPrivate();

        // 3生成jwt
        Map<String,String> map=new HashMap<>();
        map.put("conpany", "itlils");
        map.put("address", "taiyuan");
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(rsaPrivateKey));

        String jwtEncoded = jwt.getEncoded();
        System.out.println("jwtEncoded:"+jwtEncoded);
        String claims = jwt.getClaims();
        System.out.println("claims:"+claims);

    }
}
