package com.song.study.batch20220902.job.manual;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
public class ManualLogBat
    implements ItemReader<ManualItem>, ItemProcessor<ManualItem, ManualItem>, ItemWriter<ManualItem>, ItemStream {

    private Iterator<ManualItem> iterator;

    @SneakyThrows
    @Override
    public void open(ExecutionContext executionContext) {
        log.info("[ManualLogBat] open");

        List<ManualItem> readTarget = List.of(ManualItem.builder().name("song").age(1L).build(),
                                              ManualItem.builder().name("kim").age(2L).build(),
                                              ManualItem.builder().name("jung").age(3L).build());
        iterator = readTarget.iterator();
    }

    @Override
    public ManualItem read() throws Exception {
        if (iterator.hasNext()) {
            ManualItem next = iterator.next();
            log.info("[ManualLogBat] read: {} - {}", next.getName(), next.getAge());
            return next;
        }
        return null;
    }

    @Override
    public ManualItem process(ManualItem item) throws Exception {
        log.info("[ManualLogBat] process: {} - {}", item.getName(), item.getAge());
        return item;
    }

    @Override
    public void write(List<? extends ManualItem> items) throws Exception {
        for (ManualItem item : items) {
            log.info("[ManualLogBat] write: {} - {}", item.getName(), item.getAge());
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        log.info("[ManualLogBat] update: {}", executionContext);
    }

    @Override
    public void close() {
        log.info("[ManualLogBat] close");
    }
}
