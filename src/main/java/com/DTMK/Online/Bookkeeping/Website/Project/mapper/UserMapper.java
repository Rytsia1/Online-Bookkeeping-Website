package com.DTMK.Online.Bookkeeping.Website.Project.mapper;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserMapper {

    // Mencari user berdasarkan username (berguna untuk Login dan cek duplikasi saat Register)
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User findByUsername(String username);

    // Menyimpan user baru ke database (berguna untuk Register)
    @Insert("INSERT INTO t_user(username, password, avatar) VALUES(#{username}, #{password}, #{avatar})")
    void insertUser(User user);

    // Mencari user berdasarkan ID
    @Select("SELECT * FROM t_user WHERE id = #{id}")
    User findById(@Param("id") Integer id);

    // Memperbarui target anggaran bulanan user
    @Update("UPDATE t_user SET monthly_budget = #{monthlyBudget} WHERE id = #{userId}")
    void updateMonthlyBudget(@Param("userId") Integer userId, @Param("monthlyBudget") BigDecimal monthlyBudget);
}
