package com.song.study.batch20220902.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.song.study.batch20220902.job.simple.item.FileCreateItem;

@Service
public class ArticleService {
    public List<FileCreateItem> getAllArticles(){
        return List.of(
            FileCreateItem.builder().name("song").age(1L).build(),
            FileCreateItem.builder().name("kim").age(2L).build(),
            FileCreateItem.builder().name("jung").age(3L).build()
        );
    }
}
