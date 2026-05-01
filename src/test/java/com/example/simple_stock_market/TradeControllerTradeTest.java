package com.example.simple_stock_market;

import com.example.simple_stock_market.entity.BankStock;
import com.example.simple_stock_market.repository.BankStockRepository;
import com.example.simple_stock_market.repository.TradeLogRepository;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TradeControllerTradeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankStockRepository bankStockRepository;

    @Autowired
    private WalletStockRepository walletStockRepository;

    @Autowired
    private TradeLogRepository tradeLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        tradeLogRepository.deleteAll();
        walletStockRepository.deleteAll();
        bankStockRepository.deleteAll();
    }

    @Test
    void shouldBuyStock_andCreateWallet() throws Exception {
        String stock = "AAPL";
        String wallet = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 1));

        mockMvc.perform(post("/wallets/{wallet}/stocks/{stock}", wallet, stock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("type", "buy"))))
                .andExpect(status().isOk());

        int bankQty = bankStockRepository.findById(stock).orElseThrow().getQuantity();
        assertEquals(0, bankQty, "Bank stock should decrease");

        var walletQty = walletStockRepository.findQuantity(wallet, stock).orElseThrow();
        assertEquals(1, walletQty, "Wallet should receive stock");
    }

    @Test
    void shouldReturn400_whenBuyingWithoutStock() throws Exception {
        String stock = "AAPL";

        bankStockRepository.save(BankStock.of(stock, 0));

        mockMvc.perform(post("/wallets/{wallet}/stocks/{stock}", "wallet1", stock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("type", "buy"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_whenBuyingNonExistingStock() throws Exception {
        mockMvc.perform(post("/wallets/{wallet}/stocks/{stock}", "wallet1", "UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("type", "buy"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSellAllStock_andRemoveWalletEntry() throws Exception {
        String stock = "AAPL";
        String wallet = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 0));
        var bankStock = bankStockRepository.findById(stock).orElseThrow();

        walletStockRepository.save(
                com.example.simple_stock_market.entity.WalletStock.of(wallet, bankStock, 1)
        );

        mockMvc.perform(post("/wallets/{wallet}/stocks/{stock}", wallet, stock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("type", "sell"))))
                .andExpect(status().isOk());

        int bankQty = bankStockRepository.findById(stock).orElseThrow().getQuantity();
        assertEquals(1, bankQty, "Bank stock should increase");

        assertTrue(walletStockRepository.findQuantity(wallet, stock).isEmpty(),
                "Wallet entry should be removed");
    }

    @Test
    void shouldSellPartialStock_andKeepWalletEntry() throws Exception {
        String stock = "AAPL";
        String wallet = "wallet1";

        bankStockRepository.save(BankStock.of(stock, 0));
        var bankStock = bankStockRepository.findById(stock).orElseThrow();

        walletStockRepository.save(
                com.example.simple_stock_market.entity.WalletStock.of(wallet, bankStock, 2)
        );

        mockMvc.perform(post("/wallets/{wallet}/stocks/{stock}", wallet, stock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("type", "sell"))))
                .andExpect(status().isOk());

        int bankQty = bankStockRepository.findById(stock).orElseThrow().getQuantity();
        assertEquals(1, bankQty);

        int walletQty = walletStockRepository.findQuantity(wallet, stock).orElseThrow();
        assertEquals(1, walletQty);
    }

    @Test
    void shouldReturn400_whenSellingWithoutStock() throws Exception {
        String stock = "AAPL";

        bankStockRepository.save(BankStock.of(stock, 10));

        mockMvc.perform(post("/wallets/{wallet}/stocks/{stock}", "wallet1", stock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("type", "sell"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_whenSellingNonExistingStock() throws Exception {
        mockMvc.perform(post("/wallets/{wallet}/stocks/{stock}", "wallet1", "UNKNOWN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("type", "sell"))))
                .andExpect(status().isNotFound());
    }
}
