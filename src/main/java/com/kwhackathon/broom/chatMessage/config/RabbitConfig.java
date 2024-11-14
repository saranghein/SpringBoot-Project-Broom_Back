package com.kwhackathon.broom.chatMessage.config;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitConfig {

    // ChatMessageForCarpool 설정
    private static final String CARPOOL_QUEUE_NAME = "chat.carpool.queue";
    private static final String CARPOOL_EXCHANGE_NAME = "chat.carpool.exchange";
    private static final String CARPOOL_ROUTING_KEY = "chat.carpool.room.*";

    @Value("${spring.rabbitmq.username}")
    private String rabbitUser;
    @Value("${spring.rabbitmq.password}")
    private String rabbitPw;
    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;
    @Value("${spring.rabbitmq.virtual-host}")
    private String rabbitVh;
    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;



    @Bean
    public Queue carpoolQueue() {
        return new Queue(CARPOOL_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange carpoolExchange() {
        return new TopicExchange(CARPOOL_EXCHANGE_NAME);
    }

    @Bean
    public Binding carpoolBinding(){
        return BindingBuilder.bind(carpoolQueue()).to(carpoolExchange()).with(CARPOOL_ROUTING_KEY);
    }

    @Bean
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());

        //factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    // ChatMessageForTeam 설정
    private static final String TEAM_QUEUE_NAME = "chat.team.queue";
    private static final String TEAM_EXCHANGE_NAME = "chat.team.exchange";
    private static final String TEAM_ROUTING_KEY = "chat.team.room.*";

    @Bean
    public Queue teamQueue() {
        return new Queue(TEAM_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange teamExchange() {
        return new TopicExchange(TEAM_EXCHANGE_NAME);
    }

    @Bean
    public Binding teamBinding(){
        return BindingBuilder.bind(teamQueue()).to(teamExchange()).with(TEAM_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Primary
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(rabbitHost);
        factory.setPort(rabbitPort);
        factory.setVirtualHost(rabbitVh);
        factory.setUsername(rabbitUser);
        factory.setPassword(rabbitPw);

        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
