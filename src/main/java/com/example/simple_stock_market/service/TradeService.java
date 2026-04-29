package com.example.simple_stock_market.service;

import com.example.simple_stock_market.entity.TradeLog;
import com.example.simple_stock_market.repository.BankStockRepository;
import com.example.simple_stock_market.repository.TradeLogRepository;
import com.example.simple_stock_market.repository.WalletStockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TradeService {
    private final BankStockRepository bankStockRepository;
    private final WalletStockRepository walletStockRepository;
    private final TradeLogRepository tradeLogRepository;

    public TradeService(BankStockRepository bankStockRepository, WalletStockRepository walletStockRepository, TradeLogRepository tradeLogRepository) {
        this.bankStockRepository = bankStockRepository;
        this.walletStockRepository = walletStockRepository;
        this.tradeLogRepository = tradeLogRepository;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void executeTrade(String walletId, String stockName, String type) {
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

            tradeLogRepository.save(TradeLog.of("buy", walletId, stockName));
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

            tradeLogRepository.save(TradeLog.of("sell", walletId, stockName));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Operation " + type + " does not exist"
            );
        }
    }
}
