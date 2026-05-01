package com.example.simple_stock_market;

import com.example.simple_stock_market.entity.BankStock;
import com.example.simple_stock_market.entity.WalletStock;
import com.example.simple_stock_market.repository.BankStockRepository;
import com.example.simple_stock_market.repository.WalletStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void shouldReturnWalletWithStocks() throws Exception {
        String wallet = "wallet1";
        String stock = "AAPL";

        bankStockRepository.save(BankStock.of(stock, 10));
        var bankStock = bankStockRepository.findById(stock).orElseThrow();

        walletStockRepository.save(WalletStock.of(wallet, bankStock, 2));

        mockMvc.perform(get("/wallets/{wallet}", wallet))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet))
                .andExpect(jsonPath("$.stocks[0].name").value(stock))
                .andExpect(jsonPath("$.stocks[0].quantity").value(2))
                .andExpect(jsonPath("$.stocks", hasSize(1)));;
    }

    @Test
    void shouldReturnEmptyWallet() throws Exception {
        String wallet = "wallet1";

        mockMvc.perform(get("/wallets/{wallet}", wallet))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet))
                .andExpect(jsonPath("$.stocks").isArray())
                .andExpect(jsonPath("$.stocks").isEmpty());
    }

    @Test
    void shouldReturnStockQuantity_forWallet() throws Exception {
        String wallet = "wallet1";
        String stock = "AAPL";

        bankStockRepository.save(BankStock.of(stock, 10));
        var bankStock = bankStockRepository.findById(stock).orElseThrow();

        walletStockRepository.save(WalletStock.of(wallet, bankStock, 3));

        mockMvc.perform(get("/wallets/{wallet}/stocks/{stock}", wallet, stock))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void shouldReturnZero_whenWalletDoesNotHaveStock() throws Exception {
        String wallet = "wallet1";
        String stock = "AAPL";

        bankStockRepository.save(BankStock.of(stock, 10));

        mockMvc.perform(get("/wallets/{wallet}/stocks/{stock}", wallet, stock))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}