package com.example.stock.controller;

import com.example.stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 库存控制器
 */
@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    /**
     * 查询库存
     * @param itemName 商品名称
     * @return 库存数量
     */
    @GetMapping("/{itemName}")
    public ResponseEntity<Integer> getStock(@PathVariable String itemName) {
        try {
            int stock = stockService.getStock(itemName);
            return new ResponseEntity<>(stock, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 扣减库存
     * @param itemName 商品名称
     * @param quantity 扣减数量
     * @return 操作结果
     */
    @PostMapping("/reduce")
    public ResponseEntity<String> reduceStock(
            @RequestParam String itemName,
            @RequestParam int quantity) {
        try {
            stockService.reduceStock(itemName, quantity);
            return new ResponseEntity<>("库存扣减成功", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("库存扣减失败：" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}