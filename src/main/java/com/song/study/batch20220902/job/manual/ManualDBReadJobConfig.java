package com.song.study.batch20220902.job.manual;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.song.study.batch20220902.dbio.domain.Product;
import com.song.study.batch20220902.job.manual.item.CsvLine;
import com.song.study.batch20220902.job.manual.item.ManualFooter;
import com.song.study.batch20220902.job.manual.item.ManualHeader;
import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ManualDBReadJobConfig {

    private static final String JOB_NAME = "manualDBReadJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ManualDBReadReader manualDBReadReader;

    @Bean
    public Job manualDBReadJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(manualDBReadStep())
                                .build();
    }

    @Bean
    public Step manualDBReadStep() {
        return stepBuilderFactory.get("manualDBReadStep")
                                 .<Product, ManualItem>chunk(2)
                                 .reader(manualDBReadReader)
                                 .processor(manualDBReadProcessor())
                                 .writer(manualDBReadWriter())
                                 .build();
    }

    @Bean
    public ItemProcessor<Product, ManualItem> manualDBReadProcessor() {
        return item -> {
            log.info("[process] body: {}, {}", item.getName(), item.getId());
            return ManualItem.builder().name(item.getName()).age(item.getId()).build();
        };
    }


    private ItemWriter<ManualItem> manualDBReadWriter() {
        return items -> {
            for (ManualItem item : items) {
                log.info("[write] writer: {}, {}", item.getName(), item.getAge());
            }
        };
    }

}
