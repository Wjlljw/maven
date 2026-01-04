package com.example.stock.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 库存配置类
 */
@Component
@ConfigurationProperties(prefix = "app.stock")
public class StockConfig implements InitializingBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StockConfig.class);
    
    /**
     * 库存预警配置
     */
    private Alert alert = new Alert();
    
    /**
     * 库存更新配置
     */
    private Update update = new Update();
    
    /**
     * 库存查询配置
     */
    private Query query = new Query();
    
    /**
     * 初始化方法，配置加载完成后自动调用
     */
    public void afterPropertiesSet() {
        LOGGER.info("[Stock Config Loaded] 库存配置已加载：");
        LOGGER.info("  - 预警配置：最低阈值={}, 最高阈值={}, 通知邮箱={}", 
                   alert.getMinThreshold(), alert.getMaxThreshold(), alert.getEmailNotify());
        LOGGER.info("  - 更新配置：乐观锁={}, 等待时间={}ms, 最大重试={}", 
                   update.isEnableOptimisticLock(), update.getWaitTime(), update.getMaxRetry());
        LOGGER.info("  - 查询配置：缓存={}, 缓存过期={}s, 最大查询数={}", 
                   query.isEnableCache(), query.getCacheExpire(), query.getMaxQuerySize());
    }
    
    // getter and setter
    public Alert getAlert() {
        return alert;
    }
    
    public void setAlert(Alert alert) {
        this.alert = alert;
    }
    
    public Update getUpdate() {
        return update;
    }
    
    public void setUpdate(Update update) {
        this.update = update;
    }
    
    public Query getQuery() {
        return query;
    }
    
    public void setQuery(Query query) {
        this.query = query;
    }
    
    /**
     * 库存预警配置内部类
     */
    public static class Alert {
        /**
         * 最低库存阈值
         */
        private int minThreshold = 10;
        
        /**
         * 最高库存阈值
         */
        private int maxThreshold = 1000;
        
        /**
         * 预警邮件通知列表
         */
        private String emailNotify = "admin@example.com";
        
        // getter and setter
        public int getMinThreshold() {
            return minThreshold;
        }
        
        public void setMinThreshold(int minThreshold) {
            this.minThreshold = minThreshold;
        }
        
        public int getMaxThreshold() {
            return maxThreshold;
        }
        
        public void setMaxThreshold(int maxThreshold) {
            this.maxThreshold = maxThreshold;
        }
        
        public String getEmailNotify() {
            return emailNotify;
        }
        
        public void setEmailNotify(String emailNotify) {
            this.emailNotify = emailNotify;
        }
    }
    
    /**
     * 库存更新配置内部类
     */
    public static class Update {
        /**
         * 是否启用乐观锁
         */
        private boolean enableOptimisticLock = true;
        
        /**
         * 并发更新等待时间(毫秒)
         */
        private long waitTime = 5000;
        
        /**
         * 最大重试次数
         */
        private int maxRetry = 3;
        
        // getter and setter
        public boolean isEnableOptimisticLock() {
            return enableOptimisticLock;
        }
        
        public void setEnableOptimisticLock(boolean enableOptimisticLock) {
            this.enableOptimisticLock = enableOptimisticLock;
        }
        
        public long getWaitTime() {
            return waitTime;
        }
        
        public void setWaitTime(long waitTime) {
            this.waitTime = waitTime;
        }
        
        public int getMaxRetry() {
            return maxRetry;
        }
        
        public void setMaxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
        }
    }
    
    /**
     * 库存查询配置内部类
     */
    public static class Query {
        /**
         * 是否启用缓存
         */
        private boolean enableCache = true;
        
        /**
         * 缓存过期时间(秒)
         */
        private int cacheExpire = 300;
        
        /**
         * 单次查询最大数量
         */
        private int maxQuerySize = 100;
        
        // getter and setter
        public boolean isEnableCache() {
            return enableCache;
        }
        
        public void setEnableCache(boolean enableCache) {
            this.enableCache = enableCache;
        }
        
        public int getCacheExpire() {
            return cacheExpire;
        }
        
        public void setCacheExpire(int cacheExpire) {
            this.cacheExpire = cacheExpire;
        }
        
        public int getMaxQuerySize() {
            return maxQuerySize;
        }
        
        public void setMaxQuerySize(int maxQuerySize) {
            this.maxQuerySize = maxQuerySize;
        }
    }
}