package com.song.study.batch20220902.dbio;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.song.study.batch20220902.dbio.domain.Product;

@Repository
@Mapper
public interface IProductDAO {
    List<Product> findAll();
}
