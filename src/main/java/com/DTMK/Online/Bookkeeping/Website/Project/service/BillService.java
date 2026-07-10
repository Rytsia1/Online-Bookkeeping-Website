package com.DTMK.Online.Bookkeeping.Website.Project.service;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.Bill;
import com.DTMK.Online.Bookkeeping.Website.Project.mapper.BillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}