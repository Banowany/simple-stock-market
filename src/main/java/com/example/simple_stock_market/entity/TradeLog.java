package com.example.simple_stock_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "trade_logs")
@Getter
@Setter
@NoArgsConstructor
public class TradeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "wallet_id", nullable = false)
    private String walletId;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public static TradeLog of(String type, String walletId, String stockName) {
        TradeLog log = new TradeLog();
        log.setType(type);
        log.setWalletId(walletId);
        log.setStockName(stockName);
        return log;
    }
}
