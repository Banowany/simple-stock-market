package com.example.simple_stock_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(WalletStockId.class)
public class WalletStock {
    @Id
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Id
    @ManyToOne
    @JoinColumn(name = "stock_name")
    private BankStock stock;

    @Column(nullable = false)
    private Integer quantity;
}
