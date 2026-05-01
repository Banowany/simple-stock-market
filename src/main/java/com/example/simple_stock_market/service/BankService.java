package com.example.simple_stock_market.service;

import com.example.simple_stock_market.dto.StockBodyDTO;
import com.example.simple_stock_market.mapper.BankStockMapper;
import com.example.simple_stock_market.repository.BankStockRepository;
import com.example.simple_stock_market.repository.TradeLogRepository;
import com.example.simple_stock_market.repository.WalletStockRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BankService {
    private final BankStockRepository bankStockRepository;
    private final BankStockMapper bankStockMapper;
    private final WalletStockRepository walletStockRepository;
    private final TradeLogRepository tradeLogRepository;

    @Autowired
    private EntityManager em;

    public BankService(BankStockRepository bankStockRepository, BankStockMapper bankStockMapper, WalletStockRepository walletStockRepository, TradeLogRepository tradeLogRepository) {
        this.bankStockRepository = bankStockRepository;
        this.bankStockMapper = bankStockMapper;
        this.walletStockRepository = walletStockRepository;
        this.tradeLogRepository = tradeLogRepository;
    }

    @Transactional
    public void setBankState(StockBodyDTO newStocksDTO) {
        walletStockRepository.deleteAll();
        bankStockRepository.deleteAll();
        tradeLogRepository.deleteAll();

        var newStocks = bankStockMapper.fromStockBody(newStocksDTO);

        for (var stock : newStocks) {
            em.persist(stock);
        }
    }

    public StockBodyDTO getAllBankStocks() {
        var stocks = bankStockRepository.findAll();
        return bankStockMapper.toStockBody(stocks);
    }
}
