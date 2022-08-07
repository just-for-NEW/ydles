package com.ydles;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

public class ParseJWTTest {
    @Test
    public void parseJwt(){
        //基于公钥去解析jwt
        String jwt ="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRyZXNzIjoidGFpeXVhbiIsImNvbnBhbnkiOiJpdGxpbHMifQ.oG8bRMBsBpjSWM6joE8VgHOhv_3wrXio9TOHu-km2LYJOKEV-D3q4oMqwKl22Aq6qwsCS182CuHICdxqWqj_25l_-be1YGnq_MnaD_z1um2vhxIKOiPnsSF1f5IU1Tutkvd2ajomX2N3R3FDh6X_9MPkgSnX20hOt9e57pwAy9RkV59QH6PsrGra-6Hj3UF7pxqoTj6Sly8OjALESt3PSlEg4UaVfD-BpyZAM0YNuN0RXHBYXUrS5oOhHs7N6fSO-cWxq_xN4nonrPL7W9ohNKAi5-HDjG0D-0tMfQPmwZ0k2clVGCLAxGrmA3SQq4P5-kE4AqYAW_S_DIfx35jLHg";

        String publicKey ="-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuKtFwPGgk2DUeUbk0YBPgkyaCoygEl5J4zgNosYuuoLxdMsB+cNw8xZ/CZdmxLJEJyDh2Ni4Ytw0F0hFWq7lpeia6tAoeYgEqZ+7rsxJcJQnDVXG0sMI/O1o3p5fzfll0QQCVt6nZdX4gV8ojjlmltA1iUgERrR+CspgmgkS+Wwls/8NqQ0N2ubwUjEaKwaUBszPiabkZlIWKd+UaxG1+7nZY2wzDj4lBcIQZBLYtDh2jTyTkFh4ZbCcSWqxzSFWuV6ctNdrSYBhcTBpFPTy8oV/TO1oppKUeEUaozhlwSOwY6LAKXlQaadXmypIYbq+GhyIBpMJqz+KGzncCMnbfwIDAQAB-----END PUBLIC KEY-----";

        //解析令牌
        Jwt token = JwtHelper.decodeAndVerify(jwt, new RsaVerifier(publicKey));
        //获取负载
        String claims = token.getClaims();
        System.out.println(claims);

        String ydlershe = BCrypt.hashpw("itlils666", BCrypt.gensalt());
        System.out.println(ydlershe);
    }
}
