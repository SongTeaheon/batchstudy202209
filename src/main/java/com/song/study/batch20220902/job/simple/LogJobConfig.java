package com.song.study.batch20220902.job.simple;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.song.study.batch20220902.job.simple.item.FileCreateItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LogJobConfig {

    private static final String JOB_NAME = "logJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job logJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(logStep())
                                .build();
    }

    @Bean
    public Step logStep() {
        return stepBuilderFactory.get("logStep")
            .<FileCreateItem, FileCreateItem>chunk(2)
            .reader(logReader())
            .processor(logProcessor())
            .writer(logWriter())
            .build();
    }

    @Bean
    public ItemReader<FileCreateItem> logReader() {
        return new ListItemReader<>(List.of(
            FileCreateItem.builder().name("song").age(1L).build(),
            FileCreateItem.builder().name("kim").age(2L).build(),
            FileCreateItem.builder().name("jung").age(3L).build()
        ));
    }

    @Bean
    public ItemProcessor<FileCreateItem, FileCreateItem> logProcessor() {
        return item -> {
            log.info("[process] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            return item;
        };
    }

    @Bean
    public ItemWriter<FileCreateItem> logWriter() {
        return items -> {
            for (FileCreateItem item : items) {
                log.info("[write] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            }
        };
    }
}
