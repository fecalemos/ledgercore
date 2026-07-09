package com.fecalemos.ledgercore;

import org.springframework.boot.SpringApplication;

public class TestLedgercoreApplication {

	public static void main(String[] args) {
		SpringApplication.from(LedgercoreApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
