package com.song.study.batch20220902.job.manual.item;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManualItem implements CsvLine {

    private String name;
    private Long age;
}
