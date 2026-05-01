package com.example.simple_stock_market;

import com.example.simple_stock_market.entity.BankStock;
import com.example.simple_stock_market.repository.BankStockRepository;
import com.example.simple_stock_market.repository.TradeLogRepository;
import com.example.simple_stock_market.repository.WalletStockRepository;
import com.example.simple_stock_market.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TradeServiceLoggingTest {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private BankStockRepository bankStockRepository;

    @Autowired
    private WalletStockRepository walletStockRepository;

    @Autowired
    private TradeLogRepository tradeLogRepository;

    @BeforeEach
    void setup() {
        tradeLogRepository.deleteAll();
        walletStockRepository.deleteAll();
        bankStockRepository.deleteAll();
    }

    @Test
    void shouldCreateTradeLog_whenBuyingStock() {
        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 10));

        tradeService.executeTrade(walletId, stock, "buy");

        var logs = tradeLogRepository.findAll();

        assertEquals(1, logs.size(),
                "Expected exactly 1 trade log after BUY");

        var log = logs.getFirst();

        assertEquals("buy", log.getType(), "Trade log type should be 'buy'");
        assertEquals(walletId, log.getWalletId(), "Trade log walletId mismatch");
        assertEquals(stock, log.getStockName(), "Trade log stock mismatch");
        assertNotNull(log.getCreatedAt(), "Trade log should have creation timestamp");
    }

    @Test
    void shouldCreateTradeLog_whenSellingStock() {
        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 0));
        var bankStock = bankStockRepository.findById(stock).orElseThrow();

        walletStockRepository.save(
                com.example.simple_stock_market.entity.WalletStock.of(walletId, bankStock, 1)
        );

        tradeService.executeTrade(walletId, stock, "sell");

        var logs = tradeLogRepository.findAll();

        assertEquals(1, logs.size(),
                "Expected exactly 1 trade log after SELL");

        var log = logs.getFirst();

        assertEquals("sell", log.getType(), "Trade log type should be 'sell'");
        assertEquals(walletId, log.getWalletId(), "Trade log walletId mismatch");
        assertEquals(stock, log.getStockName(), "Trade log stock mismatch");
        assertNotNull(log.getCreatedAt(), "Trade log should have creation timestamp");
    }

    @Test
    void shouldCreateMultipleTradeLogs_forMultipleTrades() {
        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 10));

        tradeService.executeTrade(walletId, stock, "buy");
        tradeService.executeTrade(walletId, stock, "buy");
        tradeService.executeTrade(walletId, stock, "sell");

        var logs = tradeLogRepository.findAll();

        assertEquals(3, logs.size(),
                "Expected exactly 3 trade logs after 3 trades");

        long buyCount = logs.stream()
                .filter(l -> l.getType().equals("buy"))
                .count();

        long sellCount = logs.stream()
                .filter(l -> l.getType().equals("sell"))
                .count();

        assertEquals(2, buyCount, "Expected 2 BUY logs");
        assertEquals(1, sellCount, "Expected 1 SELL log");

        assertTrue(
                logs.stream().allMatch(l ->
                        l.getWalletId().equals(walletId) &&
                                l.getStockName().equals(stock) &&
                                l.getCreatedAt() != null
                ),
                "All logs should have correct walletId, stockName and non-null timestamp"
        );
    }

    @Test
    void shouldNotCreateTradeLog_whenTransactionFails() {
        String stock = "AAPL";

        bankStockRepository.save(BankStock.of(stock, 0));

        assertThrows(ResponseStatusException.class, () ->
                tradeService.executeTrade("wallet1", stock, "buy")
        );

        var logs = tradeLogRepository.findAll();

        assertEquals(0, logs.size(),
                "Trade log should NOT be created when transaction fails");
    }
}
