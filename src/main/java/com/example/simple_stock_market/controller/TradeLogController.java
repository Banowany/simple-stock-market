package com.example.simple_stock_market.controller;

import com.example.simple_stock_market.dto.LogResponseDTO;
import com.example.simple_stock_market.service.TradeLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class TradeLogController {
    private final TradeLogService tradeLogService;

    public TradeLogController(TradeLogService tradeLogService) {
        this.tradeLogService = tradeLogService;
    }

    @GetMapping
    public ResponseEntity<LogResponseDTO> getLog() {
        return ResponseEntity.ok(tradeLogService.getLog());
    }
}
