package com.song.study.batch20220902.config;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FooterSkipLineMapper<T> implements LineMapper<T> {

    private final DefaultLineMapper<T> delegateLineMapper;
    private final long totalLineCount;

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        if (lineNumber >= totalLineCount){
            return null;
        }
        return delegateLineMapper.mapLine(line, lineNumber);
    }
}
