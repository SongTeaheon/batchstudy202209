package com.song.study.batch20220902.job.simple;

import java.io.IOException;
import java.io.Writer;
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
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
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
public class FileWriteJobConfig {

    private static final String JOB_NAME = "fileWriteJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileWriteJob() {
        return jobBuilderFactory.get(JOB_NAME)
                                .incrementer(new RunIdIncrementer())
                                .start(fileWriteStep())
                                .build();
    }

    @Bean
    public Step fileWriteStep() {
        return stepBuilderFactory.get("fileWriteStep")
            .<FileCreateItem, FileCreateItem>chunk(2)
            .reader(fileWriteReader())
            .processor(fileWriteProcessor())
            .writer(fileWriteWriter())
            .build();
    }

    @Bean
    public ItemReader<FileCreateItem> fileWriteReader() {
        return new ListItemReader<>(List.of(
            FileCreateItem.builder().name("song").age(1L).build(),
            FileCreateItem.builder().name("kim").age(2L).build(),
            FileCreateItem.builder().name("jung").age(3L).build()
        ));
    }

    @Bean
    public ItemProcessor<FileCreateItem, FileCreateItem> fileWriteProcessor() {
        return item -> {
            log.info("[process] fileCreateItem name: {}, age: {}", item.getName(), item.getAge());
            return item;
        };
    }

    @Bean
    public FlatFileItemWriter<FileCreateItem> fileWriteWriter() {
        return new FlatFileItemWriterBuilder<FileCreateItem>()
            .name("fileWriteWriter")
            .resource(new FileSystemResource("src/main/resources/test.csv"))
            .headerCallback(columnHeaderCallback()) // 헤더를 파일에 쓰기 위한 콜백인터페이스
            .footerCallback(itemCountFooterCallback()) // 푸터를 파일에 쓰기 위한 콜백인터페이스
            .append(false)
            .shouldDeleteIfExists(true) // 이전 파일이 있으면 지운다.
            .shouldDeleteIfEmpty(false) // write할 데이터가 없는 경우, 파일 자체를 지운다.

            // 아래 3개 중에 하나를 사용한다. (lineAggregator() or formatted() or delimited())
            .lineAggregator(item -> item.getName() + ',' + item.getAge())
//            .formatted().format("%-5s,%-3d").fieldExtractor(item -> new Object[] { item.getName(), item.getAge()})
//            .delimited().delimiter(",").names("name", "age") // 객체의 필드들을 설정

            .build();

    }
    @Bean
    @StepScope
    public FlatFileHeaderCallback columnHeaderCallback() {
        return new ColumnHeaderCallback<>(FileCreateItem.class);
    }

    @Bean
    @StepScope
    public FlatFileFooterCallback itemCountFooterCallback() {
        return new ItemCountFooterCallback();
    }

}
