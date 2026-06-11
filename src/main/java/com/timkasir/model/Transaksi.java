package com.timkasir.model;

import java.time.LocalDateTime;

public class Transaksi {
    private int idTransaksi;
    private String noTransaksi;
    private LocalDateTime tglTransaksi;
    private Integer idPelanggan;
    private String namaPelanggan;
    private int idKasir;
    private String namaKasir;
    private int subtotal;
    private int diskon;
    private int pajak;
    private int total;
    private String metodePembayaran;
    private int jumlahDibayar;
    private int kembalian;

    public Transaksi() {}

    // --- Getters & Setters ---
    public int getIdTransaksi()                     { return idTransaksi; }
    public void setIdTransaksi(int v)               { this.idTransaksi = v; }

    public String getNoTransaksi()                  { return noTransaksi; }
    public void setNoTransaksi(String v)            { this.noTransaksi = v; }

    public LocalDateTime getTglTransaksi()          { return tglTransaksi; }
    public void setTglTransaksi(LocalDateTime v)    { this.tglTransaksi = v; }

    public Integer getIdPelanggan()                 { return idPelanggan; }
    public void setIdPelanggan(Integer v)           { this.idPelanggan = v; }

    public String getNamaPelanggan()                { return namaPelanggan; }
    public void setNamaPelanggan(String v)          { this.namaPelanggan = v; }

    public int getIdKasir()                         { return idKasir; }
    public void setIdKasir(int v)                   { this.idKasir = v; }

    public String getNamaKasir()                    { return namaKasir; }
    public void setNamaKasir(String v)              { this.namaKasir = v; }

    public int getSubtotal()                        { return subtotal; }
    public void setSubtotal(int v)                  { this.subtotal = v; }

    public int getDiskon()                          { return diskon; }
    public void setDiskon(int v)                    { this.diskon = v; }

    public int getPajak()                           { return pajak; }
    public void setPajak(int v)                     { this.pajak = v; }

    public int getTotal()                           { return total; }
    public void setTotal(int v)                     { this.total = v; }

    public String getMetodePembayaran()             { return metodePembayaran; }
    public void setMetodePembayaran(String v)       { this.metodePembayaran = v; }

    public int getJumlahDibayar()                   { return jumlahDibayar; }
    public void setJumlahDibayar(int v)             { this.jumlahDibayar = v; }

    public int getKembalian()                       { return kembalian; }
    public void setKembalian(int v)                 { this.kembalian = v; }
}