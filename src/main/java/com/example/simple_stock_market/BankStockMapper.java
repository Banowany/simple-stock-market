package com.example.simple_stock_market;

public class BankStockMapper {
    public StockItemDTO toDTO(BankStock bankStock) {
        var res = new StockItemDTO();
        res.setName(bankStock.getName());
        res.setQuantity(bankStock.getQuantity());
        return res;
    }

    public BankStock fromDTO(StockItemDTO stockItemDTO) {
        var res = new BankStock();
        res.setName(stockItemDTO.getName());
        res.setQuantity(stockItemDTO.getQuantity());
        return res;
    }
}
