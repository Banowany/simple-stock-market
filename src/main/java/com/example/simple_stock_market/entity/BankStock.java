package com.example.simple_stock_market.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_stocks")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BankStock {
    @Id
    @EqualsAndHashCode.Include
    private String name;

    @Column(nullable = false)
    private Integer quantity;
}
