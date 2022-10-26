package com.song.study.batch20220902.dbio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.song.study.batch20220902.dbio.domain.Product;
import com.song.study.batch20220902.service.JdbcProductRepository;

@SpringBootTest
public class JdbcBatchUpdateTest {

    private static final int COUNT = 10000;

    @Autowired
    private JdbcProductRepository jdbcProductRepository;

    @Transactional
    @Test
    void updateTest() {
        List<Product> products = createProducts(COUNT);

        findTime("saveAllSimple", () -> jdbcProductRepository.saveAllSimple(products));
        findTime("saveAllBatchUpdateAtAll", () -> jdbcProductRepository.saveAllBatchUpdateAtAll(products));
    }


    private void findTime(String log, Runnable runnable) {
        long before = System.currentTimeMillis();
        runnable.run();
        long after = System.currentTimeMillis();

        System.out.println(log + " - time: " + (after - before) + "ms");
    }

    private List<Product> createProducts(int count) {

        List<Product> products = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            Product product = Product.builder().name(UUID.randomUUID().toString().substring(0, 33)).build();
            products.add(product);
        }
        return products;
    }
}
