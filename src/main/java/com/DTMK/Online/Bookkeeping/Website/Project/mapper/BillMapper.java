package com.DTMK.Online.Bookkeeping.Website.Project.mapper;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BillMapper {

    // Mengambil semua data tagihan milik user tertentu, diurutkan dari yang terbaru (tanggalnya)
    @Select("SELECT * FROM t_bill WHERE user_id = #{userId} ORDER BY bill_date DESC")
    List<Bill> findBillsByUserId(Integer userId);

    // Nanti kita akan tambahkan fungsi Insert, Update, Delete di sini pada fase CRUD
}