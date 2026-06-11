package com.timkasir.model;

import javafx.beans.property.*;

/**
 * Item dalam keranjang belanja (POS kasir).
 * Menggunakan JavaFX Property agar bisa di-bind ke TableView.
 */
public class CartItem {

    private final IntegerProperty idProduk   = new SimpleIntegerProperty();
    private final StringProperty  namaProduk = new SimpleStringProperty();
    private final IntegerProperty harga      = new SimpleIntegerProperty();
    private final IntegerProperty qty        = new SimpleIntegerProperty();
    private final IntegerProperty subtotal   = new SimpleIntegerProperty();

    public CartItem() {}

    public CartItem(int idProduk, String namaProduk, int harga, int qty) {
        setIdProduk(idProduk);
        setNamaProduk(namaProduk);
        setHarga(harga);
        setQty(qty);
        hitungSubtotal();
    }

    public void hitungSubtotal() {
        setSubtotal(getHarga() * getQty());
    }

    // --- idProduk ---
    public int getIdProduk()                { return idProduk.get(); }
    public void setIdProduk(int v)          { idProduk.set(v); }
    public IntegerProperty idProdukProperty(){ return idProduk; }

    // --- namaProduk ---
    public String getNamaProduk()                   { return namaProduk.get(); }
    public void setNamaProduk(String v)             { namaProduk.set(v); }
    public StringProperty namaProdukProperty()      { return namaProduk; }

    // --- harga ---
    public int getHarga()                   { return harga.get(); }
    public void setHarga(int v)             { harga.set(v); }
    public IntegerProperty hargaProperty()  { return harga; }

    // --- qty ---
    public int getQty()                     { return qty.get(); }
    public void setQty(int v)               { qty.set(v); hitungSubtotal(); }
    public IntegerProperty qtyProperty()    { return qty; }

    // --- subtotal ---
    public int getSubtotal()                        { return subtotal.get(); }
    public void setSubtotal(int v)                  { subtotal.set(v); }
    public IntegerProperty subtotalProperty()       { return subtotal; }
}