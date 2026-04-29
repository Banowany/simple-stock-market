package com.example.simple_stock_market.service;

import com.example.simple_stock_market.dto.LogResponseDTO;
import com.example.simple_stock_market.mapper.TradeLogMapper;
import com.example.simple_stock_market.repository.TradeLogRepository;
import org.springframework.stereotype.Service;

@Service
public class TradeLogService {
    private final TradeLogRepository logRepo;
    private final TradeLogMapper tradeLogMapper;

    public TradeLogService(TradeLogRepository logRepo, TradeLogMapper tradeLogMapper) {
        this.logRepo = logRepo;
        this.tradeLogMapper = tradeLogMapper;
    }

    public LogResponseDTO getLog() {
        return tradeLogMapper.toResponseDTO(logRepo.findAllByOrderByCreatedAtAsc());
    }
}
