package com.example.simple_stock_market.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chaos")
public class ChaosController {
    @PostMapping
    public void chaos() {
        System.exit(1);
    }
}
