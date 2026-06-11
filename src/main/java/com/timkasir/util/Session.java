package com.timkasir.util;

import com.timkasir.model.User;

/**
 * Menyimpan data user yang sedang login (session).
 */
public class Session {

    private static User currentUser = null;

    private Session() {}

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }

    public static String getNama() {
        return currentUser != null ? currentUser.getNamaLengkap() : "";
    }

    public static int getIdUser() {
        return currentUser != null ? currentUser.getIdUser() : -1;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}