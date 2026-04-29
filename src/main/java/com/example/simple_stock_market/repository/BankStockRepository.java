package com.example.simple_stock_market.repository;

import com.example.simple_stock_market.entity.BankStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankStockRepository extends JpaRepository<BankStock, String> {
    @Modifying
    @Query("""
        UPDATE BankStock b
        SET b.quantity = b.quantity - 1
        WHERE b.name = :name AND b.quantity > 0
    """)
    int decrement(@Param("name") String name);

    @Modifying
    @Query("""
        UPDATE BankStock b
        SET b.quantity = b.quantity + 1
        WHERE b.name = :name
    """)
    int increment(@Param("name") String name);
}
