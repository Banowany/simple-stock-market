package com.example.simple_stock_market.service;

import com.example.simple_stock_market.dto.StockBodyDTO;
import com.example.simple_stock_market.mapper.BankStockMapper;
import com.example.simple_stock_market.repository.BankStockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BankService {
    private final BankStockRepository bankStockRepository;
    private final BankStockMapper bankStockMapper;

    public BankService(BankStockRepository bankStockRepository, BankStockMapper bankStockMapper) {
        this.bankStockRepository = bankStockRepository;
        this.bankStockMapper = bankStockMapper;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void setBankState(StockBodyDTO newStocksDTO) {
        var newStocks = bankStockMapper.fromStockBody(newStocksDTO);
        if (bankStockRepository.count() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bank already initialized");
        }
        bankStockRepository.saveAll(newStocks);
    }

    public StockBodyDTO getAllBankStocks() {
        var stocks = bankStockRepository.findAll();
        return bankStockMapper.toStockBody(stocks);
    }
}
