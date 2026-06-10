package com.timkasir.controller;

import com.timkasir.database.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validasi input kosong
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Peringatan", "Username dan password tidak boleh kosong!");
            return;
        }

        try {
            Connection conn = DBConnection.connect();

            // Ambil data berdasarkan username saja dulu
            String sql = "SELECT * FROM karyawan WHERE username = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String hashedInput   = hashMD5(password);
                String storedHash    = rs.getString("password");

                if (hashedInput.equals(storedHash)) {
                    String namaKaryawan = rs.getString("nama_karyawan");
                    String role         = rs.getString("role");

                    navigateToDashboard(namaKaryawan, role);
                } else {
                    showAlert(Alert.AlertType.ERROR,
                            "Login Gagal", "Username atau password salah!");
                }
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Login Gagal", "Username atau password salah!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Error", "Koneksi database gagal: " + e.getMessage());
        }
    }

    private void navigateToDashboard(String namaKaryawan, String role) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass()
                            .getResource("/com/timkasir/Dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            // Kirim data ke DashboardController
            DashboardController dashCtrl = loader.getController();
            dashCtrl.setUserInfo(namaKaryawan, role);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Coffee Shop POS - Dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Error", "Gagal membuka dashboard.");
        }
    }

    private String hashMD5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}