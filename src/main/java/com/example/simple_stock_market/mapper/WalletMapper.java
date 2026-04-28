package com.example.simple_stock_market.mapper;

import com.example.simple_stock_market.dto.WalletResponseDTO;
import com.example.simple_stock_market.entity.Wallet;
import com.example.simple_stock_market.entity.WalletStock;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WalletMapper {
    private WalletResponseDTO.StockItem toStockItem(WalletStock walletStock) {
        return new WalletResponseDTO.StockItem(
                walletStock.getStock().getName(),
                walletStock.getQuantity()
        );
    }

    public WalletResponseDTO toResponseDTO(Wallet wallet) {
        List<WalletResponseDTO.StockItem> stocksDTO = wallet.getStocks().stream().map(this::toStockItem).toList();
        return new WalletResponseDTO(wallet.getId(), stocksDTO);
    }
}
