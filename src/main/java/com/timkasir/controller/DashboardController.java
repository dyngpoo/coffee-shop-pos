package com.timkasir.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label labelWelcome;

    @FXML
    private Label labelRole;

    public void setUserInfo(String namaKaryawan, String role) {
        labelWelcome.setText("Selamat datang, " + namaKaryawan + "!");
        labelRole.setText("Role: " + role);
    }
}