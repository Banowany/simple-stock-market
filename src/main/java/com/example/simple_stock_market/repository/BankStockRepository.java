package com.example.simple_stock_market.repository;

import com.example.simple_stock_market.entity.BankStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankStockRepository extends JpaRepository<BankStock, String> {
}
