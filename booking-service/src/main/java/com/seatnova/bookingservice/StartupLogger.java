package com.seatnova.bookingservice;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component

public class StartupLogger {

	@Value("${spring.data.redis.host}")

	private String host;

	@Value("${spring.data.redis.port}")

	private String port;

	@PostConstruct

	public void print() {

		System.out.println("Redis host = " + host);

		System.out.println("Redis port = " + port);

	}

}
