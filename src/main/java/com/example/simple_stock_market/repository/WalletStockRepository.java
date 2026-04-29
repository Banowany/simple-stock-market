package com.example.simple_stock_market.repository;

import com.example.simple_stock_market.entity.WalletStock;
import com.example.simple_stock_market.entity.WalletStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletStockRepository extends JpaRepository<WalletStock, WalletStockId> {
    List<WalletStock> findByWalletId(String walletId);

    @Modifying
    @Query(value = """
        INSERT INTO wallet_stock (wallet_id, stock_name, quantity)
        VALUES (:walletId, :stockName, 1)
        ON CONFLICT (wallet_id, stock_name)
        DO UPDATE SET quantity = wallet_stock.quantity + 1
    """, nativeQuery = true)
    void upsertIncrement(@Param("walletId") String walletId,
                         @Param("stockName") String stockName);

    @Modifying
    @Query("""
        UPDATE WalletStock w
        SET w.quantity = w.quantity - 1
        WHERE w.wallet.id = :walletId
          AND w.stock.name = :stockName
          AND w.quantity > 0
    """)
    int decrement(@Param("walletId") String walletId,
                  @Param("stockName") String stockName);

    @Modifying
    @Query("""
        DELETE FROM WalletStock w
        WHERE w.wallet.id = :walletId
          AND w.stock.name = :stockName
          AND w.quantity = 0
    """)
    void deleteIfZero(@Param("walletId") String walletId,
                      @Param("stockName") String stockName);

    @Query("""
        SELECT w.quantity
        FROM WalletStock w
        WHERE w.wallet.id = :walletId
          AND w.stock.name = :stockName
    """)
    Optional<Integer> findQuantity(@Param("walletId") String walletId,
                          @Param("stockName") String stockName);
}
