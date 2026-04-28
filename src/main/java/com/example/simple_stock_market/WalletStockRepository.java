package com.example.simple_stock_market;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletStockRepository extends JpaRepository<WalletStock, WalletStockId> {
    List<WalletStock> findByWalletId(String walletId);
}
