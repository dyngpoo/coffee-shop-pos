package com.timkasir.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/coffee_shop_pos";

    private static final String USER = "root";

    private static final String PASSWORD = "";

    public static Connection connect() {

        try {

            Connection conn =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Koneksi database berhasil!");

            return conn;

        } catch (Exception e) {

            System.out.println("Koneksi gagal!");
            e.printStackTrace();

            return null;
        }
    }
}