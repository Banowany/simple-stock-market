package com.example.simple_stock_market.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankStocksItemDTO {
    private String name;
    private Integer quantity;
}
