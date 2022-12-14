package com.song.study.batch20220902.dbio.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Product {
    private Long id;
    private String name;
}
