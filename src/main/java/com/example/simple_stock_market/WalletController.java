package com.example.simple_stock_market;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{wallet_id}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable("wallet_id") String walletId) {
//        walletService.createWallet(walletId);
        WalletResponse response = walletService.getWallet(walletId);
        return ResponseEntity.ok(response);
    }
}
