package com.example.simple_stock_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "wallet_stock")
public class WalletStock {
    @EmbeddedId
    private WalletStockId id;


    @MapsId("stockName")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_name")
    private BankStock stock;

    @Column(nullable = false)
    private Integer quantity;
}
