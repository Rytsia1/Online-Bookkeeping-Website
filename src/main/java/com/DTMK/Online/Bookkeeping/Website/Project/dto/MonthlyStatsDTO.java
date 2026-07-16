package com.DTMK.Online.Bookkeeping.Website.Project.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MonthlyStatsDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;          // Saldo (Pemasukan - Pengeluaran)
    private BigDecimal monthlyBudget;    // Target anggaran (null jika belum diset)
    private BigDecimal budgetUsedPercent; // Persentase pengeluaran (0-100+)
    private boolean budgetWarning;        // true jika >= 80%
    private boolean budgetExceeded;       // true jika >= 100%
}
