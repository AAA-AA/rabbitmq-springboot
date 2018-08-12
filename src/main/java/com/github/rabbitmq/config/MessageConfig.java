package com.github.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : hongqiangren.
 * @since: 2018/8/12 15:18
 */
@Configuration
public class MessageConfig {
    public static final String MAIN_EXCHANGE = "demo.main.exchange";
    public static final String MAIN_ROUTING = "demo.main.routing";
    public static final String MAIN_QUEUE = "demo.main.queue";


    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE);
    }

    @Bean
    public Queue mainQueue() {
        return new Queue(MAIN_QUEUE);
    }

    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue()).to(mainExchange()).with(MAIN_ROUTING);
    }

}
