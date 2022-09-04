package com.song.study.batch20220902.config;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.batch.item.file.FlatFileHeaderCallback;


public class ColumnHeaderCallback<T> implements FlatFileHeaderCallback {

    private Class<T> type;

    public ColumnHeaderCallback(Class<T> type) {
        this.type = type;
    }

    @Override
    public void writeHeader(Writer writer) throws IOException {
        writer.append(Strings.join(Arrays.stream(type.getDeclaredFields()).map(Field::getName)
                                         .collect(Collectors.toList()), ','));
    }
}
