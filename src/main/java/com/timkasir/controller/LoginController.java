package com.timkasir.controller;

import com.timkasir.app.MainApp;
import com.timkasir.dao.UserDAO;
import com.timkasir.model.User;
import com.timkasir.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField     tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private Label         lblError;
    @FXML private Button        btnLogin;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        lblError.setVisible(false);
        // Enter key pada password langsung login
        pfPassword.setOnAction(e -> doLogin());
        btnLogin.setOnAction(e -> doLogin());
    }
    public void doLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password tidak boleh kosong.");
            return;
        }

        try {
            User user = userDAO.login(username, password);
            if (user == null) {
                showError("Username atau password salah.");
                pfPassword.clear();
                tfUsername.requestFocus();
                return;
            }

            Session.setUser(user);
            lblError.setVisible(false);

            // Arahkan ke dashboard sesuai role
            switch (user.getRole()) {
               case "manager" -> MainApp.loadScene("/com/timkasir/ManagerDashboard.fxml", "Manager Dashboard");
case "lead"    -> MainApp.loadScene("/com/timkasir/LeadDashboard.fxml",    "Lead Dashboard");
case "kasir"   -> MainApp.loadScene("/com/timkasir/KasirDashboard.fxml",   "Kasir Dashboard");
            }
        } catch (Exception e) {
            showError("Koneksi database gagal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }
}