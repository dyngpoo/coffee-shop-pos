package com.timkasir.dao;

import com.timkasir.model.User;
import com.timkasir.util.Database;
import com.timkasir.util.Helper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /** Login: cek username + password (MD5) + status aktif */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 'aktif'";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, Helper.md5(password));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    /** Ambil semua user */
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, nama_lengkap";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapUser(rs));
        }
        return list;
    }

    /** Tambah user baru */
    public boolean tambah(User u) throws SQLException {
        String sql = "INSERT INTO users (username,password,nama_lengkap,no_hp,alamat,role,status) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, Helper.md5(u.getPassword()));
            ps.setString(3, u.getNamaLengkap());
            ps.setString(4, u.getNoHp());
            ps.setString(5, u.getAlamat());
            ps.setString(6, u.getRole());
            ps.setString(7, u.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    /** Update user (tanpa ubah password jika kosong) */
    public boolean update(User u) throws SQLException {
        String sql;
        if (u.getPassword() != null && !u.getPassword().isEmpty()) {
            sql = "UPDATE users SET username=?, password=?, nama_lengkap=?, no_hp=?, alamat=?, role=?, status=? WHERE id_user=?";
        } else {
            sql = "UPDATE users SET username=?, nama_lengkap=?, no_hp=?, alamat=?, role=?, status=? WHERE id_user=?";
        }
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                ps.setString(1, u.getUsername());
                ps.setString(2, Helper.md5(u.getPassword()));
                ps.setString(3, u.getNamaLengkap());
                ps.setString(4, u.getNoHp());
                ps.setString(5, u.getAlamat());
                ps.setString(6, u.getRole());
                ps.setString(7, u.getStatus());
                ps.setInt(8, u.getIdUser());
            } else {
                ps.setString(1, u.getUsername());
                ps.setString(2, u.getNamaLengkap());
                ps.setString(3, u.getNoHp());
                ps.setString(4, u.getAlamat());
                ps.setString(5, u.getRole());
                ps.setString(6, u.getStatus());
                ps.setInt(7, u.getIdUser());
            }
            return ps.executeUpdate() > 0;
        }
    }

    /** Hapus user */
    public boolean hapus(int idUser) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            return ps.executeUpdate() > 0;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setIdUser(rs.getInt("id_user"));
        u.setUsername(rs.getString("username"));
        u.setNamaLengkap(rs.getString("nama_lengkap"));
        u.setNoHp(rs.getString("no_hp"));
        u.setAlamat(rs.getString("alamat"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        return u;
    }
}