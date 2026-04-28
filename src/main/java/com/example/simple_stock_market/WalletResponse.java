package com.example.simple_stock_market;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WalletResponse {
    private String id;
    private List<StockItem> stocks;

    @Data
    @Builder
    public static class StockItem {
        private String name;
        private Integer quantity;
    }
}
