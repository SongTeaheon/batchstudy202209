package com.song.study.batch20220902.job.manual;

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

import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ManualFileWriteJobConfig {

    private static final String JOB_NAME = "manualFileWriteJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job manualFileWriteJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(manualFileWriteStep())
                                .build();
    }

    @Bean
    public Step manualFileWriteStep() {
        return stepBuilderFactory.get("manualFileWriteStep")
                                 .<ManualItem, ManualItem>chunk(2)
                                 .reader(manualFileWriteReader())
                                 .processor(manualFileWriteProcessor())
                                 .writer(logWriterWriter())
                                 .build();
    }

    @Bean
    public ItemReader<ManualItem> manualFileWriteReader() {
        return new ListItemReader<>(List.of(
            ManualItem.builder().name("song").age(1L).build(),
            ManualItem.builder().name("kim").age(2L).build(),
            ManualItem.builder().name("jung").age(3L).build()
        ));
    }

    @Bean
    public ItemProcessor<ManualItem, ManualItem> manualFileWriteProcessor() {
        return item -> {
            log.info("[process] ManualItem name: {}, age: {}", item.getName(), item.getAge());
            return item;
        };
    }

    @Bean
    public ItemWriter<ManualItem> logWriterWriter() {
        return new ManualFileWriteWriter();
    }
}
