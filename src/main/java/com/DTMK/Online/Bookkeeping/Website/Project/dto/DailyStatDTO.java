package com.DTMK.Online.Bookkeeping.Website.Project.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyStatDTO {
    private LocalDate date;   // Tanggal (Misal: 2026-07-16)
    private BigDecimal total; // Total pengeluaran pada tanggal tersebut
}
