package com.example.simple_stock_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "wallet_stocks")
public class WalletStock {
    @EmbeddedId
    private WalletStockId id;


    @MapsId("stockName")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_name")
    private BankStock stock;

    @Column(nullable = false)
    private Integer quantity;

    public static WalletStock of(String walletId, BankStock stock, Integer quantity) {
        var walletStockId = new WalletStockId(walletId, stock.getName());
        var walletStock = new WalletStock();
        walletStock.id = walletStockId;
        walletStock.stock = stock;
        walletStock.quantity = quantity;
        return walletStock;
    }
}
