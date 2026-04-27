package com.example.simple_stock_market;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {
    private final BankStockRepository bankStockRepository;

    public BankService(BankStockRepository bankStockRepository) {
        this.bankStockRepository = bankStockRepository;
    }

    @Transactional
    public void setBankState(List<BankStocksItemDTO> newStocksDTO) {
        var mapper = new BankStockMapper();
        var newStocks = newStocksDTO.stream().map(mapper::fromDTO).toList();
        bankStockRepository.deleteAll();
        bankStockRepository.saveAll(newStocks);
    }

    public List<BankStocksItemDTO> getAllBankStocks() {
        var stocks = bankStockRepository.findAll();
        var mappper = new BankStockMapper();
        return stocks.stream().map(mappper::toDTO).toList();
    }
}
