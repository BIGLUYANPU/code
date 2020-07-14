package com.rocketdemo.rocketdemo.rabbit;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j(topic = "consumer-mq")
public class MyConsumer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "TextQueueText"),
            exchange = @Exchange(name = "TextExchangeText"),
            key = "TextKeyText"
    ),
    concurrency = "2-10")
    @RabbitHandler
    public void test(Message message, Channel channel){
        try{
            System.out.println(Thread.currentThread().getId()+":"+new String(message.getBody(), StandardCharsets.UTF_8));
//            System.out.println("consumer--:"+message.getMessageProperties()+":"+);
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch(Exception e){
            e.printStackTrace();//TODO 业务处理
            //加了下面这句会拒绝，丢弃这条数据
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
            //加了下面这句仍然会扔回队列
//            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }

    }
}
