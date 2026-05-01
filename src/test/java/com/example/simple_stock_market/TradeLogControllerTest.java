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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TradeLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void shouldReturnTradeLogs_afterMultipleTrades() throws Exception {

        String stock = "AAPL";
        String wallet = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 10));

        tradeService.executeTrade(wallet, stock, "buy");
        tradeService.executeTrade(wallet, stock, "buy");
        tradeService.executeTrade(wallet, stock, "sell");

        mockMvc.perform(get("/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.log", hasSize(3)))
                .andExpect(jsonPath("$.log[*].type",
                        containsInAnyOrder("buy", "buy", "sell")))
                .andExpect(jsonPath("$.log[*].walletId",
                        everyItem(is(wallet))))
                .andExpect(jsonPath("$.log[*].stockName",
                        everyItem(is(stock))));
    }

    @Test
    void shouldReturnLogs_inCorrectOrder() throws Exception {

        String stock = "AAPL";
        String wallet = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 10));

        tradeService.executeTrade(wallet, stock, "buy");
        tradeService.executeTrade(wallet, stock, "sell");

        mockMvc.perform(get("/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.log", hasSize(2)))
                .andExpect(jsonPath("$.log[0].type").value("sell"))
                .andExpect(jsonPath("$.log[1].type").value("buy"));
    }

    @Test
    void shouldReturnEmptyList_whenNoLogs() throws Exception {

        mockMvc.perform(get("/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.log").isArray())
                .andExpect(jsonPath("$.log").isEmpty());
    }
}