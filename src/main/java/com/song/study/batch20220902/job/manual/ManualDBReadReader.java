package com.song.study.batch20220902.job.manual;

import java.nio.file.Files;
import java.util.Iterator;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.song.study.batch20220902.dbio.IProductDAO;
import com.song.study.batch20220902.dbio.domain.Product;
import com.song.study.batch20220902.job.manual.item.CsvLine;
import com.song.study.batch20220902.job.manual.item.ManualFooter;
import com.song.study.batch20220902.job.manual.item.ManualHeader;
import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
public class ManualDBReadReader implements ItemReader<Product>, ItemStream {

    private final StepExecution stepExecution;
    private final IProductDAO iProductDAO;
    private Iterator<Product> iterator;

    public ManualDBReadReader(@Value("#{StepExecution}") StepExecution stepExecution, IProductDAO iProductDAO) {
        this.iProductDAO = iProductDAO;
        this.stepExecution = stepExecution;
    }

    @SneakyThrows
    @Override
    public void open(ExecutionContext executionContext) {
        iterator = iProductDAO.findAll().iterator();
    }

    @Override
    public Product read() throws Exception {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    @Override
    public void update(ExecutionContext executionContext) {
        log.info("[reader] update");
    }

    @Override
    public void close() {
        log.info("[reader] close");
    }

}
