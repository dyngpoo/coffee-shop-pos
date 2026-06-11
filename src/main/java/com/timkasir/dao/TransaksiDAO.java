package com.timkasir.dao;

import com.timkasir.model.CartItem;
import com.timkasir.model.Transaksi;
import com.timkasir.util.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    /**
     * Simpan transaksi + detail sekaligus dalam satu transaksi DB (atomic).
     * @return id_transaksi yang baru dibuat
     */
    public int simpan(Transaksi t, List<CartItem> items) throws SQLException {
        Connection con = Database.getConnection();
        con.setAutoCommit(false);
        try {
            // 1. Insert header transaksi
            String sqlT = "INSERT INTO transaksi (no_transaksi,tgl_transaksi,id_pelanggan,id_kasir,"
                        + "subtotal,diskon,pajak,total,metode_pembayaran,jumlah_dibayar,kembalian) "
                        + "VALUES (?,NOW(),?,?,?,?,?,?,?,?,?)";
            int idTransaksi;
            try (PreparedStatement ps = con.prepareStatement(sqlT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, t.getNoTransaksi());
                if (t.getIdPelanggan() != null) ps.setInt(2, t.getIdPelanggan());
                else ps.setNull(2, Types.INTEGER);
                ps.setInt(3, t.getIdKasir());
                ps.setInt(4, t.getSubtotal());
                ps.setInt(5, t.getDiskon());
                ps.setInt(6, t.getPajak());
                ps.setInt(7, t.getTotal());
                ps.setString(8, t.getMetodePembayaran());
                ps.setInt(9, t.getJumlahDibayar());
                ps.setInt(10, t.getKembalian());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                idTransaksi = rs.getInt(1);
            }

            // 2. Insert detail
            String sqlD = "INSERT INTO detail_transaksi (id_transaksi,id_produk,nama_produk,harga_satuan,qty,subtotal) "
                        + "VALUES (?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sqlD)) {
                for (CartItem item : items) {
                    ps.setInt(1, idTransaksi);
                    ps.setInt(2, item.getIdProduk());
                    ps.setString(3, item.getNamaProduk());
                    ps.setInt(4, item.getHarga());
                    ps.setInt(5, item.getQty());
                    ps.setInt(6, item.getSubtotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return idTransaksi;

        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /** Ambil semua transaksi untuk laporan */
    public List<Transaksi> getAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, u.nama_lengkap AS nama_kasir, "
                   + "COALESCE(p.nama_pelanggan,'Umum') AS nama_pelanggan "
                   + "FROM transaksi t "
                   + "JOIN users u ON t.id_kasir = u.id_user "
                   + "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                   + "ORDER BY t.tgl_transaksi DESC";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Total penjualan hari ini */
    public long getTotalHariIni() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total),0) FROM transaksi WHERE DATE(tgl_transaksi) = CURDATE()";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    /** Jumlah transaksi hari ini */
    public int getJumlahTransaksiHariIni() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transaksi WHERE DATE(tgl_transaksi) = CURDATE()";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Produk terlaris (top 5) */
    public ResultSet getProdukTerlaris() throws SQLException {
        String sql = "SELECT d.nama_produk, SUM(d.qty) AS total_terjual "
                   + "FROM detail_transaksi d GROUP BY d.id_produk, d.nama_produk "
                   + "ORDER BY total_terjual DESC LIMIT 5";
        Connection con = Database.getConnection();
        Statement st = con.createStatement();
        return st.executeQuery(sql);
    }

    private Transaksi map(ResultSet rs) throws SQLException {
        Transaksi t = new Transaksi();
        t.setIdTransaksi(rs.getInt("id_transaksi"));
        t.setNoTransaksi(rs.getString("no_transaksi"));
        Timestamp ts = rs.getTimestamp("tgl_transaksi");
        if (ts != null) t.setTglTransaksi(ts.toLocalDateTime());
        t.setNamaKasir(rs.getString("nama_kasir"));
        t.setNamaPelanggan(rs.getString("nama_pelanggan"));
        t.setSubtotal(rs.getInt("subtotal"));
        t.setDiskon(rs.getInt("diskon"));
        t.setPajak(rs.getInt("pajak"));
        t.setTotal(rs.getInt("total"));
        t.setMetodePembayaran(rs.getString("metode_pembayaran"));
        t.setJumlahDibayar(rs.getInt("jumlah_dibayar"));
        t.setKembalian(rs.getInt("kembalian"));
        return t;
    }
}