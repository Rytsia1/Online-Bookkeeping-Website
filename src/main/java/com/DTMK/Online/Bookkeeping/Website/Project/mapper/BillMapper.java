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

    // Update: Mengedit tagihan
    @org.apache.ibatis.annotations.Update("UPDATE t_bill SET amount=#{amount}, type=#{type}, category=#{category}, description=#{description}, bill_date=#{billDate} WHERE id=#{id}")
    void updateBill(Bill bill);

    // Mengambil statistik pengeluaran per kategori untuk Pie Chart (Hanya untuk type = 0 / Pengeluaran)
    @org.apache.ibatis.annotations.Select("SELECT category AS name, SUM(amount) AS value FROM t_bill " +
            "WHERE user_id = #{userId} AND type = 0 AND MONTH(bill_date) = #{month} AND YEAR(bill_date) = #{year} " +
            "GROUP BY category")
    java.util.List<com.DTMK.Online.Bookkeeping.Website.Project.dto.CategoryStatDTO> getExpenseByCategory(
            @org.apache.ibatis.annotations.Param("userId") Integer userId,
            @org.apache.ibatis.annotations.Param("month") int month,
            @org.apache.ibatis.annotations.Param("year") int year);
    // Mengambil daftar tagihan berdasarkan kategori & bulan untuk detail chart
    @Select("SELECT * FROM t_bill " +
            "WHERE user_id = #{userId} AND category = #{category} " +
            "AND type = 0 " +
            "AND MONTH(bill_date) = #{month} AND YEAR(bill_date) = #{year} " +
            "ORDER BY bill_date DESC")
    List<Bill> findBillsByCategoryAndMonth(
            @org.apache.ibatis.annotations.Param("userId") Integer userId,
            @org.apache.ibatis.annotations.Param("category") String category,
            @org.apache.ibatis.annotations.Param("month") int month,
            @org.apache.ibatis.annotations.Param("year") int year);
    // Mengambil daftar tagihan berdasarkan type (0=expense, 1=income) & bulan
    @Select("SELECT * FROM t_bill " +
            "WHERE user_id = #{userId} AND type = #{type} " +
            "AND MONTH(bill_date) = #{month} AND YEAR(bill_date) = #{year} " +
            "ORDER BY bill_date DESC")
    List<Bill> findBillsByTypeAndMonth(
            @org.apache.ibatis.annotations.Param("userId") Integer userId,
            @org.apache.ibatis.annotations.Param("type") int type,
            @org.apache.ibatis.annotations.Param("month") int month,
            @org.apache.ibatis.annotations.Param("year") int year);
} // <--- Pastikan kurung penutup interface berada di paling bawah