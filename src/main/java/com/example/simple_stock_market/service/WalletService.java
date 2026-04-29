package com.example.simple_stock_market.service;

import com.example.simple_stock_market.mapper.WalletMapper;
import com.example.simple_stock_market.dto.WalletResponseDTO;
import com.example.simple_stock_market.repository.WalletStockRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private final WalletStockRepository walletStockRepository;
    private final WalletMapper walletMapper;

    public WalletService(WalletStockRepository walletStockRepository, WalletMapper walletMapper) {
        this.walletStockRepository = walletStockRepository;
        this.walletMapper = walletMapper;
    }

    public WalletResponseDTO getWallet(String walletId) {
        var walletStocks = walletStockRepository.findByIdWalletId(walletId);
        return walletMapper.toResponseDTO(walletId, walletStocks);
    }

    public int getQuantity(String walletId, String stockName) {
        var maybeQuantity = walletStockRepository.findQuantity(walletId, stockName);

        return maybeQuantity.orElse(0);
    }
}
