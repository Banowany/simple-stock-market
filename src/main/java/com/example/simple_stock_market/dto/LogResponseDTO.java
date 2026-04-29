package com.example.simple_stock_market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class LogResponseDTO {
    private List<LogItem> log;

    @AllArgsConstructor
    @Getter
    public static class LogItem {
        private String type;
        private String wallet_id;
        private String stock_name;
    }
}
