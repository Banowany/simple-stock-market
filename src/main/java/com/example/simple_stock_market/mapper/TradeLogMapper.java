package com.example.simple_stock_market.mapper;

import com.example.simple_stock_market.dto.LogResponseDTO;
import com.example.simple_stock_market.entity.TradeLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TradeLogMapper {
    public LogResponseDTO.LogItem toLogItem(TradeLog tradeLog) {
        return new LogResponseDTO.LogItem(
                tradeLog.getType(),
                tradeLog.getWalletId(),
                tradeLog.getStockName()
        );
    }
    public LogResponseDTO toResponseDTO(List<TradeLog> tradeLogs) {
        return new LogResponseDTO(
                tradeLogs.stream().map(this::toLogItem).toList()
        );
    }
}
