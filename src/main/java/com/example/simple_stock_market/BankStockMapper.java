package com.example.simple_stock_market;

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
