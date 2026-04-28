package com.example.simple_stock_market;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletStockRepository walletStockRepository;

    public WalletService(WalletRepository walletRepository, WalletStockRepository walletStockRepository) {
        this.walletRepository = walletRepository;
        this.walletStockRepository = walletStockRepository;
    }

    private Wallet getWalletOrThrow(String walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Wallet not found with id: " + walletId
                ));
    }

    public WalletResponse getWallet(String walletId) {
        // Rzucamy wyjątkiem jeśli nie istnieje
        Wallet wallet = getWalletOrThrow(walletId);

        List<WalletResponse.StockItem> stocks = walletStockRepository
                .findByWalletId(walletId)
                .stream()
                .map(ws -> WalletResponse.StockItem.builder()
                        .name(ws.getStock().getName())
                        .quantity(ws.getQuantity())
                        .build())
                .toList();

        return WalletResponse.builder()
                .id(walletId)
                .stocks(stocks)
                .build();
    }

    public void createWallet(String walletId) {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        walletRepository.save(wallet);
    }
}
