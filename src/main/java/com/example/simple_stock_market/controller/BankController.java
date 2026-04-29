package com.example.simple_stock_market.controller;

import com.example.simple_stock_market.service.BankService;
import com.example.simple_stock_market.dto.StockBodyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ResponseEntity<StockBodyDTO> getBankStocks() {
        var response = bankService.getAllBankStocks();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> setBankStocks(@RequestBody StockBodyDTO request) {
        bankService.setBankState(request);
        return ResponseEntity.ok().build();
    }
}
