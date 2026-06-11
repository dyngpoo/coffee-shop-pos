package com.timkasir.model;

public class Produk {
    private int idProduk;
    private String namaProduk;
    private int harga;
    private int idKategori;
    private String namaKategori;
    private String foto;
    private int stok;
    private String status;

    public Produk() {}

    public Produk(int idProduk, String namaProduk, int harga,
                  int idKategori, String namaKategori, String foto, int stok, String status) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.harga = harga;
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
        this.foto = foto;
        this.stok = stok;
        this.status = status;
    }

    public int getIdProduk()                { return idProduk; }
    public void setIdProduk(int idProduk)   { this.idProduk = idProduk; }

    public String getNamaProduk()                   { return namaProduk; }
    public void setNamaProduk(String namaProduk)    { this.namaProduk = namaProduk; }

    public int getHarga()               { return harga; }
    public void setHarga(int harga)     { this.harga = harga; }

    public int getIdKategori()                  { return idKategori; }
    public void setIdKategori(int idKategori)   { this.idKategori = idKategori; }

    public String getNamaKategori()                     { return namaKategori; }
    public void setNamaKategori(String namaKategori)    { this.namaKategori = namaKategori; }

    public String getFoto()                 { return foto; }
    public void setFoto(String foto)        { this.foto = foto; }

    public int getStok()                { return stok; }
    public void setStok(int stok)       { this.stok = stok; }

    public String getStatus()               { return status; }
    public void setStatus(String status)    { this.status = status; }

    @Override
    public String toString() { return namaProduk; }
}