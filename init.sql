-- =========================
-- BANK_STOCKS
-- =========================
CREATE TABLE bank_stocks (
                             name STRING PRIMARY KEY,
                             quantity INT NOT NULL
);


-- =========================
-- TRADE_LOGS
-- =========================
CREATE TABLE trade_logs (
                            id BIGINT PRIMARY KEY DEFAULT unique_rowid(),
                            type STRING NOT NULL,
                            wallet_id STRING NOT NULL,
                            stock_name STRING NOT NULL,
                            created_at TIMESTAMP NOT NULL
);


-- =========================
-- WALLET_STOCKS
-- =========================
CREATE TABLE wallet_stocks (
                               wallet_id STRING NOT NULL,
                               stock_name STRING NOT NULL,
                               quantity INT NOT NULL,

                               PRIMARY KEY (wallet_id, stock_name),

                               CONSTRAINT fk_wallet_stock_bank_stock
                                   FOREIGN KEY (stock_name)
                                       REFERENCES bank_stocks(name)
);