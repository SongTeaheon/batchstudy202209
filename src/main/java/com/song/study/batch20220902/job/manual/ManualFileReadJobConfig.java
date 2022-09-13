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

import com.song.study.batch20220902.job.manual.item.CsvLine;
import com.song.study.batch20220902.job.manual.item.ManualFooter;
import com.song.study.batch20220902.job.manual.item.ManualHeader;
import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ManualFileReadJobConfig {

    private static final String JOB_NAME = "manualFileReadJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ManualFileReadReader manualFileReadReader;

    @Bean
    public Job manualFileReadJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(manualFileReadStep())
                                .build();
    }

    @Bean
    public Step manualFileReadStep() {
        return stepBuilderFactory.get("manualFileReadStep")
                                 .<CsvLine, ManualItem>chunk(2)
                                 .reader(manualFileReadReader)
                                 .processor(manualFileReadProcessor())
                                 .writer(manualFileReadWriter())
                                 .build();
    }

    @Bean
    public ItemProcessor<CsvLine, ManualItem> manualFileReadProcessor() {
        return item -> {
            if (item instanceof ManualHeader header) {
                log.info("[process] header check: {}", header.getDescription());
                return null;
            }

            if (item instanceof ManualItem manualItem) {
                log.info("[process] body: {}, {}", manualItem.getName(), manualItem.getAge());
                return manualItem;
            }

            if (item instanceof ManualFooter footer) {
                log.info("[process] footer count: {}", footer.getCount());
                return null;
            }

            throw new RuntimeException("[process] wrong exception");
        };
    }


    private ItemWriter<ManualItem> manualFileReadWriter() {
        return items -> {
            for (ManualItem item : items) {
                log.info("[write] writer: {}, {}", item.getName(), item.getAge());
            }
        };
    }

}
