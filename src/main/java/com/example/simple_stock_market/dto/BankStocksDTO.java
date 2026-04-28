package com.example.simple_stock_market.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankStocksDTO {
    private List<BankStocksItemDTO> stocks;
}
