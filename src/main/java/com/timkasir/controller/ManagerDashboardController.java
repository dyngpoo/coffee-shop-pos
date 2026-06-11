package com.timkasir.controller;

import com.timkasir.app.MainApp;
import com.timkasir.dao.TransaksiDAO;
import com.timkasir.dao.UserDAO;
import com.timkasir.model.Transaksi;
import com.timkasir.model.User;
import com.timkasir.util.Helper;
import com.timkasir.util.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class ManagerDashboardController {

    @FXML private Label lblNama;
    @FXML private Label lblRole;

    // ── Dashboard Summary ─────────────────────────────────
    @FXML private Label lblTotalHariIni;
    @FXML private Label lblTransaksiHariIni;
    @FXML private Label lblTotalKaryawan;

    // ── Tab Laporan ───────────────────────────────────────
    @FXML private TableView<Transaksi>              tblLaporan;
    @FXML private TableColumn<Transaksi,String>     colNo;
    @FXML private TableColumn<Transaksi,String>     colTgl;
    @FXML private TableColumn<Transaksi,String>     colKasir;
    @FXML private TableColumn<Transaksi,String>     colPelanggan;
    @FXML private TableColumn<Transaksi,Integer>    colTotal;
    @FXML private TableColumn<Transaksi,String>     colMetode;

    // ── Tab Karyawan ──────────────────────────────────────
    @FXML private TableView<User>                   tblKaryawan;
    @FXML private TableColumn<User,Integer>         colIdUser;
    @FXML private TableColumn<User,String>          colUsername;
    @FXML private TableColumn<User,String>          colNamaKaryawan;
    @FXML private TableColumn<User,String>          colRoleUser;
    @FXML private TableColumn<User,String>          colStatusUser;
    @FXML private TableColumn<User,Void>            colAksiUser;

    // ── Form Karyawan ─────────────────────────────────────
    @FXML private TextField         tfUsername;
    @FXML private PasswordField     pfPassword;
    @FXML private TextField         tfNamaLengkap;
    @FXML private TextField         tfNoHp;
    @FXML private TextField         tfAlamat;
    @FXML private ComboBox<String>  cbRole;
    @FXML private ComboBox<String>  cbStatusUser;
    @FXML private Button            btnSimpanUser;

    private final UserDAO      userDAO      = new UserDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    private final ObservableList<Transaksi> laporanList   = FXCollections.observableArrayList();
    private final ObservableList<User>      karyawanList  = FXCollections.observableArrayList();

    private User userDiedit = null;

    @FXML
    public void initialize() {
        lblNama.setText(Session.getNama());
        lblRole.setText(Session.getRole().toUpperCase());

        setupTableLaporan();
        setupTableKaryawan();
        setupFormKaryawan();
        loadSummary();
        loadLaporan();
        loadKaryawan();
    }

    // ─────────────────────────────────────────────────────
    //  SUMMARY
    // ─────────────────────────────────────────────────────
    private void loadSummary() {
        try {
            lblTotalHariIni.setText(Helper.formatRupiah(transaksiDAO.getTotalHariIni()));
            lblTransaksiHariIni.setText(String.valueOf(transaksiDAO.getJumlahTransaksiHariIni()));
            lblTotalKaryawan.setText(String.valueOf(userDAO.getAllUsers().size()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────
    //  LAPORAN
    // ─────────────────────────────────────────────────────
    private void setupTableLaporan() {
        colNo.setCellValueFactory(new PropertyValueFactory<>("noTransaksi"));
        colTgl.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                Transaksi t = getTableView().getItems().get(getIndex());
                setText(t.getTglTransaksi() != null
                        ? t.getTglTransaksi().toString().replace("T"," ").substring(0,16) : "-");
            }
        });
        colKasir.setCellValueFactory(new PropertyValueFactory<>("namaKasir"));
        colPelanggan.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : Helper.formatRupiah(v));
            }
        });
        colMetode.setCellValueFactory(new PropertyValueFactory<>("metodePembayaran"));
        tblLaporan.setItems(laporanList);
    }

    @FXML
    private void loadLaporan() {
        try {
            laporanList.setAll(transaksiDAO.getAll());
        } catch (SQLException e) {
            showAlert("Error", "Gagal memuat laporan: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────
    //  KARYAWAN
    // ─────────────────────────────────────────────────────
    private void setupTableKaryawan() {
        colIdUser.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNamaKaryawan.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
        colRoleUser.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatusUser.setCellValueFactory(new PropertyValueFactory<>("status"));

        colAksiUser.setCellFactory(col -> new TableCell<>() {
            final Button btnEdit  = new Button("Edit");
            final Button btnHapus = new Button("Hapus");
            {
                btnEdit.getStyleClass().add("btn-primary-sm");
                btnHapus.getStyleClass().add("btn-danger-sm");
                btnEdit.setOnAction(e -> editUser(getTableView().getItems().get(getIndex())));
                btnHapus.setOnAction(e -> hapusUser(getTableView().getItems().get(getIndex())));
            }
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(4, btnEdit, btnHapus);
                setGraphic(box);
            }
        });

        tblKaryawan.setItems(karyawanList);
    }

    private void setupFormKaryawan() {
        cbRole.setItems(FXCollections.observableArrayList("manager","lead","kasir"));
        cbRole.setValue("kasir");
        cbStatusUser.setItems(FXCollections.observableArrayList("aktif","nonaktif"));
        cbStatusUser.setValue("aktif");
    }

    @FXML
    private void loadKaryawan() {
        try {
            karyawanList.setAll(userDAO.getAllUsers());
        } catch (SQLException e) {
            showAlert("Error", "Gagal memuat karyawan: " + e.getMessage());
        }
    }

    @FXML
    private void simpanUser() {
        String uname = tfUsername.getText().trim();
        String nama  = tfNamaLengkap.getText().trim();
        String hp    = tfNoHp.getText().trim();
        String adr   = tfAlamat.getText().trim();

        if (uname.isEmpty() || nama.isEmpty()) {
            showAlert("Peringatan", "Username dan nama lengkap wajib diisi.");
            return;
        }
        if (userDiedit == null && pfPassword.getText().isEmpty()) {
            showAlert("Peringatan", "Password wajib diisi untuk user baru.");
            return;
        }

        try {
            User u = userDiedit != null ? userDiedit : new User();
            u.setUsername(uname);
            if (!pfPassword.getText().isEmpty()) u.setPassword(pfPassword.getText());
            u.setNamaLengkap(nama);
            u.setNoHp(hp);
            u.setAlamat(adr);
            u.setRole(cbRole.getValue());
            u.setStatus(cbStatusUser.getValue());

            boolean ok = userDiedit == null ? userDAO.tambah(u) : userDAO.update(u);
            if (ok) {
                showAlert("Berhasil", userDiedit == null ? "Karyawan ditambahkan!" : "Karyawan diperbarui!");
                batalEditUser();
                loadKaryawan();
                loadSummary();
            }
        } catch (SQLException e) {
            showAlert("Error", "Gagal menyimpan: " + e.getMessage());
        }
    }

    private void editUser(User u) {
        userDiedit = u;
        tfUsername.setText(u.getUsername());
        pfPassword.clear();
        tfNamaLengkap.setText(u.getNamaLengkap());
        tfNoHp.setText(u.getNoHp());
        tfAlamat.setText(u.getAlamat());
        cbRole.setValue(u.getRole());
        cbStatusUser.setValue(u.getStatus());
        btnSimpanUser.setText("Update");
    }

    private void hapusUser(User u) {
        if (u.getIdUser() == Session.getIdUser()) {
            showAlert("Peringatan", "Tidak bisa menghapus akun yang sedang login.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus karyawan \"" + u.getNamaLengkap() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    userDAO.hapus(u.getIdUser());
                    loadKaryawan();
                    loadSummary();
                } catch (SQLException e) {
                    showAlert("Error", "Gagal menghapus: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void batalEditUser() {
        userDiedit = null;
        tfUsername.clear(); pfPassword.clear();
        tfNamaLengkap.clear(); tfNoHp.clear(); tfAlamat.clear();
        cbRole.setValue("kasir");
        cbStatusUser.setValue("aktif");
        btnSimpanUser.setText("Simpan");
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