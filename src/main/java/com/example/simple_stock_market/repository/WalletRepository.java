package com.example.simple_stock_market.repository;

import com.example.simple_stock_market.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    @Modifying
    @Query(value = """
        INSERT INTO wallet (id)
        VALUES (:id)
        ON CONFLICT (id) DO NOTHING
    """, nativeQuery = true)
    void createIfNotExists(@Param("id") String id);
}
