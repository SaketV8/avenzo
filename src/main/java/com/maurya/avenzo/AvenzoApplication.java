package com.maurya.avenzo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AvenzoApplication implements CommandLineRunner {

	// run method execute when all context of the application loaded
	@Override
	public void run(String... args) throws Exception {
		System.out.println();
		System.out.println();
		System.out.println("=======================================");
		System.out.println("🌸🌸 Let's Go 🌸🌸");
		System.out.println("=======================================");
		System.out.println();
		System.out.println();

		log.info("🐸 Application successfully started");
	}


	public static void main(String[] args) {
		SpringApplication.run(AvenzoApplication.class, args);
	}

}
