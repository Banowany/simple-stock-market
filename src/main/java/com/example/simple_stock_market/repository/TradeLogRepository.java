package com.example.simple_stock_market.repository;

import com.example.simple_stock_market.entity.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeLogRepository extends JpaRepository<TradeLog, Long> {
    List<TradeLog> findTop10000ByOrderByCreatedAtDescIdDesc();
}
