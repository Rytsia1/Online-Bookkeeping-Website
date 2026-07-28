-- Monthly Budget Feature Migration
-- Run this against your db_bookkeeping database

ALTER TABLE t_user
    ADD COLUMN monthly_budget DECIMAL(15, 2) DEFAULT NULL COMMENT 'Target pengeluaran maksimal per bulan';

-- Savings Goal / Wishlist Tracker Migration
-- Run this against your db_bookkeeping database

CREATE TABLE IF NOT EXISTS t_saving_goal (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT            NOT NULL,
    name        VARCHAR(100)   NOT NULL COMMENT 'Nama target tabungan',
    target      DECIMAL(15,2)  NOT NULL COMMENT 'Jumlah target yang ingin dicapai',
    saved       DECIMAL(15,2)  NOT NULL DEFAULT 0 COMMENT 'Jumlah yang sudah dialokasikan',
    note        VARCHAR(255)   DEFAULT NULL COMMENT 'Catatan tambahan',
    completed   TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '1 jika sudah tercapai',
    created_at  DATETIME       DEFAULT CURRENT_TIMESTAMP
);
