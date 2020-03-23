package com.new4net.sso.core;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public final static String USER_SAVE_QUEUE = "USER_SAVE_QUEUE";
    public final static String USER_EXCHANGE="USER_EXCHANGE";
    public final static String ROUTINGKEY="ROUTINGKEY";

    //队列 起名：TestDirectQueue
    @Bean
    public Queue userSaveQueue() {
        return new Queue(USER_SAVE_QUEUE,true);  //true 是否持久
    }

    //Direct交换机 起名：TestDirectExchange
    @Bean
    DirectExchange userExchange() {
        return new DirectExchange(USER_EXCHANGE);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(userSaveQueue()).to(userExchange()).with(ROUTINGKEY);
    }
}