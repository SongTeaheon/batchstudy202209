package com.song.study.batch20220902.job.manual.item;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManualHeader implements CsvLine {

    private String description;
}
