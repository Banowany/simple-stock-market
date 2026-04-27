package com.example.simple_stock_market;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank_stocks")
@NoArgsConstructor
@Getter
@Setter
public class BankStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id", unique = true, nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private Long quantity;
}
