package com.leyou.listener;

import com.aliyuncs.exceptions.ClientException;

import com.leyou.properties.springProperties;
import com.leyou.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.util.Map;

@Component
@EnableConfigurationProperties(springProperties.class)
public class SmsListener {
    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private springProperties springProperties;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SMS.SEND",durable = "true"),
            exchange = @Exchange(value = "LEYOU.SMS.EXCHANGE",type = "topic",ignoreDeclarationExceptions = "true"),
            key = {"verify.code"}
    ))
    public  void send(Map<String,String> phoneMap) throws ClientException {
        if(CollectionUtils.isEmpty(phoneMap)){
            return;
        }
        String phone = phoneMap.get("phone");
        String code = phoneMap.get("code");

        if(StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(code)){
            smsUtils.sendSms(phone,code,springProperties.getSignName(),springProperties.getTemplateCode());
        }

    }
}
