package com.song.study.batch20220902.job.simple;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.song.study.batch20220902.config.ColumnHeaderCallback;
import com.song.study.batch20220902.config.ItemCountFooterCallback;
import com.song.study.batch20220902.job.simple.item.FileCreateItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FixedFileWriteJobConfig {

    private static final String JOB_NAME = "fixedFileWriteJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fixedFileWriteJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(fixedFileWriteStep())
                                .build();
    }

    @Bean
    public Step fixedFileWriteStep() {
        return stepBuilderFactory.get("fileWriteStep")
            .<FileCreateItem, FileCreateItem>chunk(2)
            .reader(fixedFileWriteReader())
            .processor(fixedFileWriteProcessor())
            .writer(fixedFileWriteWriter())
            .build();
    }

    @Bean
    public ItemReader<FileCreateItem> fixedFileWriteReader() {
        return new ListItemReader<>(List.of(
            FileCreateItem.builder().name("song").age(1L).build(),
            FileCreateItem.builder().name("kim").age(2L).build(),
            FileCreateItem.builder().name("jung").age(3L).build()
        ));
    }

    @Bean
    public ItemProcessor<FileCreateItem, FileCreateItem> fixedFileWriteProcessor() {
        return item -> {
            log.info("[process] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            return item;
        };
    }

    @Bean
    public FlatFileItemWriter<FileCreateItem> fixedFileWriteWriter() {
        return new FlatFileItemWriterBuilder<FileCreateItem>()
            .name("fixedFileWriteWriter")
            .resource(new FileSystemResource("src/main/resources/test_fixed.csv"))
            .append(false)
            .shouldDeleteIfExists(true) // 이전 파일이 있으면 지운다.
            .shouldDeleteIfEmpty(false) // write할 데이터가 없는 경우, 파일 자체를 지운다.
            .formatted().format("%-5s%-3d").fieldExtractor(item -> new Object[] { item.getName(), item.getAge()})
            .build();
    }
}
