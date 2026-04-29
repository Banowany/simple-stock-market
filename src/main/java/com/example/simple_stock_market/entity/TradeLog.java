package com.example.simple_stock_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "trade_logs")
@Getter
@NoArgsConstructor
public class TradeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String walletId;

    @Column(nullable = false)
    private String stockName;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public static TradeLog of(String type, String walletId, String stockName) {
        TradeLog log = new TradeLog();
        log.type = type;
        log.walletId = walletId;
        log.stockName = stockName;
        return log;
    }
}
