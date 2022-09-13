package com.song.study.batch20220902.job.manual;

import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.song.study.batch20220902.job.manual.item.CsvLine;
import com.song.study.batch20220902.job.manual.item.ManualFooter;
import com.song.study.batch20220902.job.manual.item.ManualHeader;
import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
public class ManualFileWriteWriter implements ItemWriter<ManualItem>, ItemStream {

    private FlatFileItemWriter<CsvLine> writer;
    private StepExecution stepExecution;

    public ManualFileWriteWriter(@Value("#{stepExecution}") StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        writer = new FlatFileItemWriterBuilder<CsvLine>()
            .name("fileWriteWriter")
            .resource(new FileSystemResource("src/main/resources/test_manual.csv"))
            .append(false)
            .shouldDeleteIfExists(true) // 이전 파일이 있으면 지운다.
            .shouldDeleteIfEmpty(false) // write할 데이터가 없는 경우, 파일 자체를 지운다.
            .lineAggregator(new ManualFileAggregator())
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

    @SneakyThrows
    @Override
    public void open(ExecutionContext executionContext) {
        log.info("ManualFileWriteWriter open");
        writer.open(executionContext);
        writer.write(List.of(ManualHeader.builder().description("test header").build()));
    }

    @Override
    public void update(ExecutionContext executionContext) {
        log.info("ManualFileWriteWriter update");
    }

    @SneakyThrows
    @Override
    public void close() {
        log.info("ManualFileWriteWriter close");
        writer.write(List.of(ManualFooter.builder().count((long) this.stepExecution.getWriteCount()).build()));
        writer.close();
    }

    private static class ManualFileAggregator implements LineAggregator<CsvLine> {
        @Override
        public String aggregate(CsvLine item) {

            if (item instanceof ManualHeader header) {
                return header.getDescription();
            }

            if (item instanceof ManualItem manualItem) {
                return manualItem.getName() + ',' + manualItem.getAge();
            }

            if (item instanceof ManualFooter footer) {
                return "count: " + footer.getCount();
            }

            throw new RuntimeException("wrong exception");
        }
    }
}
