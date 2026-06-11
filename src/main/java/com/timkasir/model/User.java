package com.timkasir.model;

public class User {
    private int idUser;
    private String username;
    private String password;
    private String namaLengkap;
    private String noHp;
    private String alamat;
    private String role;   // manager | lead | kasir
    private String status; // aktif | nonaktif

    public User() {}

    public User(int idUser, String username, String namaLengkap, String noHp,
                String alamat, String role, String status) {
        this.idUser = idUser;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.noHp = noHp;
        this.alamat = alamat;
        this.role = role;
        this.status = status;
    }

    // Getters & Setters
    public int getIdUser()              { return idUser; }
    public void setIdUser(int idUser)   { this.idUser = idUser; }

    public String getUsername()                 { return username; }
    public void setUsername(String username)    { this.username = username; }

    public String getPassword()                 { return password; }
    public void setPassword(String password)    { this.password = password; }

    public String getNamaLengkap()                      { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap)      { this.namaLengkap = namaLengkap; }

    public String getNoHp()                 { return noHp; }
    public void setNoHp(String noHp)        { this.noHp = noHp; }

    public String getAlamat()               { return alamat; }
    public void setAlamat(String alamat)    { this.alamat = alamat; }

    public String getRole()                 { return role; }
    public void setRole(String role)        { this.role = role; }

    public String getStatus()               { return status; }
    public void setStatus(String status)    { this.status = status; }

    @Override
    public String toString() {
        return namaLengkap + " (" + role + ")";
    }
}