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
import org.springframework.context.annotation.ImportResource;

import com.song.study.batch20220902.job.simple.item.FileCreateItem;
import com.song.study.batch20220902.service.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileCreateJobConfig {

    private static final String JOB_NAME = "fileCreateJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ArticleService articleService;

    @Bean
    public Job fileCreateJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(fileCreateStep())
                                .build();
    }

    @Bean
    public Step fileCreateStep() {
        return stepBuilderFactory.get("fileCreateStep")
            .<FileCreateItem, FileCreateItem>chunk(2)
            .reader(fileCreateReader())
            .processor(fileCreateProcessor())
            .writer(fileCreateWriter())
            .build();
    }

    @Bean
    public ItemReader<FileCreateItem> fileCreateReader() {
        List<FileCreateItem> allArticles = articleService.getAllArticles();
        for (FileCreateItem allArticle : allArticles) {
            log.info("allArticles : " + allArticle.getName());
        }

        return new ListItemReader<>(allArticles);
    }

    @Bean
    public ItemProcessor<FileCreateItem, FileCreateItem> fileCreateProcessor() {
        return item -> {
            log.info("[process] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            return item;
        };
    }

    @Bean
    public ItemWriter<FileCreateItem> fileCreateWriter() {
        return items -> {
            for (FileCreateItem item : items) {
                log.info("[write] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            }
        };
    }
}
