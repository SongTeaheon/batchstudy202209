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
public class ManualLogJobConfig {

    private static final String JOB_NAME = "manualLogJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ManualLogBat manualLogBat;

    @Bean
    public Job manualLogJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(manualLogStep())
                                .build();
    }

    @Bean
    public Step manualLogStep() {
        return stepBuilderFactory.get("manualFileStep")
                                 .<ManualItem, ManualItem>chunk(2)
                                 .reader(manualLogBat)
                                 .processor(manualLogBat)
                                 .writer(manualLogBat)
                                 .build();
    }

}
