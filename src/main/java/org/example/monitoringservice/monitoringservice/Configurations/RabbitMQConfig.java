package org.example.monitoringservice.monitoringservice.Configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE = "mailQueue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, false);
    }
}
