package com.song.study.batch20220902.job.manual;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
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
public class ManualFileJobConfig {

    private static final String JOB_NAME = "manualFileJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ManualFileReadReader manualFileReadReader;
    private final ManualFileWriteWriter manualFileWriteWriter;

    @Bean
    public Job manualFileJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(manualFileStep())
                                .build();
    }

    @Bean
    public Step manualFileStep() {
        return stepBuilderFactory.get("manualFileStep")
                                 .<CsvLine, ManualItem>chunk(2)
                                 .reader(manualFileReadReader)
                                 .processor(manualFileProcessor())
                                 .writer(manualFileWriteWriter)
                                 .build();
    }

    @Bean
    public ItemProcessor<CsvLine, ManualItem> manualFileProcessor() {
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
}
