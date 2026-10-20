package com.cards.transaction;

import org.springframework.boot.SpringApplication;

public class TestCardTransactionApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(CardTransactionApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
