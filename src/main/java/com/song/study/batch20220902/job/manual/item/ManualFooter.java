package com.song.study.batch20220902.job.manual.item;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManualFooter implements CsvLine {

    private Long count;
}
