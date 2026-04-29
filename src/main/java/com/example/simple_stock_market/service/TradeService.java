package com.example.simple_stock_market.service;

import com.example.simple_stock_market.entity.BankStock;
import com.example.simple_stock_market.entity.Wallet;
import com.example.simple_stock_market.repository.BankStockRepository;
import com.example.simple_stock_market.repository.WalletRepository;
import com.example.simple_stock_market.repository.WalletStockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;

@Service
public class TradeService {
    private final BankStockRepository bankStockRepository;
    private final WalletRepository walletRepository;
    private final WalletStockRepository walletStockRepository;

    public TradeService(BankStockRepository bankStockRepository, WalletRepository walletRepository, WalletStockRepository walletStockRepository) {
        this.bankStockRepository = bankStockRepository;
        this.walletRepository = walletRepository;
        this.walletStockRepository = walletStockRepository;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void executeTrade(String walletId, String stockName, String type) {
        walletRepository.createIfNotExists(walletId);

        if (!bankStockRepository.existsById(stockName)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Stock " + stockName + " does not exist"
            );
        }

        if ("buy".equalsIgnoreCase(type)) {
            int updated = bankStockRepository.decrement(stockName);

            if (updated == 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No stock " + stockName + " in bank"
                );
            }

            walletStockRepository.upsertIncrement(walletId, stockName);
        } else if ("sell".equalsIgnoreCase(type)) {

            int updated = walletStockRepository.decrement(walletId, stockName);

            if (updated == 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No stock " + stockName + " in wallet"
                );
            }

            walletStockRepository.deleteIfZero(walletId, stockName);

            bankStockRepository.increment(stockName);

        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Operation " + type + " does not exist"
            );
        }
    }
}
