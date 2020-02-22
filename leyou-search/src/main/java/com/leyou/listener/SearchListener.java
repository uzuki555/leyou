package com.leyou.listener;

import com.leyou.client.GoodsClient;
import com.leyou.item.pojo.Spu;
import com.leyou.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchListener {
    @Autowired
    public SearchService searchService;
    @Autowired
    public GoodsClient goodsClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.INDEX.CREATE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"item-insert","item-update"}

    ))
    public void listenCreateIndex(Long spuId){
        if(spuId==null){
            return;
        }
        this.searchService.createIndex(spuId);

    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.INDEX.DELETE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"item-delete"}

    ))
    public void listenrDeleteIndex(Long spuId){
        if(spuId==null){
            return;
        }
        searchService.deleteIndex(spuId);
    }

}
