package com.DTMK.Online.Bookkeeping.Website.Project.controller;

import com.DTMK.Online.Bookkeeping.Website.Project.dto.MonthlyStatsDTO;
import com.DTMK.Online.Bookkeeping.Website.Project.entity.Bill;
import com.DTMK.Online.Bookkeeping.Website.Project.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    // Menampilkan daftar tagihan milik user tertentu
    @GetMapping("/list")
    public List<Bill> getBills(@RequestParam Integer userId) {
        return billService.getBills(userId);
    }

    // Menambahkan tagihan (Menggunakan @RequestBody karena datanya berbentuk JSON)
    @PostMapping("/add")
    public String addBill(@RequestBody Bill bill) {
        return billService.addBill(bill);
    }

    // Menghapus tagihan
    @DeleteMapping("/delete")
    public String deleteBill(@RequestParam Integer id) {
        return billService.deleteBill(id);
    }

    // Menampilkan statistik bulanan
    @GetMapping("/stats/monthly")
    public MonthlyStatsDTO getMonthlyStats(
            @RequestParam Integer userId,
            @RequestParam int month,
            @RequestParam int year) {
        return billService.getMonthlyStats(userId, month, year);
    }

} // <--- Pastikan kurung penutup class berada di paling bawah