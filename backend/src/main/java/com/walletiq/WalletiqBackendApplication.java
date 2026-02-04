package com.walletiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.walletiq.config.properties")
public class WalletiqBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletiqBackendApplication.class, args);
	}

}
