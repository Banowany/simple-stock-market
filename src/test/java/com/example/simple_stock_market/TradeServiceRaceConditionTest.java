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

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TradeServiceRaceConditionTest {
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
    void shouldHandleRaceCondition_buyLastStock_singleWallet() throws InterruptedException {

        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 1));

        int threads = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    tradeService.executeTrade(walletId, stock, "buy");

                    results.add(true);

                } catch (Exception e) {
                    results.add(false);

                    if (e instanceof ResponseStatusException rse &&
                            rse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return;
                    }

                    fail("Unexpected exception during concurrent BUY: "
                            + e.getClass().getSimpleName() + " -> " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        doneLatch.await();
        executor.shutdown();

        long successCount = results.stream().filter(r -> r).count();

        assertEquals(1, successCount,
                "Race condition detected: expected exactly 1 successful BUY, but got " + successCount);

        int bankQuantity = bankStockRepository.findById(stock)
                .orElseThrow(() -> new AssertionError("Bank stock not found after test"))
                .getQuantity();

        assertEquals(0, bankQuantity,
                "Bank stock quantity mismatch: expected 0 after single successful BUY, but was " + bankQuantity);

        var walletStocks = walletStockRepository.findAll();

        assertEquals(1, walletStocks.size(),
                "Wallet state corrupted: expected exactly 1 wallet-stock entry, but found " + walletStocks.size());

        var walletStock = walletStocks.getFirst();

        assertEquals(stock, walletStock.getStock().getName(),
                "Wallet contains incorrect stock. Expected: " + stock + ", but got: " + walletStock.getStock().getName());

        assertEquals(walletId, walletStock.getId().getWalletId(),
                "Wallet ID mismatch. Expected: " + walletId + ", but got: " + walletStock.getId().getWalletId());

        assertEquals(1, walletStock.getQuantity(),
                "Wallet stock quantity mismatch: expected 1 after successful BUY, but was " + walletStock.getQuantity());
    }

    @Test
    void shouldHandleRaceCondition_sellLastStock() throws InterruptedException {

        String stock = "AAPL";
        String walletId = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 0));

        var bankStock = bankStockRepository.findById(stock)
                .orElseThrow(() -> new AssertionError("Bank stock not found before test"));

        walletStockRepository.save(WalletStock.of(walletId, bankStock, 1));

        int threads = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    tradeService.executeTrade(walletId, stock, "sell");

                    results.add(true);

                } catch (Exception e) {
                    results.add(false);

                    if (e instanceof ResponseStatusException rse &&
                            rse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return;
                    }

                    fail("Unexpected exception during concurrent SELL: "
                            + e.getClass().getSimpleName() + " -> " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        doneLatch.await();
        executor.shutdown();

        long successCount = results.stream().filter(r -> r).count();

        assertEquals(1, successCount,
                "Race condition detected: expected exactly 1 successful SELL, but got " + successCount);

        int bankQuantity = bankStockRepository.findById(stock)
                .orElseThrow(() -> new AssertionError("Bank stock not found after SELL test"))
                .getQuantity();

        assertEquals(1, bankQuantity,
                "Bank stock quantity mismatch: expected 1 after single successful SELL, but was " + bankQuantity);

        var walletQuantity = walletStockRepository.findQuantity(walletId, stock);

        assertTrue(walletQuantity.isEmpty(),
                "Wallet state corrupted: stock should be removed after full SELL, but still exists with quantity = "
                        + walletQuantity.orElse(null));
    }
}