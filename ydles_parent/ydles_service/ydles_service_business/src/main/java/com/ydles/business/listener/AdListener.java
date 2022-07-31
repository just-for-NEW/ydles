package com.ydles.business.listener;

import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
public class AdListener {

    @Autowired
    RestTemplate restTemplate;



    @RabbitListener(queues = "ad_update_queue")
     public void receiveMsg(String position){

        System.out.println("接收到了广告的消息，位置：" + position);

        // 发请求 http://192.168.200.128/ad_update?position=web_index_lb
        String url= "http://192.168.200.128/ad_update?position=" + position;

        // 1.创建okHttpClient对象
//        OkHttpClient okHttpClient = new OkHttpClient();
//        // 2.创建一个 request对象
//        final Request request = new Request.Builder()
//                .url(url).build();
//        // 新建一个call对象
//        Call call = okHttpClient.newCall(request);
//        // 4.请求加入调度,这里是异步Get请求回调
//        call.enqueue(new Callback() {
//            // 失败处理
//            @Override
//            public void onFailure(Call call, IOException e) {
//                System.out.println("发送请求失败");
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                System.out.println("发送请求成功" + response.message());
//            }
//        });

        restTemplate.getForObject(url, Map.class);

    }
}
