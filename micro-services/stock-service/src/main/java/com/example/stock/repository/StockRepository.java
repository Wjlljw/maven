package com.example.stock.repository;

import com.example.common.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 库存仓库
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    
    /**
     * 根据商品名称查询库存
     * @param productName 商品名称
     * @return 库存信息
     */
    Optional<Stock> findByProductName(String productName);
}