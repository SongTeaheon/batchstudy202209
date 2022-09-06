package com.song.study.batch20220902.job.simple;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.song.study.batch20220902.job.simple.item.FileCreateItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleFileReadJobConfig {

    private static final String JOB_NAME = "simpleFileReadJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleFileReadJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(simpleFileReadStep())
                                .build();
    }

    @Bean
    public Step simpleFileReadStep() {
        return stepBuilderFactory.get("simpleFileReadStep")
            .<FileCreateItem, FileCreateItem>chunk(2)
            .reader(simpleFileReadReader())
            .processor(simpleFileReadProcessor())
            .writer(simpleFileReadWriter())
            .build();
    }

    @Bean
    public ItemReader<FileCreateItem> simpleFileReadReader() {

        return new FlatFileItemReaderBuilder<FileCreateItem>()
            .name("simpleFileReadReader")
            .resource(new ClassPathResource("test_no_trailer.csv"))
            .delimited().delimiter(",")
            .names("name", "age")
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
            .targetType(FileCreateItem.class)
            .strict(false) // 라인을 읽을 때 parsing 에러가 나면 해당 필드 스킵
            .encoding("UTF-8")
            .linesToSkip(1)
            .saveState(false) // 상태 정보를 저장할 것인지 설정 (라인 수 등. 보통 false로 설정하는 듯?)
            .build();
    }

    @Bean
    public ItemProcessor<FileCreateItem, FileCreateItem> simpleFileReadProcessor() {
        return item -> {
            log.info("[process] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            return item;
        };
    }

    @Bean
    public ItemWriter<FileCreateItem> simpleFileReadWriter() {
        return items -> {
            for (FileCreateItem item : items) {
                log.info("[write] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            }
        };
    }
}
