package com.example.stock.service;

import com.example.common.entity.Stock;
import com.example.common.exception.BusinessException;
import com.example.stock.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * 库存服务
 */
@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    /**
     * 初始化库存数据
     */
    @PostConstruct
    public void init() {
        initializeStockData();
    }

    /**
     * 初始化库存数据
     */
    private void initializeStockData() {
        if (stockRepository.count() == 0) {
            stockRepository.save(new Stock("MacBook", 10));
            stockRepository.save(new Stock("iPhone", 50));
        }
    }

    /**
     * 扣减库存
     * @param itemName 商品名称
     * @param quantity 扣减数量
     */
    @Transactional
    public void reduceStock(String itemName, int quantity) {
        Stock stock = stockRepository.findByProductName(itemName)
                .orElseThrow(() -> new BusinessException("商品不存在：" + itemName));

        if (stock.getQuantity() < quantity) {
            throw new BusinessException(itemName + " 库存不足！当前库存：" + stock.getQuantity());
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);
        System.out.println("库存扣减成功： " + itemName + " 减少 " + quantity + " 件");
    }

    /**
     * 查询库存
     * @param itemName 商品名称
     * @return 库存数量
     */
    public int getStock(String itemName) {
        return stockRepository.findByProductName(itemName)
                .map(Stock::getQuantity)
                .orElseThrow(() -> new BusinessException("商品不存在：" + itemName));
    }
}