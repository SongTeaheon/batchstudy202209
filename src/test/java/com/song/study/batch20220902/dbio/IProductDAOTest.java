package com.song.study.batch20220902.dbio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.song.study.batch20220902.dbio.domain.Product;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class IProductDAOTest {

    @Autowired
    private IProductDAO iProductDAO;

    @Test
    void findAll() {
        for (Product product : iProductDAO.findAll()) {
            log.info("product: {}, {}", product.getId(), product.getName());
        }

    }
}