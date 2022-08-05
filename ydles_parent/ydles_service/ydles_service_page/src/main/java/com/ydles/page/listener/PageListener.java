package com.ydles.page.listener;

import com.ydles.page.config.RabbitMQConfig;
import com.ydles.page.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageListener {
    @Autowired
    PageService pageService;

    @RabbitListener(queues = RabbitMQConfig.PAGE_CREATE_QUEUE)
    public void receiveMsg(String spuId){
        System.out.println("监听到商品上架了，生成静态页面了：" + spuId);

        pageService.generateHtml(spuId);
    }

}
