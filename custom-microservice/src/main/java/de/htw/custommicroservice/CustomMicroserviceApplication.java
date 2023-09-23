package de.htw.custommicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomMicroserviceApplication.class, args);
	}
}
