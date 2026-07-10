package com.DTMK.Online.Bookkeeping.Website.Project.mapper;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    // Mencari user berdasarkan username (berguna untuk Login dan cek duplikasi saat Register)
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User findByUsername(String username);

    // Menyimpan user baru ke database (berguna untuk Register)
    @Insert("INSERT INTO t_user(username, password, avatar) VALUES(#{username}, #{password}, #{avatar})")
    void insertUser(User user);
}