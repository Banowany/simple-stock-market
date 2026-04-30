package com.example.simple_stock_market.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instance")
public class InstanceController {
    @GetMapping
    public String getInstanceId() {
        return System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "unknown-instance";
    }
}
