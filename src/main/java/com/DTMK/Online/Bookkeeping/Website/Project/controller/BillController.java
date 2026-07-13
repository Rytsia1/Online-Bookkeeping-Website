package com.DTMK.Online.Bookkeeping.Website.Project.controller;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.Bill;
import com.DTMK.Online.Bookkeeping.Website.Project.mapper.BillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills") // Diubah menjadi jamak (bills)
@RequiredArgsConstructor
public class BillController {

    private final BillMapper billMapper;

    // GET /api/bills?userId=1
    @GetMapping
    public ResponseEntity<List<Bill>> getBills(@RequestParam Integer userId) {
        return ResponseEntity.ok(billMapper.findBillsByUserId(userId));
    }

    // POST /api/bills
    @PostMapping
    public ResponseEntity<Map<String, String>> addBill(@RequestBody Bill bill) {
        billMapper.insertBill(bill);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Berhasil menambahkan tagihan");
        return ResponseEntity.ok(response);
    }

    // PUT /api/bills/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateBill(@PathVariable Integer id, @RequestBody Bill bill) {
        bill.setId(id);
        billMapper.updateBill(bill);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Berhasil memperbarui tagihan");
        return ResponseEntity.ok(response);
    }

    // DELETE /api/bills/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBill(@PathVariable Integer id) {
        billMapper.deleteBill(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Berhasil menghapus tagihan");
        return ResponseEntity.ok(response);
    }
}