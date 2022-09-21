package com.song.study.batch20220902;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@EnableBatchProcessing
@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class Batch20220902Application {

	public static void main(String[] args) {

		String[] newArgs = Arrays.copyOf(args, args.length + 1);
		newArgs[newArgs.length - 1] = "currentDate=" + LocalDateTime.now();

		SpringApplication.run(Batch20220902Application.class, args);
	}

}
