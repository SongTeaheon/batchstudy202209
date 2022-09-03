package com.song.study.batch20220902.job.simple.item;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileCreateItem {

    private String name;
    private Long age;
}
