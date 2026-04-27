package com.example.simple_stock_market;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallet_stocks")
@Getter
@Setter
@NoArgsConstructor
public class WalletStock {

    @EmbeddedId
    private WalletStockId id;

    @Column(nullable = false)
    private Integer quantity;

    // Relacja do portfela (opcjonalnie, ale przydatne)
    // mapsId mówi JPA, że walletId w kluczu to to samo co ID w encji Wallet
    @ManyToOne
    @MapsId("walletId")
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
