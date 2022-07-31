package com.ydles.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.ydles.canal.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@CanalEventListener  // canal监听类
public class BusinessListener {


    /**
     * 数据监控服务，监控tb_ad表，当发生增删改查操作时，提取position值（广告位置key）,发送到rabbitmq
     */

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 标明具体库和表
    @ListenPoint(schema = "ydles_business", table = {"tb_ad"})
    public void adUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.err.println("广告数据发生变化");
        System.out.println("EventType = " + eventType);

        // 改变之前 这一行数据
        rowData.getBeforeColumnsList().forEach(c->System.out.println("改变之前，列名字："+c.getName()+"列值："+c.getValue()));



        // 修改前数据
        /*List<CanalEntry.Column> beforeColumnList = rowData.getBeforeColumnsList();
        for(CanalEntry.Column column: beforeColumnList) {
            if(column.getName().equals("position")){
                System.out.println("发送消息到mq  ad_update_queue:"+column.getValue());
                // 发送消息到mq
                rabbitTemplate.convertAndSend("", RabbitMQConfig.AD_UPDATE_QUEUE,column.getValue());
                break;
            }
        }*/

        // 修改后数据
        List<CanalEntry.Column> afterColumnList = rowData.getAfterColumnsList();
        for(CanalEntry.Column column: afterColumnList) {
            if(column.getName().equals("position")){
                System.out.println("发送消息到mq  ad_update_queue:"+column.getValue());
                // 发送消息到mq
                rabbitTemplate.convertAndSend("",RabbitMQConfig.AD_UPDATE_QUEUE,column.getValue());
                break;
            }
        }


        System.out.println("======================================");

        // 改变之后 这一行数据
         rowData.getAfterColumnsList().forEach(c->System.out.println("改变之后，列名字："+c.getName()+"列值："+c.getValue()));




    }
}
