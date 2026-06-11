package com.timkasir.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("CoffeeShop POS System");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        loadScene("/com/timkasir/Login.fxml", "Login");
        primaryStage.show();
    }

    public static void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(MainApp.class.getResource(fxmlPath));

            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(
                MainApp.class.getResource("/com/timkasir/style.css")
                             .toExternalForm()
            );

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}