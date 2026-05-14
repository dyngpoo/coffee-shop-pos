package com.timkasir.app;

import com.timkasir.database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // TEST KONEKSI DATABASE
        DBConnection.connect();

        // LOAD LOGIN FXML
        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        Main.class.getResource("/com/timkasir/Login.fxml")
                );

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Coffee Shop POS");

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {

        launch();
    }
}