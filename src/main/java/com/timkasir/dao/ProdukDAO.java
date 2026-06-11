package com.timkasir.dao;

import com.timkasir.model.Produk;
import com.timkasir.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukDAO {

    /** Semua produk aktif + nama kategori */
    public List<Produk> getAll() throws SQLException {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kategori FROM produk p "
                   + "JOIN kategori k ON p.id_kategori = k.id_kategori "
                   + "WHERE p.status = 'aktif' ORDER BY k.id_kategori, p.nama_produk";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Filter by kategori */
    public List<Produk> getByKategori(int idKategori) throws SQLException {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kategori FROM produk p "
                   + "JOIN kategori k ON p.id_kategori = k.id_kategori "
                   + "WHERE p.status = 'aktif' AND p.id_kategori = ? ORDER BY p.nama_produk";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idKategori);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Semua produk (termasuk nonaktif) untuk manajemen stok */
    public List<Produk> getAllForManagement() throws SQLException {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kategori FROM produk p "
                   + "JOIN kategori k ON p.id_kategori = k.id_kategori "
                   + "ORDER BY k.id_kategori, p.nama_produk";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean tambah(Produk p) throws SQLException {
        String sql = "INSERT INTO produk (nama_produk,harga,id_kategori,foto,stok,status) VALUES (?,?,?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNamaProduk());
            ps.setInt(2, p.getHarga());
            ps.setInt(3, p.getIdKategori());
            ps.setString(4, p.getFoto());
            ps.setInt(5, p.getStok());
            ps.setString(6, p.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Produk p) throws SQLException {
        String sql = "UPDATE produk SET nama_produk=?,harga=?,id_kategori=?,foto=?,stok=?,status=? WHERE id_produk=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNamaProduk());
            ps.setInt(2, p.getHarga());
            ps.setInt(3, p.getIdKategori());
            ps.setString(4, p.getFoto());
            ps.setInt(5, p.getStok());
            ps.setString(6, p.getStatus());
            ps.setInt(7, p.getIdProduk());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hapus(int idProduk) throws SQLException {
        String sql = "UPDATE produk SET status='nonaktif' WHERE id_produk=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProduk);
            return ps.executeUpdate() > 0;
        }
    }

    /** Update stok setelah transaksi */
    public boolean kurangiStok(int idProduk, int qty) throws SQLException {
        String sql = "UPDATE produk SET stok = stok - ? WHERE id_produk = ? AND stok >= ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, idProduk);
            ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        }
    }

    private Produk map(ResultSet rs) throws SQLException {
        Produk p = new Produk();
        p.setIdProduk(rs.getInt("id_produk"));
        p.setNamaProduk(rs.getString("nama_produk"));
        p.setHarga(rs.getInt("harga"));
        p.setIdKategori(rs.getInt("id_kategori"));
        p.setNamaKategori(rs.getString("nama_kategori"));
        p.setFoto(rs.getString("foto"));
        p.setStok(rs.getInt("stok"));
        p.setStatus(rs.getString("status"));
        return p;
    }
}