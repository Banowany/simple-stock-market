package com.example.simple_stock_market;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class WalletStockId implements Serializable {
    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "stock_name")
    private String stockName;
}
