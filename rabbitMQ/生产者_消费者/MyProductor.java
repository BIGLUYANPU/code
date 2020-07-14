package com.rocketdemo.rocketdemo.rabbit;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j(topic = "rabbitMQ-G-TMS")
public class MyProductor {

    @Resource
    private RabbitTemplate rabbitTemplate;

//    @Resource
//    private RedisTemplate<String, String> redisTemplate;

    public void sendData(String str) {
        //开始发送
//        String jsonStrng = JSONObject.toJSONString(str);
        log.info("Send message iDmsOrderModel-{}", str);
        String msgId = UUID.randomUUID().toString();
        Message message = MessageBuilder.withBody(str.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).setCorrelationId(msgId).setContentEncoding("UTF-8").build();
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(msgId);
        //构造数据
        //rabbitTemplate.setMandatory(true);

        rabbitTemplate.convertAndSend("TextExchangeText", "TextKeyText", message, correlationData);
        //broker失败处理
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
//        redisTemplate.opsForValue().set("GTMS-IDMS-" + msgId, jsonStrng);

    }

    //消息成功到交换机 或者 没有成功到交换机
    private RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
//        String messageId = correlationData.getId();
        if (!ack) {
            log.info("消费发送失败，correlationData:{}", correlationData.getId());
//            log.info("消息内容:{}", redisTemplate.opsForValue().get("GTMS-IDMS-"+correlationData.getId()));
        } else {
            log.info("消息的ID{}", correlationData.getId());
//            redisTemplate.expire("GTMS-IDMS-" + correlationData.getId(), 60, TimeUnit.SECONDS);
        }
    };

    //消息没有加入到队列中 会调用此方法
    private RabbitTemplate.ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routingKey) -> log.info("returnCallback:message:{}replyCode:{}replyText:{}exchange:{}routingKey:{}", message, replyCode, replyText, exchange, routingKey);


}
