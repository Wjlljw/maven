package com.example.stock.service;

import com.example.common.entity.Stock;
import com.example.common.exception.BusinessException;
import com.example.stock.config.StockConfig;
import com.example.stock.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * 库存服务
 */
@Service
public class StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private StockConfig stockConfig;

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
        int retryCount = 0;
        boolean success = false;
        
        // 重试机制
        while (!success && retryCount < stockConfig.getUpdate().getMaxRetry()) {
            try {
                Stock stock = stockRepository.findByProductName(itemName)
                        .orElseThrow(() -> new BusinessException("商品不存在：" + itemName));

                if (stock.getQuantity() < quantity) {
                    throw new BusinessException(itemName + " 库存不足！当前库存：" + stock.getQuantity());
                }

                stock.setQuantity(stock.getQuantity() - quantity);
                stockRepository.save(stock);
                
                LOGGER.info("库存扣减成功： {} 减少 {} 件，剩余库存：{}", 
                           itemName, quantity, stock.getQuantity());
                
                // 检查库存预警
                checkStockAlert(stock);
                
                success = true;
            } catch (Exception e) {
                retryCount++;
                LOGGER.warn("库存扣减失败，第 {} 次重试：{}", retryCount, e.getMessage());
                
                if (retryCount >= stockConfig.getUpdate().getMaxRetry()) {
                    throw new BusinessException("库存扣减失败，已达到最大重试次数：" + retryCount);
                }
                
                // 等待一段时间后重试
                try {
                    Thread.sleep(stockConfig.getUpdate().getWaitTime());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException("库存扣减重试被中断: " + ie.getMessage());
                }
            }
        }
    }

    /**
     * 查询库存
     * @param itemName 商品名称
     * @return 库存数量
     */
    public int getStock(String itemName) {
        LOGGER.info("查询库存：{}，是否启用缓存：{}", 
                   itemName, stockConfig.getQuery().isEnableCache());
        
        Stock stock = stockRepository.findByProductName(itemName)
                .orElseThrow(() -> new BusinessException("商品不存在：" + itemName));
        
        int quantity = stock.getQuantity();
        
        // 检查库存预警
        checkStockAlert(stock);
        
        return quantity;
    }
    
    /**
     * 检查库存预警
     * @param stock 库存对象
     */
    private void checkStockAlert(Stock stock) {
        int currentStock = stock.getQuantity();
        String productName = stock.getProductName();
        
        // 检查最低库存阈值
        if (currentStock < stockConfig.getAlert().getMinThreshold()) {
            LOGGER.warn("[库存预警] {} 库存不足！当前库存：{}，最低阈值：{}", 
                       productName, currentStock, stockConfig.getAlert().getMinThreshold());
            LOGGER.warn("[库存预警] 通知邮箱：{}", stockConfig.getAlert().getEmailNotify());
        }
        
        // 检查最高库存阈值
        if (currentStock > stockConfig.getAlert().getMaxThreshold()) {
            LOGGER.warn("[库存预警] {} 库存过高！当前库存：{}，最高阈值：{}", 
                       productName, currentStock, stockConfig.getAlert().getMaxThreshold());
            LOGGER.warn("[库存预警] 通知邮箱：{}", stockConfig.getAlert().getEmailNotify());
        }
    }
}