package com.example.simple_stock_market.controller;

import com.example.simple_stock_market.dto.TradeRequestDTO;
import com.example.simple_stock_market.dto.WalletResponseDTO;
import com.example.simple_stock_market.service.TradeService;
import com.example.simple_stock_market.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private final WalletService walletService;
    private final TradeService tradeService;

    public WalletController(WalletService walletService, TradeService tradeService) {
        this.walletService = walletService;
        this.tradeService = tradeService;
    }

    @GetMapping("/{wallet_id}")
    public ResponseEntity<WalletResponseDTO> getWallet(@PathVariable("wallet_id") String walletId) {
//        walletService.createWallet(walletId);
        WalletResponseDTO response = walletService.getWallet(walletId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{wallet_id}/stocks/{stock_name}")
    public ResponseEntity<Void> trade(
            @PathVariable("wallet_id") String walletId,
            @PathVariable("stock_name") String stockName,
            @RequestBody TradeRequestDTO request
    ) {
        tradeService.executeTrade(walletId, stockName, request.getType());
        return ResponseEntity.ok().build();
    }
}
