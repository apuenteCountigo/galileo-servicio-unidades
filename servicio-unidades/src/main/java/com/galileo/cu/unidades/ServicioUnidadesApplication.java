package com.galileo.cu.unidades;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@EntityScan({ "com.galileo.cu.commons.models", "com.galileo.cu.commons.dto" })
public class ServicioUnidadesApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ServicioUnidadesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("**************************************");
		System.out.println("UNIDADES V-2024-07-17 17:58");
	}

}
