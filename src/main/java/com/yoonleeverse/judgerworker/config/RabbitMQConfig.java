package com.yoonleeverse.judgerworker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableRabbit
public class RabbitMQConfig {

    public static final int CONCURRENT_CONSUMERS = 1;
    public static final String EXCHANGE_NAME = "server.exchange";
    public static final String JUDGE_QUEUE_NAME = "judge.queue";
    public static final String JUDGE_ROUTING_KEY = "server.judge.#";
    public static final String COMPLETE_QUEUE_NAME = "complete.queue";
    public static final String COMPLETE_ROUTING_KEY = "server.complete.#";

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding judgeBinding() {
        return BindingBuilder.bind(new Queue(JUDGE_QUEUE_NAME)).to(exchange()).with(JUDGE_ROUTING_KEY);
    }

    @Bean("containerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(CONCURRENT_CONSUMERS);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}
