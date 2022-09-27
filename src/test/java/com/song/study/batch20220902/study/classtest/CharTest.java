package com.song.study.batch20220902.study.classtest;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class CharTest {

    @Test
    void testLength() {
        System.out.println("❎".length());
        System.out.println("❎".getBytes(StandardCharsets.UTF_8).length);
        System.out.println("➿".length());
        System.out.println("➿".getBytes(StandardCharsets.UTF_8).length);
        System.out.println("📪".length());
        System.out.println("📪".getBytes(StandardCharsets.UTF_8).length);
    }
}
