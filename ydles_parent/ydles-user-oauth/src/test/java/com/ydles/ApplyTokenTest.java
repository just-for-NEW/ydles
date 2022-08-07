package com.ydles;

import com.ydles.entity.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SpringBootTest(classes = OAuthApplication.class)
@RunWith(SpringRunner.class)
public class ApplyTokenTest {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;

    // 测试代码申请令牌
    @Test
    public void applyToken(){
        // 1 http://localhost:9200/oauth/token
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
        //  http://localhost:9200
        URI uri = serviceInstance.getUri();
        String url = uri + "/oauth/token";

        // 2.1 请求头
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization",getHttpHeaders("ydlershe","ydlershe"));

        // 2.2 请求体
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username","666");
        body.add("password","itlils");

        // 2 构建请求
        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(body,headers);
        // 状态码 400 401 不在后端报错，返回给前端
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                // 响应当中有错误(400,401) 具体处理方法实现
                if (response.getRawStatusCode() != 400&&response.getRawStatusCode() !=401){
                    super.handleError(url,method,response);
                }
            }
        });
        // 3 发请求
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        // 4 请求中 拿数据
        Map body1 = exchange.getBody();
        System.out.println(body1);

    }
    // 请求头中Authorization值的计算方法
    public String getHttpHeaders(String clientId,String clientSecret){
        // Basic base64(客户端：客户端密码)
        String str = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(str.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encode);
    }
}
