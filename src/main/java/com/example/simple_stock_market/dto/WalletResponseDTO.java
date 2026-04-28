package com.example.simple_stock_market.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class WalletResponseDTO {
    private String id;
    private List<StockItem> stocks;

    public WalletResponseDTO(String id, List<StockItem> stocks) {
        this.id = id;
        this.stocks = stocks;
    }

    @Data
    public static class StockItem {
        private String name;
        private Integer quantity;

        public StockItem(String name, Integer quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }
}
