package com.github.rabbitmq.mq;

import com.alibaba.fastjson.JSON;
import com.github.rabbitmq.domain.UserDto;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.springframework.amqp.support.converter.AbstractJsonMessageConverter.DEFAULT_CHARSET;

/**
 * @author : hongqiangren.
 * @since: 2018/8/12 15:24
 */
@Service
public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);
    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitTemplate amqpTemplate;

    private static final String DELAY_EXCHANGE = "demo.exchange.delay";
    private static final String DELAY_QUEUE_NAME = "demo.exchange.delay.queue";
    private static final String DELAY_ROUTING_KEY = "demo.exchange.delay.routing";

    private static final String MAIN_EXCHANGE = "demo.main.exchange";
    private static final String MAIN_ROUTING = "demo.main.routing";



    public void sendDelayMsg(Object t, long time, TimeUnit unit) {
        /**方案一：简洁易理解*/
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        log.info("准备发送！"+new Date());
        executorService.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    amqpTemplate.convertAndSend(MAIN_EXCHANGE, MAIN_ROUTING, JSON.toJSONString(t).getBytes(DEFAULT_CHARSET));
                    log.info("已经发送：" + new Date());
                } catch (AmqpException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    log.error("发送异常：",e);
                }
                return "ok";
            }
        },time,unit);
        /**方案二：更优雅***/
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(false);

        try {
            channel.exchangeDeclare(DELAY_EXCHANGE, ExchangeTypes.DIRECT, true);
            channel.queueDeclare(DELAY_QUEUE_NAME, true, false, false, delayProperties(MAIN_EXCHANGE, MAIN_ROUTING));
            channel.queueBind(DELAY_QUEUE_NAME, DELAY_EXCHANGE, DELAY_ROUTING_KEY);
            // 设置延迟时间并持久化
            AMQP.BasicProperties properties = new AMQP.BasicProperties
                    .Builder()
                    .contentEncoding(DEFAULT_CHARSET)
                    .contentType(MessageProperties.CONTENT_TYPE_JSON)
                    .expiration(String.valueOf(unit.toMillis(time)))
                    .deliveryMode(MessageDeliveryMode.toInt(MessageDeliveryMode.PERSISTENT))
                    .priority(0)
                    .build();
            log.info("发送消息时间：{}",new Date());
            channel.basicPublish(DELAY_EXCHANGE, DELAY_ROUTING_KEY, properties, JSON.toJSONString(t).getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Object> delayProperties(String mainExchange, String mainRouting) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("x-dead-letter-exchange", mainExchange);
        properties.put("x-dead-letter-routing-key", mainRouting);
        return properties;
    }

    @PostConstruct
    public void afterInit() {
        UserDto userDto = new UserDto();
        userDto.setNickName("kaka");
        userDto.setId(20L);
        sendDelayMsg(userDto,30,TimeUnit.SECONDS);
    }

}
