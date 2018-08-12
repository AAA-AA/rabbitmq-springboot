package com.github.rabbitmq.mq;

import com.alibaba.fastjson.JSON;
import com.github.rabbitmq.config.MessageConfig;
import com.github.rabbitmq.domain.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author : hongqiangren.
 * @since: 2018/8/12 15:24
 */
@Service
public class Consumer {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    @RabbitListener(queues = MessageConfig.MAIN_QUEUE)
    public void execute(Message message) {
        log.info("消息接收时间：{}",new Date());
        UserDto user = JSON.parseObject(message.getBody(),UserDto.class);
        System.out.println(user.getNickName());
    }
}
