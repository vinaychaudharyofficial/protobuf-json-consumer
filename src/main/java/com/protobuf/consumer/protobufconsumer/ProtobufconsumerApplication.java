package com.protobuf.consumer.protobufconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan(basePackages = "com.protobuf")
public class ProtobufconsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProtobufconsumerApplication.class, args);
	}

	
}
