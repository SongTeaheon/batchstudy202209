package com.song.study.batch20220902.job.manual;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.core.io.FileSystemResource;

import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManualFileWriteWriter implements ItemWriter<ManualItem>, ItemStream {

    FlatFileItemWriter<ManualItem> writer;

    public ManualFileWriteWriter() {
        writer = new FlatFileItemWriterBuilder<ManualItem>()
            .name("fileWriteWriter")
            .resource(new FileSystemResource("src/main/resources/test_manual.csv"))
            .append(false)
            .shouldDeleteIfExists(true) // 이전 파일이 있으면 지운다.
            .shouldDeleteIfEmpty(false) // write할 데이터가 없는 경우, 파일 자체를 지운다.
            .delimited().delimiter(",")
            .names("name", "age")
            .build();

        try {
            writer.afterPropertiesSet();
        } catch (Exception e) {
            log.error("FlatFileItemWriter afterPropertiesSet error", e);
        }
    }

    @Override
    public void write(List<? extends ManualItem> items) throws Exception {
        for (ManualItem item : items) {
            log.info("[write] ManualItem name: {}, age: {}", item.getName(), item.getAge());
        }
        writer.write(items);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("ManualFileWriteWriter open");
        writer.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("ManualFileWriteWriter update");
    }

    /**
     * @throws ItemStreamException
     */
    @Override
    public void close() {
        log.info("ManualFileWriteWriter close");
        writer.close();
    }
}
