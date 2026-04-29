package com.example.simple_stock_market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class WalletResponseDTO {
    private String id;
    private List<StockItem> stocks;

    @AllArgsConstructor
    @Getter
    public static class StockItem {
        private String name;
        private Integer quantity;
    }
}
