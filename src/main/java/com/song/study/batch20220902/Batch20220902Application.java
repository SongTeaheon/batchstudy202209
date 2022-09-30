package com.song.study.batch20220902;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@MapperScan
@EnableBatchProcessing
@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class Batch20220902Application {

	public static void main(String[] args) {

		SpringApplication.run(Batch20220902Application.class, args);
	}

}
