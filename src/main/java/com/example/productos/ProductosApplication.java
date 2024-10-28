package com.example.productos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ProductosApplication {

	@Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${nats.url}")
    private String natsUrl;

	public static void main(String[] args) {
		SpringApplication.run(ProductosApplication.class, args);
	}

	@PostConstruct
    public void init() {
		System.out.println("AAA");
        System.out.println("Datasource URL: " + datasourceUrl);
        System.out.println("Datasource Username: " + datasourceUsername);
        System.out.println("Datasource Password: " + datasourcePassword);
        System.out.println("NATS Url: " + natsUrl);
    }

}
