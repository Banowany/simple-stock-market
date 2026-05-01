package com.example.simple_stock_market;

import com.example.simple_stock_market.entity.BankStock;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
    void shouldHandleRaceCondition_buyLastStock() throws InterruptedException {

        String stock = "AAPL";

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

                    tradeService.executeTrade(UUID.randomUUID().toString(), stock, "buy");

                    results.add(true);

                } catch (Exception e) {
                    results.add(false);

                    if (e instanceof ResponseStatusException rse) {
                        if (rse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            return;
                        }
                    }

                    fail("Unexpected exception: " + e.getClass() + " -> " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        //start every thread
        startLatch.countDown();

        doneLatch.await();
        executor.shutdown();

        long successCount = results.stream().filter(r -> r).count();

        assertEquals(1, successCount, "More than one transaction succeeded — race condition!");

        int bankQuantity = bankStockRepository.findById(stock)
                .orElseThrow()
                .getQuantity();

        assertEquals(0, bankQuantity, "Bank stock should be 0");

        var walletStocks = walletStockRepository.findAll();

        assertEquals(1, walletStocks.size(), "Only one wallet stock should exist");

        var walletStock = walletStocks.getFirst();

        assertEquals(stock, walletStock.getStock().getName(), "Wallet stock should contain declared stock");
        assertEquals(1, walletStock.getQuantity(), "Wallet stock should have quantity 1");
    }
}