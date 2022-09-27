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
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.song.study.batch20220902.job.simple.item.FileCreateItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FixedFileReadJobConfig {

    private static final String JOB_NAME = "fixedFileReadJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fixedFileReadJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(fixedFileReadStep())
                                .build();
    }

    @Bean
    public Step fixedFileReadStep() {
        return stepBuilderFactory.get("fixedFileReadStep")
            .<FileCreateItem, FileCreateItem>chunk(2)
            .reader(fixedFileReadReader())
            .processor(fixedFileReadProcessor())
            .writer(fixedFileReadWriter())
            .build();
    }

    @Bean
    public ItemReader<FileCreateItem> fixedFileReadReader() {

        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames("name", "age");
        tokenizer.setColumns(new Range(1, 5), new Range(6, 8));

        return new FlatFileItemReaderBuilder<FileCreateItem>()
            .name("fixedFileReadReader")
            .resource(new ClassPathResource("test_fixed.csv"))
            .lineTokenizer(tokenizer)
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
            .targetType(FileCreateItem.class)
            .strict(false) // 라인을 읽을 때 parsing 에러가 나면 해당 필드 스킵
            .encoding("UTF-8")
            .saveState(false) // 상태 정보를 저장할 것인지 설정 (라인 수 등. 보통 false로 설정하는 듯?)
            .build();
    }

    @Bean
    public ItemProcessor<FileCreateItem, FileCreateItem> fixedFileReadProcessor() {
        return item -> {
            log.info("[process] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            return item;
        };
    }

    @Bean
    public ItemWriter<FileCreateItem> fixedFileReadWriter() {
        return items -> {
            for (FileCreateItem item : items) {
                log.info("[write] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            }
        };
    }
}
