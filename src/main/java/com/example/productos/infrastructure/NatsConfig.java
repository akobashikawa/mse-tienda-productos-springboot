package com.example.productos.infrastructure;

import io.nats.client.Connection;
import io.nats.client.Nats;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class NatsConfig {

    @Value("${nats.url}")
    private String natsServerUrl;

    @Bean
    public Connection natsConnection() throws Exception {
        return Nats.connect(natsServerUrl);
    }
}

