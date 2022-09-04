package com.song.study.batch20220902.config;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.beans.factory.annotation.Value;

public class ItemCountFooterCallback implements FlatFileFooterCallback {

    @Value("#{StepExecution}")
    private StepExecution stepExecution;

    @Override
    public void writeFooter(Writer writer) throws IOException {
        writer.append("count: " + stepExecution.getWriteCount());
    }
}
