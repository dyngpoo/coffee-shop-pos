package com.timkasir.controller;

import com.timkasir.database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

        String username = usernameField.getText();
        String password = passwordField.getText();

        try {

            Connection conn = DBConnection.connect();

            String sql =
                    "SELECT * FROM karyawan WHERE username=? AND password=?";

            PreparedStatement pst =
                    conn.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                Alert alert =
                        new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Login Berhasil");
                alert.setHeaderText(null);

                alert.setContentText(
                        "Selamat datang " +
                        rs.getString("nama_karyawan")
                );

                alert.showAndWait();

            } else {

                Alert alert =
                        new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Login Gagal");
                alert.setHeaderText(null);

                alert.setContentText(
                        "Username atau password salah!"
                );

                alert.showAndWait();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}