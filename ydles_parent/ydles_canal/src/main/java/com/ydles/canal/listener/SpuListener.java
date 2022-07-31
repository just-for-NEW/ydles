package com.ydles.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.ydles.canal.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


/**
 * spu 表更新(上下架)
 */
@CanalEventListener
public class SpuListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ListenPoint(schema = "ydles_goods", table = {"tb_spu"},eventType = CanalEntry.EventType.UPDATE )
    public void spuExchange(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.err.println("tb_spu表数据发生变化" + eventType);


        // 修改前数据
        Map <String,String>oldMap=new HashMap<>();
        for(CanalEntry.Column column: rowData.getBeforeColumnsList()) {
            oldMap.put(column.getName(),column.getValue());
        }

        // 修改后数据
        Map<String,String> newMap=new HashMap<>();
        for(CanalEntry.Column column: rowData.getAfterColumnsList()) {
            newMap.put(column.getName(),column.getValue());
        }

        // is_marketable字段

        // 由0改为1表示上架，监听上架信息
        if("0".equals(oldMap.get("is_marketable")) && "1".equals(newMap.get("is_marketable")) ){
            // 发送到mq商品上架交换器上
            String spuId = newMap.get("id");
            System.out.println("商品上架了，往mq发消息"+ spuId);
            rabbitTemplate.convertAndSend(RabbitMQConfig.GOODS_UP_EXCHANGE,"",spuId);
        }

        // 由1改为0表示下架，监听下架信息
        if("1".equals(oldMap.get("is_marketable")) && "0".equals(newMap.get("is_marketable")) ){
            // 发送到mq商品下架交换器上
            String spuId = oldMap.get("id");
            System.out.println("商品下架了，往mq发消息" + spuId);
            rabbitTemplate.convertAndSend(RabbitMQConfig.GOODS_DOWN_EXCHANGE,"",spuId);
        }
    }
}
