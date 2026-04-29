package com.example.simple_stock_market.mapper;

import com.example.simple_stock_market.dto.TradeLogDTO;
import com.example.simple_stock_market.entity.TradeLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TradeLogMapper {
    public TradeLogDTO.LogItem toLogItem(TradeLog tradeLog) {
        return new TradeLogDTO.LogItem(
                tradeLog.getType(),
                tradeLog.getWalletId(),
                tradeLog.getStockName()
        );
    }
    public TradeLogDTO toResponseDTO(List<TradeLog> tradeLogs) {
        return new TradeLogDTO(
                tradeLogs.stream().map(this::toLogItem).toList()
        );
    }
}
