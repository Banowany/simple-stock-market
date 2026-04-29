package com.example.simple_stock_market.mapper;

import com.example.simple_stock_market.dto.StockBodyDTO;
import com.example.simple_stock_market.entity.BankStock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankStockMapper {
    public StockBodyDTO.StockItem toStockItem(BankStock bankStock) {
        return new StockBodyDTO.StockItem(
                bankStock.getName(),
                bankStock.getQuantity()
        );
    }

    public StockBodyDTO toStockBody(List<BankStock> bankStocks) {
        return new StockBodyDTO(
                bankStocks.stream().map(this::toStockItem).toList()
        );
    }

    public BankStock fromStockItem(StockBodyDTO.StockItem stockItemDTO) {
        return BankStock.of(
                stockItemDTO.getName(),
                stockItemDTO.getQuantity()
        );
    }
    public List<BankStock> fromStockBody(StockBodyDTO stockBodyDTO) {
        return stockBodyDTO.getStocks().stream().map(this::fromStockItem).toList();
    }
}
