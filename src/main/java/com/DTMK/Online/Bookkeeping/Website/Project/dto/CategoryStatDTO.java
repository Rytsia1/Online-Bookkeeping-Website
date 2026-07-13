package com.DTMK.Online.Bookkeeping.Website.Project.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CategoryStatDTO {
    private String name;  // Nama Kategori (Misal: "Makanan")
    private BigDecimal value; // Total Pengeluaran (Misal: 50000.00)
}