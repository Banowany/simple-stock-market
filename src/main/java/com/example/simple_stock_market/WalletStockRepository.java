package com.example.simple_stock_market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletStockRepository extends JpaRepository<WalletStock, WalletStockId> {
    // Pobranie wszystkich akcji danego portfela
    List<WalletStock> findByIdWalletId(String walletId);
}
