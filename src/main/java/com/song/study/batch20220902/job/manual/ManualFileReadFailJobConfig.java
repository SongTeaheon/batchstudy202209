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
import org.springframework.retry.policy.AlwaysRetryPolicy;

import com.song.study.batch20220902.job.manual.item.CsvLine;
import com.song.study.batch20220902.job.manual.item.ManualFooter;
import com.song.study.batch20220902.job.manual.item.ManualHeader;
import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ManualFileReadFailJobConfig {

    private static final String JOB_NAME = "manualFileReadFailJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ManualFileReadFailReader manualFileReadFailReader;

    @Bean
    public Job manualFileReadFailJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .start(manualFileReadFailStep())
//                                .incrementer(new RunIdIncrementer())
                                .build();
    }

    @Bean
    public Step manualFileReadFailStep() {
        return stepBuilderFactory.get("manualFileReadStep")
                                 .<CsvLine, ManualItem>chunk(1)
                                 .reader(manualFileReadFailReader)
                                 .processor(manualFileReadFailProcessor())
                                 .writer(manualFileReadFailWriter())
            .faultTolerant().retryPolicy(new AlwaysRetryPolicy())
            .retryLimit(5)
                                 .build();
    }

    @Bean
    public ItemProcessor<CsvLine, ManualItem> manualFileReadFailProcessor() {
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


    private ItemWriter<ManualItem> manualFileReadFailWriter() {
        return items -> {
            for (ManualItem item : items) {
                log.info("[write] writer: {}, {}", item.getName(), item.getAge());
            }
        };
    }

}
