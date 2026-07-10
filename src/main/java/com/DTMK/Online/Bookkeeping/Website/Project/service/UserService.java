package com.DTMK.Online.Bookkeeping.Website.Project.service;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.User;
import com.DTMK.Online.Bookkeeping.Website.Project.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    // --- FITUR REGISTER ---
    public String registerUser(String username, String password) {
        User existingUser = userMapper.findByUsername(username);
        if (existingUser != null) {
            return "Gagal: Username sudah terdaftar!";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Nanti kita tambah enkripsi di tahap *finishing*
        newUser.setAvatar("default-avatar.png");

        userMapper.insertUser(newUser);
        return "Sukses: Registrasi berhasil!";
    }

    // --- FITUR LOGIN (BARU) ---
    public String loginUser(String username, String password) {
        // 1. Cari user di database
        User user = userMapper.findByUsername(username);

        // 2. Validasi apakah user ada
        if (user == null) {
            return "Gagal: Username tidak ditemukan!";
        }

        // 3. Validasi apakah password cocok
        if (!user.getPassword().equals(password)) {
            return "Gagal: Password salah!";
        }

        // 4. Jika semua benar (Nantinya kita akan buatkan Token JWT di sini)
        return "Sukses: Berhasil login! Selamat datang, " + user.getUsername();
    }
}