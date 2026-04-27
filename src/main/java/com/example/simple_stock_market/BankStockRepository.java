package com.example.simple_stock_market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankStockRepository extends JpaRepository<BankStock, Long> {
}
