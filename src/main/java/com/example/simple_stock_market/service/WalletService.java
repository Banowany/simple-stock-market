package com.example.simple_stock_market.service;

import com.example.simple_stock_market.entity.Wallet;
import com.example.simple_stock_market.mapper.WalletMapper;
import com.example.simple_stock_market.repository.WalletRepository;
import com.example.simple_stock_market.dto.WalletResponseDTO;
import com.example.simple_stock_market.repository.WalletStockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletStockRepository walletStockRepository;
    private final WalletMapper walletMapper;

    public WalletService(WalletRepository walletRepository, WalletStockRepository walletStockRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletStockRepository = walletStockRepository;
        this.walletMapper = walletMapper;
    }

    private Wallet getWalletOrThrow(String walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Wallet not found with id: " + walletId
                ));
    }

    public WalletResponseDTO getWallet(String walletId) {
        // Rzucamy wyjątkiem jeśli nie istnieje
        Wallet wallet = getWalletOrThrow(walletId);

        return walletMapper.toResponseDTO(wallet);
    }

    public void createWallet(String walletId) {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        walletRepository.save(wallet);
    }
}
