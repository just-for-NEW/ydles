package com.ydles.search.listener;

import com.ydles.search.config.RabbitMQConfig;
import com.ydles.search.service.EsManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品上下架消息监听
 */
@Component
public class GoodChangeListener {

    @Autowired
    EsManagerService esManagerService;

    // 上架监听
    @RabbitListener(queues = RabbitMQConfig.SEARCH_ADD_QUEUE)
    public void receiveUPMsg(String spuId){
        System.out.println("监听到了商品上架了！：" + spuId);

        esManagerService.importDataBySpuId(spuId);
    }

    // 下架监听
    @RabbitListener(queues = RabbitMQConfig.SEARCH_DELETE_QUEUE)
    public void receiveDeleteMsg(String spuId){
        System.out.println("监听到了商品下架了！：" + spuId);

        esManagerService.deleteDataBySpuId(spuId);
    }


}
