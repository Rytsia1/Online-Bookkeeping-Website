package com.DTMK.Online.Bookkeeping.Website.Project.controller;

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
}