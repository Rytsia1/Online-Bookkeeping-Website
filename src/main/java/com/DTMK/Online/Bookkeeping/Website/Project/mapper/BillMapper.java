package com.DTMK.Online.Bookkeeping.Website.Project.mapper;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.Bill;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BillMapper {

    // Read: Mengambil tagihan berdasarkan ID User
    @Select("SELECT * FROM t_bill WHERE user_id = #{userId} ORDER BY bill_date DESC")
    List<Bill> findBillsByUserId(Integer userId);

    // Create: Menambahkan tagihan baru
    @Insert("INSERT INTO t_bill(user_id, amount, type, category, description, bill_date) " +
            "VALUES(#{userId}, #{amount}, #{type}, #{category}, #{description}, #{billDate})")
    void insertBill(Bill bill);

    // Delete: Menghapus tagihan berdasarkan ID Tagihan
    @Delete("DELETE FROM t_bill WHERE id = #{id}")
    void deleteBill(Integer id);

    // Menghitung total pemasukan bulanan
    @Select("SELECT COALESCE(SUM(amount), 0) FROM t_bill WHERE user_id = #{userId} AND type = 1 AND MONTH(bill_date) = #{month} AND YEAR(bill_date) = #{year}")
    BigDecimal calculateMonthlyIncome(Integer userId, int month, int year);

    // Menghitung total pengeluaran bulanan
    @Select("SELECT COALESCE(SUM(amount), 0) FROM t_bill WHERE user_id = #{userId} AND type = 0 AND MONTH(bill_date) = #{month} AND YEAR(bill_date) = #{year}")
    BigDecimal calculateMonthlyExpense(Integer userId, int month, int year);

} // <--- Pastikan kurung penutup interface berada di paling bawah