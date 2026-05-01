package com.example.simple_stock_market;

import com.example.simple_stock_market.entity.BankStock;
import com.example.simple_stock_market.entity.WalletStock;
import com.example.simple_stock_market.repository.BankStockRepository;
import com.example.simple_stock_market.repository.WalletStockRepository;
import com.example.simple_stock_market.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TradeServiceValidationTest {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private BankStockRepository bankStockRepository;

    @Autowired
    private WalletStockRepository walletStockRepository;

    @BeforeEach
    void setup() {
        walletStockRepository.deleteAll();
        bankStockRepository.deleteAll();
    }

    @Test
    void shouldFail_whenBuyingWithoutAvailableStock() {
        String stock = "AAPL";

        bankStockRepository.save(BankStock.of(stock, 0));

        var ex = assertThrows(ResponseStatusException.class, () ->
                tradeService.executeTrade("wallet1", stock, "buy")
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode(),
                "Expected BAD_REQUEST when buying without available stock");
    }

    @Test
    void shouldFail_whenSellingWithoutOwningStock() {
        String stock = "AAPL";

        bankStockRepository.save(BankStock.of(stock, 10));

        var ex = assertThrows(ResponseStatusException.class, () ->
                tradeService.executeTrade("wallet1", stock, "sell")
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode(),
                "Expected BAD_REQUEST when selling stock not owned by wallet");
    }

    @Test
    void shouldFail_whenSellingMoreThanOwned() {
        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 0));
        var bankStock = bankStockRepository.findById(stock).orElseThrow();

        walletStockRepository.save(WalletStock.of(walletId, bankStock, 1));

        var ex = assertThrows(ResponseStatusException.class, () -> {
                tradeService.executeTrade(walletId, stock, "sell");
                tradeService.executeTrade(walletId, stock, "sell");
            }
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode(),
                "Expected BAD_REQUEST when selling more stock than owned");
    }

    @Test
    void shouldBuyStock_successfully() {
        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 10));

        tradeService.executeTrade(walletId, stock, "buy");

        int bankQuantity = bankStockRepository.findById(stock)
                .orElseThrow()
                .getQuantity();

        assertEquals(9, bankQuantity, "Bank stock should decrease by 1 after BUY");

        var walletStock = walletStockRepository.findQuantity(walletId, stock)
                .orElseThrow(() -> new AssertionError("Wallet stock should exist after BUY"));

        assertEquals(1, walletStock, "Wallet should have 1 stock after BUY");
    }

    @Test
    void shouldSellAllStock_andRemoveWalletEntry() {
        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 0));
        var bankStock = bankStockRepository.findById(stock)
                .orElseThrow(() -> new AssertionError("Bank stock not found before test"));

        walletStockRepository.save(WalletStock.of(walletId, bankStock, 1));

        tradeService.executeTrade(walletId, stock, "sell");

        int bankQuantity = bankStockRepository.findById(stock)
                .orElseThrow(() -> new AssertionError("Bank stock not found after SELL"))
                .getQuantity();

        assertEquals(1, bankQuantity,
                "Bank stock should increase by 1 after selling all shares");

        var walletStock = walletStockRepository.findQuantity(walletId, stock);

        assertTrue(walletStock.isEmpty(),
                "Wallet entry should be removed after selling all shares");
    }

    @Test
    void shouldSellPartialStock_andKeepWalletEntry() {
        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 0));
        var bankStock = bankStockRepository.findById(stock)
                .orElseThrow(() -> new AssertionError("Bank stock not found before test"));

        walletStockRepository.save(WalletStock.of(walletId, bankStock, 2));

        tradeService.executeTrade(walletId, stock, "sell");

        int bankQuantity = bankStockRepository.findById(stock)
                .orElseThrow(() -> new AssertionError("Bank stock not found after SELL"))
                .getQuantity();

        assertEquals(1, bankQuantity,
                "Bank stock should increase by 1 after partial SELL");

        var walletStock = walletStockRepository.findQuantity(walletId, stock)
                .orElseThrow(() -> new AssertionError("Wallet entry should still exist after partial SELL"));

        assertEquals(1, walletStock,
                "Wallet stock should decrease by 1 after partial SELL");
    }
}