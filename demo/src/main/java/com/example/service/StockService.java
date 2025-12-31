package com.example.service;

import com.example.entity.Stock;
import com.example.exception.BusinessException;
import com.example.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @PostConstruct
    public void init() {
        initializeStockData();
    }

    private void initializeStockData() {
        if (stockRepository.count() == 0) {
            stockRepository.save(new Stock("MacBook", 10));
            stockRepository.save(new Stock("iPhone", 50));
        }
    }

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

    public int getStock(String itemName) {
        return stockRepository.findByProductName(itemName)
                .map(Stock::getQuantity)
                .orElseThrow(() -> new BusinessException("商品不存在：" + itemName));
    }
}
