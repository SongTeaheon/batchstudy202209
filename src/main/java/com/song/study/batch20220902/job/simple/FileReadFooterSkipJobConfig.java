package com.song.study.batch20220902.job.simple;

import java.io.IOException;
import java.nio.file.Files;

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
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.song.study.batch20220902.config.FooterSkipLineMapper;
import com.song.study.batch20220902.job.simple.item.FileCreateItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileReadFooterSkipJobConfig {

    private static final String JOB_NAME = "FileReadFooterSkipJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileReadFooterSkipJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(fileReadStep())
                                .build();
    }

    @Bean
    public Step fileReadStep() {
        return stepBuilderFactory.get("fileReadStep")
            .<FileCreateItem, FileCreateItem>chunk(2)
            .reader(fileReadReader())
            .processor(fileReadProcessor())
            .writer(fileReadWriter())
            .build();
    }

    @Bean
    public ItemReader<FileCreateItem> fileReadReader() {

        ClassPathResource resource = new ClassPathResource("test.csv");
        long totalItemsToRead = getTotalItemsToRead(resource);
        DefaultLineMapper<FileCreateItem> lineMapper = makeFileCreateItemDefaultLineMapper();

        return new FlatFileItemReaderBuilder<FileCreateItem>()
            .name("fileReadReader")
            .resource(resource)
            .lineMapper(new FooterSkipLineMapper<>(lineMapper, totalItemsToRead))
            .strict(false) // 라인을 읽을 때 parsing 에러가 나면 해당 필드 스킵
            .encoding("UTF-8")
            .linesToSkip(1)
            .saveState(false) // 상태 정보를 저장할 것인지 설정 (라인 수 등. 보통 false로 설정하는 듯?)
            .build();
    }

    private DefaultLineMapper<FileCreateItem> makeFileCreateItemDefaultLineMapper() {

        BeanWrapperFieldSetMapper<FileCreateItem> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(FileCreateItem.class);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("name", "age");

        DefaultLineMapper<FileCreateItem> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(tokenizer);
        return lineMapper;
    }

    private long getTotalItemsToRead(ClassPathResource resource) {
        try {
            return Files.lines(resource.getFile().toPath()).count();
        } catch (IOException e) {
            throw new RuntimeException("file io exception");
        }
    }

    @Bean
    public ItemProcessor<FileCreateItem, FileCreateItem> fileReadProcessor() {
        return item -> {
            log.info("[process] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            return item;
        };
    }

    @Bean
    public ItemWriter<FileCreateItem> fileReadWriter() {
        return items -> {
            for (FileCreateItem item : items) {
                log.info("[write] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            }
        };
    }
}
