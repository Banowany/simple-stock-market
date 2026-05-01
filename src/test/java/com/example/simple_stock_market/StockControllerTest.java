package com.example.simple_stock_market;

import com.example.simple_stock_market.entity.BankStock;
import com.example.simple_stock_market.entity.WalletStock;
import com.example.simple_stock_market.repository.BankStockRepository;
import com.example.simple_stock_market.repository.WalletStockRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankStockRepository bankStockRepository;

    @Autowired
    private WalletStockRepository walletStockRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        walletStockRepository.deleteAll();
        bankStockRepository.deleteAll();
    }

    @Test
    void shouldReturnAllBankStocks() throws Exception {
        bankStockRepository.save(BankStock.of("AAPL", 5));
        bankStockRepository.save(BankStock.of("GOOG", 3));

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks", hasSize(2)))
                .andExpect(jsonPath("$.stocks[*].name",
                        containsInAnyOrder("AAPL", "GOOG")))
                .andExpect(jsonPath("$.stocks[?(@.name=='AAPL')].quantity")
                        .value(5))
                .andExpect(jsonPath("$.stocks[?(@.name=='GOOG')].quantity")
                        .value(3));
    }

    @Test
    void shouldReturnEmptyStockList_whenNoStocks() throws Exception {
        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks").isArray())
                .andExpect(jsonPath("$.stocks").isEmpty());
    }

    @Test
    void shouldSetBankState_withMultipleStocks() throws Exception {

        var body = Map.of(
                "stocks", List.of(
                        Map.of("name", "AAPL", "quantity", 10),
                        Map.of("name", "GOOG", "quantity", 5)
                )
        );

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        assertEquals(2, bankStockRepository.findAll().size());

        assertEquals(10, bankStockRepository.findById("AAPL").orElseThrow().getQuantity());
        assertEquals(5, bankStockRepository.findById("GOOG").orElseThrow().getQuantity());
    }

    @Test
    void shouldOverwritePreviousBankState() throws Exception {

        bankStockRepository.save(BankStock.of("AAPL", 100));

        var body = Map.of(
                "stocks", List.of(
                        Map.of("name", "GOOG", "quantity", 5)
                )
        );

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        assertEquals(1, bankStockRepository.findAll().size());
        assertFalse(bankStockRepository.findById("AAPL").isPresent());
        assertEquals(5, bankStockRepository.findById("GOOG").orElseThrow().getQuantity());
    }

    @Test
    void shouldClearBank_whenEmptyListProvided() throws Exception {

        bankStockRepository.save(BankStock.of("AAPL", 10));

        var body = Map.of("stocks", List.of());

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        assertTrue(bankStockRepository.findAll().isEmpty());
    }

    @Test
    void shouldClearWallets_whenBankStateIsReset() throws Exception {

        String stock = "AAPL";
        String wallet = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 10));
        var bankStock = bankStockRepository.findById(stock).orElseThrow();

        walletStockRepository.save(WalletStock.of(wallet, bankStock, 2));

        var body = Map.of(
                "stocks", List.of(
                        Map.of("name", "GOOG", "quantity", 5)
                )
        );

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        assertTrue(walletStockRepository.findAll().isEmpty(),
                "Wallets should be cleared after bank reset");
    }
}