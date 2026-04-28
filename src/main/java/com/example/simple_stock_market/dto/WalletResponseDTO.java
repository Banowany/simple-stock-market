package com.example.simple_stock_market.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WalletResponseDTO {
    private String id;
    private List<StockItem> stocks;

    @Data
    @Builder
    public static class StockItem {
        private String name;
        private Integer quantity;
    }
}
