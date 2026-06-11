package com.timkasir.controller;

import com.timkasir.app.MainApp;
import com.timkasir.dao.ProdukDAO;
import com.timkasir.dao.TransaksiDAO;
import com.timkasir.model.CartItem;
import com.timkasir.model.Produk;
import com.timkasir.model.Transaksi;
import com.timkasir.util.Helper;
import com.timkasir.util.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.List;

public class KasirDashboardController {

    // ── Sidebar info ──────────────────────────────────────
    @FXML private Label lblNamaKasir;
    @FXML private Label lblRole;

    // ── Produk panel ─────────────────────────────────────
    @FXML private TextField      tfCari;
    @FXML private ComboBox<String> cbKategori;
    @FXML private FlowPane       fpProduk;

    // ── Keranjang ─────────────────────────────────────────
    @FXML private TableView<CartItem>         tblKeranjang;
    @FXML private TableColumn<CartItem,String>  colNama;
    @FXML private TableColumn<CartItem,Integer> colHarga;
    @FXML private TableColumn<CartItem,Integer> colQty;
    @FXML private TableColumn<CartItem,Integer> colSubtotal;
    @FXML private TableColumn<CartItem,Void>    colAksi;

    // ── Hitung total ──────────────────────────────────────
    @FXML private Label         lblSubtotal;
    @FXML private ComboBox<String> cbDiskon;
    @FXML private TextField     tfNilaiDiskon;
    @FXML private Label         lblPajak;
    @FXML private Label         lblTotal;

    // ── Pembayaran ────────────────────────────────────────
    @FXML private ComboBox<String> cbMetode;
    @FXML private TextField        tfDibayar;
    @FXML private Label            lblKembalian;
    @FXML private Button           btnBayar;

    // ── Struk ─────────────────────────────────────────────
    @FXML private TextArea taStruk;

    // ── State ─────────────────────────────────────────────
    private final ProdukDAO    produkDAO    = new ProdukDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final ObservableList<CartItem> cart = FXCollections.observableArrayList();
    private List<Produk> semuaProduk;

    private int subtotal  = 0;
    private int pajak     = 0;
    private int diskon    = 0;
    private int total     = 0;

    // ── Pajak per item (Rp1.000) ──────────────────────────
    private static final int PAJAK_PER_ITEM = 1000;

    @FXML
    public void initialize() {
        lblNamaKasir.setText(Session.getNama());
        lblRole.setText(Session.getRole().toUpperCase());

        setupTable();
        setupKombo();
        loadProduk();

        tfCari.textProperty().addListener((o, ov, nv) -> filterProduk());
        cbKategori.valueProperty().addListener((o, ov, nv) -> filterProduk());
        cbDiskon.valueProperty().addListener((o, ov, nv) -> hitungTotal());
        tfNilaiDiskon.textProperty().addListener((o, ov, nv) -> hitungTotal());
        tfDibayar.textProperty().addListener((o, ov, nv) -> hitungKembalian());
    }

