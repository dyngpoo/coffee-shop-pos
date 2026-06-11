package com.timkasir.controller;

import com.timkasir.app.MainApp;
import com.timkasir.dao.ProdukDAO;
import com.timkasir.dao.TransaksiDAO;
import com.timkasir.model.Produk;
import com.timkasir.model.Transaksi;
import com.timkasir.util.Helper;
import com.timkasir.util.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class LeadDashboardController {

    @FXML private Label lblNama;
    @FXML private Label lblRole;

    // ── Tab Stok ─────────────────────────────────────────
    @FXML private TableView<Produk>             tblStok;
    @FXML private TableColumn<Produk,Integer>   colIdProduk;
    @FXML private TableColumn<Produk,String>    colNamaProduk;
    @FXML private TableColumn<Produk,String>    colKategori;
    @FXML private TableColumn<Produk,Integer>   colHargaProduk;
    @FXML private TableColumn<Produk,Integer>   colStok;
    @FXML private TableColumn<Produk,String>    colStatus;
    @FXML private TableColumn<Produk,Void>      colAksiStok;

    // ── Form Produk ───────────────────────────────────────
    @FXML private TextField   tfNamaProduk;
    @FXML private TextField   tfHarga;
    @FXML private TextField   tfStok;
    @FXML private ComboBox<String> cbKategoriProduk;
    @FXML private TextField   tfFoto;
    @FXML private ComboBox<String> cbStatusProduk;
    @FXML private Button      btnSimpanProduk;
    @FXML private Button      btnBatalProduk;

    // ── Tab Penjualan ─────────────────────────────────────
    @FXML private Label lblTotalPenjualan;
    @FXML private Label lblJumlahTransaksi;
    @FXML private Label lblRataRata;

    @FXML private TableView<Transaksi>              tblTransaksi;
    @FXML private TableColumn<Transaksi,String>     colNoTransaksi;
    @FXML private TableColumn<Transaksi,String>     colTglTransaksi;
    @FXML private TableColumn<Transaksi,String>     colKasir;
    @FXML private TableColumn<Transaksi,Integer>    colTotal;
    @FXML private TableColumn<Transaksi,String>     colMetode;

    private final ProdukDAO    produkDAO    = new ProdukDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    private final ObservableList<Produk>    stokList  = FXCollections.observableArrayList();
    private final ObservableList<Transaksi> transList = FXCollections.observableArrayList();

    private Produk produkDiedit = null;  // null = mode tambah

    // ── Kategori ID mapping ───────────────────────────────
    private static final String[] KATEGORI_NAMA = {"Coffee","Non Coffee","Tea","Snack"};
    private static final int[]    KATEGORI_ID   = {1, 2, 3, 4};

    @FXML
    public void initialize() {
        lblNama.setText(Session.getNama());
        lblRole.setText(Session.getRole().toUpperCase());

        setupTableStok();
        setupTableTransaksi();
        setupForm();
        loadStok();
        loadTransaksi();
    }

    // ─────────────────────────────────────────────────────
    //  STOK
    // ─────────────────────────────────────────────────────
    private void setupTableStok() {
        colIdProduk.setCellValueFactory(new PropertyValueFactory<>("idProduk"));
        colNamaProduk.setCellValueFactory(new PropertyValueFactory<>("namaProduk"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("namaKategori"));
        colHargaProduk.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colHargaProduk.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : Helper.formatRupiah(v));
            }
        });

        // Kolom aksi: Edit | Hapus
        colAksiStok.setCellFactory(col -> new TableCell<>() {
            final Button btnEdit  = new Button("Edit");
            final Button btnHapus = new Button("Hapus");
            {
                btnEdit.getStyleClass().add("btn-primary-sm");
                btnHapus.getStyleClass().add("btn-danger-sm");
                btnEdit.setOnAction(e -> editProduk(getTableView().getItems().get(getIndex())));
                btnHapus.setOnAction(e -> hapusProduk(getTableView().getItems().get(getIndex())));
            }
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); } else {
                    javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(4, btnEdit, btnHapus);
                    setGraphic(box);
                }
            }
        });

        tblStok.setItems(stokList);
    }

    private void setupForm() {
        cbKategoriProduk.setItems(FXCollections.observableArrayList(KATEGORI_NAMA));
        cbKategoriProduk.setValue("Coffee");
        cbStatusProduk.setItems(FXCollections.observableArrayList("aktif","nonaktif"));
        cbStatusProduk.setValue("aktif");
    }

    private void loadStok() {
        try {
            stokList.setAll(produkDAO.getAllForManagement());
        } catch (SQLException e) {
            showAlert("Error", "Gagal memuat stok: " + e.getMessage());
        }
    }

    @FXML
    private void simpanProduk() {
        String nama   = tfNamaProduk.getText().trim();
        String hargaS = tfHarga.getText().trim();
        String stokS  = tfStok.getText().trim();

        if (nama.isEmpty() || hargaS.isEmpty() || stokS.isEmpty()) {
            showAlert("Peringatan", "Nama, harga, dan stok wajib diisi.");
            return;
        }
        try {
            int harga = Integer.parseInt(hargaS);
            int stok  = Integer.parseInt(stokS);
            int idKat = KATEGORI_ID[cbKategoriProduk.getSelectionModel().getSelectedIndex()];

            Produk p = produkDiedit != null ? produkDiedit : new Produk();
            p.setNamaProduk(nama);
            p.setHarga(harga);
            p.setStok(stok);
            p.setIdKategori(idKat);
            p.setFoto(tfFoto.getText().trim());
            p.setStatus(cbStatusProduk.getValue());

            boolean ok = produkDiedit == null ? produkDAO.tambah(p) : produkDAO.update(p);
            if (ok) {
                showAlert("Berhasil", produkDiedit == null ? "Produk ditambahkan!" : "Produk diperbarui!");
                batalEdit();
                loadStok();
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Harga dan stok harus berupa angka.");
        } catch (SQLException e) {
            showAlert("Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    private void editProduk(Produk p) {
        produkDiedit = p;
        tfNamaProduk.setText(p.getNamaProduk());
        tfHarga.setText(String.valueOf(p.getHarga()));
        tfStok.setText(String.valueOf(p.getStok()));
        tfFoto.setText(p.getFoto() != null ? p.getFoto() : "");
        cbKategoriProduk.setValue(p.getNamaKategori());
        cbStatusProduk.setValue(p.getStatus());
        btnSimpanProduk.setText("Update");
    }

    private void hapusProduk(Produk p) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Nonaktifkan produk \"" + p.getNamaProduk() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    produkDAO.hapus(p.getIdProduk());
                    loadStok();
                } catch (SQLException e) {
                    showAlert("Error", "Gagal menghapus: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void batalEdit() {
        produkDiedit = null;
        tfNamaProduk.clear(); tfHarga.clear(); tfStok.clear(); tfFoto.clear();
        cbKategoriProduk.setValue("Coffee");
        cbStatusProduk.setValue("aktif");
        btnSimpanProduk.setText("Simpan");
    }

    // ─────────────────────────────────────────────────────
    //  PENJUALAN
    // ─────────────────────────────────────────────────────
    private void setupTableTransaksi() {
        colNoTransaksi.setCellValueFactory(new PropertyValueFactory<>("noTransaksi"));
        colTglTransaksi.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                Transaksi t = getTableView().getItems().get(getIndex());
                setText(t.getTglTransaksi() != null
                        ? t.getTglTransaksi().toString().replace("T"," ").substring(0,16)
                        : "-");
            }
        });
        colKasir.setCellValueFactory(new PropertyValueFactory<>("namaKasir"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : Helper.formatRupiah(v));
            }
        });
        colMetode.setCellValueFactory(new PropertyValueFactory<>("metodePembayaran"));
        tblTransaksi.setItems(transList);
    }

    @FXML
    private void loadTransaksi() {
        try {
            List<Transaksi> list = transaksiDAO.getAll();
            transList.setAll(list);

            long totalPenjualan = list.stream().mapToLong(Transaksi::getTotal).sum();
            long jumlah         = list.size();
            long rata           = jumlah > 0 ? totalPenjualan / jumlah : 0;

            lblTotalPenjualan.setText(Helper.formatRupiah(totalPenjualan));
            lblJumlahTransaksi.setText(String.valueOf(jumlah));
            lblRataRata.setText(Helper.formatRupiah(rata));
        } catch (SQLException e) {
            showAlert("Error", "Gagal memuat transaksi: " + e.getMessage());
        }
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