package com.song.study.batch20220902.service;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.song.study.batch20220902.dbio.domain.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JdbcProductRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAllSimple(List<Product> products) {
        for (Product product : products) {
            jdbcTemplate.update("INSERT INTO product (name) " +
                                "VALUES (?)",
                                product.getName());
        }
    }

    @Transactional
    public void saveAllBatchUpdateLoop(List<Product> products) {
        for (Product product : products) {
            jdbcTemplate.batchUpdate("INSERT INTO product (name) " +
                                "VALUES (?)",
                                product.getName());
        }
    }

    @Transactional
    public void saveAllBatchUpdateAtAll(List<Product> products) {
        jdbcTemplate.batchUpdate("INSERT INTO product (name) " +
                                 "VALUES (?)",
                                 products,
                                 1000,
                                 (PreparedStatement ps, Product product) -> ps.setString(1, product.getName()));
    }
}
