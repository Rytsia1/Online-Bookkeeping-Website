package com.DTMK.Online.Bookkeeping.Website.Project.service;

import com.DTMK.Online.Bookkeeping.Website.Project.dto.MonthlyStatsDTO;
import com.DTMK.Online.Bookkeeping.Website.Project.entity.Bill;
import com.DTMK.Online.Bookkeeping.Website.Project.mapper.BillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillMapper billMapper;

    public List<Bill> getBills(Integer userId) {
        return billMapper.findBillsByUserId(userId);
    }

    public String addBill(Bill bill) {
        billMapper.insertBill(bill);
        return "Sukses: Catatan keuangan berhasil ditambahkan!";
    }

    public String deleteBill(Integer id) {
        billMapper.deleteBill(id);
        return "Sukses: Catatan keuangan berhasil dihapus!";
    }

    public List<com.DTMK.Online.Bookkeeping.Website.Project.dto.CategoryStatDTO> getExpenseByCategory(Integer userId, int month, int year) {
        return billMapper.getExpenseByCategory(userId, month, year);
    }

    public MonthlyStatsDTO getMonthlyStats(Integer userId, int month, int year) {
        BigDecimal income = billMapper.calculateMonthlyIncome(userId, month, year);
        BigDecimal expense = billMapper.calculateMonthlyExpense(userId, month, year);
        BigDecimal balance = income.subtract(expense); // Pemasukan dikurangi Pengeluaran

        MonthlyStatsDTO stats = new MonthlyStatsDTO();
        stats.setTotalIncome(income);
        stats.setTotalExpense(expense);
        stats.setBalance(balance);

        return stats;
    }

} // <--- Pastikan kurung penutup class berada di paling bawah