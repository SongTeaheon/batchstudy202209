package com.song.study.batch20220902.study.classtest;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import com.song.study.batch20220902.job.simple.item.FileCreateItem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ClassTest {

    @Test
    void getFields() {
        Class<FileCreateItem> fileCreateItemClass = FileCreateItem.class;

        for (Field field : fileCreateItemClass.getDeclaredFields()) {
            log.info("test : " + field.getName());
        }

    }
}