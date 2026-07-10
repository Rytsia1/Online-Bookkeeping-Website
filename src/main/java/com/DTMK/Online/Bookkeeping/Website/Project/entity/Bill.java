package com.DTMK.Online.Bookkeeping.Website.Project.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Bill {
    private Integer id;
    private Integer userId;
    private BigDecimal amount; // Sesuai standar industri keuangan, gunakan BigDecimal
    private Integer type;      // 0 untuk pengeluaran, 1 untuk pemasukan
    private String category;
    private String description;
    private LocalDate billDate;
    private LocalDateTime createdAt;
}