package com.example.simple_stock_market.controller;

import com.example.simple_stock_market.service.BankService;
import com.example.simple_stock_market.dto.BankStocksDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    // Punkt 4: GET /stocks
    @GetMapping
    public ResponseEntity<BankStocksDTO> getBankStocks() {
        var stocks = bankService.getAllBankStocks();
        var response = new BankStocksDTO(stocks);

        return ResponseEntity.ok(response);
    }

    // Punkt 5: POST /stocks
    @PostMapping
    public ResponseEntity<Void> setBankStocks(@RequestBody BankStocksDTO request) {
        bankService.setBankState(request.getStocks());
        return ResponseEntity.ok().build();
    }
}
