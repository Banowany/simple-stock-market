package com.example.simple_stock_market.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_stocks")
@NoArgsConstructor
@Getter
public class BankStock {
    @Id
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    public static BankStock of(String name, Integer quantity) {
        var res = new BankStock();
        res.name = name;
        res.quantity = quantity;
        return res;
    }
}
