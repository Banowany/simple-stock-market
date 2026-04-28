package com.example.simple_stock_market.mapper;

import com.example.simple_stock_market.dto.BankStocksItemDTO;
import com.example.simple_stock_market.entity.BankStock;

public class BankStockMapper {
    public BankStocksItemDTO toDTO(BankStock bankStock) {
        var res = new BankStocksItemDTO();
        res.setName(bankStock.getName());
        res.setQuantity(bankStock.getQuantity());
        return res;
    }

    public BankStock fromDTO(BankStocksItemDTO bankStocksItemDTO) {
        var res = new BankStock();
        res.setName(bankStocksItemDTO.getName());
        res.setQuantity(bankStocksItemDTO.getQuantity());
        return res;
    }
}