    // ─────────────────────────────────────────────────────
    //  SETUP
    // ─────────────────────────────────────────────────────
    private void setupTable() {
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaProduk"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Format kolom rupiah
        colHarga.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : Helper.formatRupiah(v));
            }
        });
        colSubtotal.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : Helper.formatRupiah(v));
            }
        });

        // Kolom Qty: tombol ─ qty +
        colQty.setCellFactory(col -> new TableCell<>() {
            final Button btnMin = new Button("−");
            final Button btnPls = new Button("+");
            final Label  lbl    = new Label();
            final HBox   box    = new HBox(4, btnMin, lbl, btnPls);
            {
                box.setStyle("-fx-alignment:center;");
                btnMin.getStyleClass().add("btn-qty");
                btnPls.getStyleClass().add("btn-qty");
                btnMin.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    if (item.getQty() > 1) { item.setQty(item.getQty()-1); refreshCart(); }
                    else { cart.remove(item); refreshCart(); }
                });
                btnPls.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    item.setQty(item.getQty()+1); refreshCart();
                });
            }
            protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); } else {
                    lbl.setText(String.valueOf(getTableView().getItems().get(getIndex()).getQty()));
                    setGraphic(box);
                }
            }
        });

        // Kolom Aksi: tombol hapus
        colAksi.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("✕");
            {
                btn.getStyleClass().add("btn-danger-sm");
                btn.setOnAction(e -> {
                    cart.remove(getTableView().getItems().get(getIndex()));
                    refreshCart();
                });
            }
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tblKeranjang.setItems(cart);
    }

    private void setupKombo() {
        cbKategori.setItems(FXCollections.observableArrayList(
                "Semua", "Coffee", "Non Coffee", "Tea", "Snack"));
        cbKategori.setValue("Semua");

        cbDiskon.setItems(FXCollections.observableArrayList(
                "Tidak Ada", "Persen (%)", "Nominal (Rp)"));
        cbDiskon.setValue("Tidak Ada");

        cbMetode.setItems(FXCollections.observableArrayList(
                "Tunai", "QRIS", "Debit"));
        cbMetode.setValue("Tunai");
    }

    // ─────────────────────────────────────────────────────
    //  PRODUK
    // ─────────────────────────────────────────────────────
    private void loadProduk() {
        try {
            semuaProduk = produkDAO.getAll();
            tampilProduk(semuaProduk);
        } catch (SQLException e) {
            showAlert("Error", "Gagal memuat produk: " + e.getMessage());
        }
    }

    private void filterProduk() {
        if (semuaProduk == null) return;
        String cari     = tfCari.getText().toLowerCase();
        String kategori = cbKategori.getValue();

        List<Produk> hasil = semuaProduk.stream()
            .filter(p -> (kategori.equals("Semua") || p.getNamaKategori().equalsIgnoreCase(kategori)))
            .filter(p -> p.getNamaProduk().toLowerCase().contains(cari))
            .toList();
        tampilProduk(hasil);
    }

    private void tampilProduk(List<Produk> list) {
        fpProduk.getChildren().clear();
        for (Produk p : list) {
            fpProduk.getChildren().add(buatKartuProduk(p));
        }
    }

    private VBox buatKartuProduk(Produk p) {
        VBox card = new VBox(6);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(160);
        card.setPadding(new Insets(10));

        Label nama  = new Label(p.getNamaProduk());
        nama.getStyleClass().add("product-name");
        nama.setWrapText(true);

        Label harga = new Label(Helper.formatRupiah(p.getHarga()));
        harga.getStyleClass().add("product-price");

        Label stok  = new Label("Stok: " + p.getStok());
        stok.getStyleClass().add("product-stok");

        Label kat   = new Label(p.getNamaKategori());
        kat.getStyleClass().add("product-kategori");

        Button btn  = new Button("+ Tambah");
        btn.getStyleClass().add("btn-add");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setDisable(p.getStok() <= 0);
        btn.setOnAction(e -> tambahKeKeranjang(p));

        card.getChildren().addAll(kat, nama, harga, stok, btn);
        return card;
    }

    // ─────────────────────────────────────────────────────
    //  KERANJANG
    // ─────────────────────────────────────────────────────
    private void tambahKeKeranjang(Produk p) {
        CartItem existing = cart.stream()
                .filter(i -> i.getIdProduk() == p.getIdProduk())
                .findFirst().orElse(null);
        if (existing != null) {
            existing.setQty(existing.getQty() + 1);
        } else {
            cart.add(new CartItem(p.getIdProduk(), p.getNamaProduk(), p.getHarga(), 1));
        }
        tblKeranjang.refresh();
        refreshCart();
    }

    private void refreshCart() {
        tblKeranjang.refresh();
        hitungTotal();
    }

    @FXML
    private void clearKeranjang() {
        cart.clear();
        hitungTotal();
    }

    // ─────────────────────────────────────────────────────
    //  HITUNG TOTAL
    // ─────────────────────────────────────────────────────
    private void hitungTotal() {
        subtotal = cart.stream().mapToInt(CartItem::getSubtotal).sum();
        int totalQty = cart.stream().mapToInt(CartItem::getQty).sum();
        pajak = totalQty * PAJAK_PER_ITEM;

        String jenisDiskon = cbKategori.getValue(); // reused variable below
        String jd = cbDiskon.getValue() != null ? cbDiskon.getValue() : "Tidak Ada";
        int nilaiDiskon = 0;
        try { nilaiDiskon = Integer.parseInt(tfNilaiDiskon.getText().trim()); } catch (Exception ignored) {}

        diskon = switch (jd) {
            case "Persen (%)"  -> subtotal * nilaiDiskon / 100;
            case "Nominal (Rp)"-> nilaiDiskon;
            default            -> 0;
        };

        total = Math.max(0, subtotal + pajak - diskon);

        lblSubtotal.setText(Helper.formatRupiah(subtotal));
        lblPajak.setText(Helper.formatRupiah(pajak));
        lblTotal.setText(Helper.formatRupiah(total));

        hitungKembalian();
    }

    private void hitungKembalian() {
        try {
            int dibayar = Integer.parseInt(tfDibayar.getText().trim());
            int kembalian = dibayar - total;
            if (kembalian < 0) {
                lblKembalian.setText("Uang Kurang");
                lblKembalian.setStyle("-fx-text-fill: #c0392b;");
            } else {
                lblKembalian.setText(Helper.formatRupiah(kembalian));
                lblKembalian.setStyle("-fx-text-fill: #27ae60;");
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("-");
            lblKembalian.setStyle("");
        }
    }

    // ─────────────────────────────────────────────────────
    //  PROSES BAYAR
    // ─────────────────────────────────────────────────────
    @FXML
    private void prosesBayar() {
        if (cart.isEmpty()) {
            showAlert("Peringatan", "Keranjang masih kosong!");
            return;
        }

        int dibayar;
        try {
            dibayar = Integer.parseInt(tfDibayar.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Peringatan", "Masukkan jumlah uang yang dibayar.");
            return;
        }

        if (dibayar < total) {
            showAlert("Peringatan", "Uang yang dibayar kurang dari total.");
            return;
        }

        Transaksi t = new Transaksi();
        t.setNoTransaksi(Helper.generateNoTransaksi());
        t.setIdKasir(Session.getIdUser());
        t.setSubtotal(subtotal);
        t.setDiskon(diskon);
        t.setPajak(pajak);
        t.setTotal(total);
        t.setMetodePembayaran(cbMetode.getValue().toLowerCase());
        t.setJumlahDibayar(dibayar);
        t.setKembalian(dibayar - total);

        try {
            int idTransaksi = transaksiDAO.simpan(t, cart);
            // Kurangi stok
            ProdukDAO pd = new ProdukDAO();
            for (CartItem item : cart) {
                pd.kurangiStok(item.getIdProduk(), item.getQty());
            }
            cetakStruk(t);
            showAlert("Berhasil", "Transaksi #" + t.getNoTransaksi() + " berhasil disimpan!\nKembalian: "
                    + Helper.formatRupiah(t.getKembalian()));
            // Reset
            cart.clear();
            tfDibayar.clear();
            tfNilaiDiskon.clear();
            cbDiskon.setValue("Tidak Ada");
            hitungTotal();
            loadProduk(); // refresh stok
        } catch (SQLException e) {
            showAlert("Error", "Gagal menyimpan transaksi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────
    //  STRUK
    // ─────────────────────────────────────────────────────
    private void cetakStruk(Transaksi t) {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================\n");
        sb.append("         COFFEESHOP POS SYSTEM       \n");
        sb.append("          Brew Your Moments          \n");
        sb.append("=====================================\n");
        sb.append("No. Transaksi : ").append(t.getNoTransaksi()).append("\n");
        sb.append("Kasir         : ").append(Session.getNama()).append("\n");
        sb.append("Metode        : ").append(t.getMetodePembayaran().toUpperCase()).append("\n");
        sb.append("-------------------------------------\n");
        for (CartItem item : cart) {
            sb.append(String.format("%-20s x%d\n", item.getNamaProduk(), item.getQty()));
            sb.append(String.format("  %s\n", Helper.formatRupiah(item.getSubtotal())));
        }
        sb.append("-------------------------------------\n");
        sb.append(String.format("Subtotal : %s\n", Helper.formatRupiah(subtotal)));
        if (diskon > 0)
            sb.append(String.format("Diskon   : -%s\n", Helper.formatRupiah(diskon)));
        sb.append(String.format("Pajak    : %s\n", Helper.formatRupiah(pajak)));
        sb.append(String.format("TOTAL    : %s\n", Helper.formatRupiah(total)));
        sb.append(String.format("Dibayar  : %s\n", Helper.formatRupiah(t.getJumlahDibayar())));
        sb.append(String.format("Kembalian: %s\n", Helper.formatRupiah(t.getKembalian())));
        sb.append("=====================================\n");
        sb.append("       Terima kasih sudah mampir!    \n");
        sb.append("=====================================\n");
        taStruk.setText(sb.toString());
    }

    // ─────────────────────────────────────────────────────
    //  NAVIGASI
    // ─────────────────────────────────────────────────────
    @FXML
    private void logout() {
        Session.logout();
        MainApp.loadScene("/com/coffeeshop/view/Login.fxml", "Login");
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}