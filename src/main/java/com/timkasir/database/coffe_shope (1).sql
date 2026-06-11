-- ============================================================
-- Database: coffeeshop
-- Versi: 2.0 (JavaFX Edition)
-- Perbaikan: Tabel users dengan role (manager/lead/kasir),
--            tabel produk terpisah dari barang,
--            tabel transaksi & detail transaksi dirapikan
-- ============================================================

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+07:00";

CREATE DATABASE IF NOT EXISTS `coffeeshop`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `coffeeshop`;

-- ============================================================
-- 1. USERS (login dengan role)
-- ============================================================
CREATE TABLE `users` (
  `id_user`      INT AUTO_INCREMENT PRIMARY KEY,
  `username`     VARCHAR(50)  NOT NULL UNIQUE,
  `password`     VARCHAR(255) NOT NULL,          -- simpan hash (MD5/BCrypt)
  `nama_lengkap` VARCHAR(100) NOT NULL,
  `no_hp`        VARCHAR(20)  NOT NULL,
  `alamat`       TEXT         NOT NULL,
  `role`         ENUM('manager','lead','kasir') NOT NULL DEFAULT 'kasir',
  `status`       ENUM('aktif','nonaktif')       NOT NULL DEFAULT 'aktif',
  `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Akun default (password: 12345 → MD5: 827ccb0eea8a706c4c34a16891f84e7b)
INSERT INTO `users` (`username`,`password`,`nama_lengkap`,`no_hp`,`alamat`,`role`) VALUES
('manager', '827ccb0eea8a706c4c34a16891f84e7b', 'Reza Maulana',   '081233445567', 'Jakarta',    'manager'),
('lead',    '827ccb0eea8a706c4c34a16891f84e7b', 'Nazwa Aulia',    '081123445677', 'Bogor',      'lead'),
('kasir',   '827ccb0eea8a706c4c34a16891f84e7b', 'Rahmi Putri',    '081344567789', 'Tangerang',  'kasir');

-- ============================================================
-- 2. KATEGORI PRODUK
-- ============================================================
CREATE TABLE `kategori` (
  `id_kategori`   INT AUTO_INCREMENT PRIMARY KEY,
  `nama_kategori` VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `kategori` (`nama_kategori`) VALUES
('Coffee'), ('Non Coffee'), ('Tea'), ('Snack');

-- ============================================================
-- 3. PRODUK (menu kafe)
-- ============================================================
CREATE TABLE `produk` (
  `id_produk`   INT AUTO_INCREMENT PRIMARY KEY,
  `nama_produk` VARCHAR(100) NOT NULL,
  `harga`       INT          NOT NULL,
  `id_kategori` INT          NOT NULL,
  `foto`        VARCHAR(200) DEFAULT NULL,
  `stok`        INT          NOT NULL DEFAULT 100,
  `status`      ENUM('aktif','nonaktif') NOT NULL DEFAULT 'aktif',
  FOREIGN KEY (`id_kategori`) REFERENCES `kategori`(`id_kategori`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `produk` (`nama_produk`,`harga`,`id_kategori`,`foto`,`stok`) VALUES
-- Coffee
('Latte',          22000, 1, 'latte.jpg',          100),
('Americano',      25000, 1, 'americano.jpg',       100),
('Butterscotch',   22000, 1, 'bts.jpg',             100),
('Cream Coffee',   22000, 1, 'caramel.jpg',         100),
-- Non Coffee
('Choco Cream',    28000, 2, 'chococream.jpg',      100),
('Choco Latte',    25000, 2, 'chocolatte.jpg',      100),
('Vanila Taro',    18000, 2, 'vanila.jpg',          100),
('Choco Matcha',   22000, 2, 'matchachoco.jpg',     100),
-- Tea
('Lemon Tea',      15000, 3, 'lemontea.jpg',        100),
('Green Tea',      15000, 3, 'greentea.jpg',        100),
('Melati Tea',     15000, 3, 'melati.jpg',          100),
('Thai Tea',       18000, 3, 'thaitea.jpg',         100),
-- Snack
('Brownie',        30000, 4, 'brownie.jpg',         100),
('Cheese Cake',    30000, 4, 'chesee.jpg',          100),
('Caramel Puding', 20000, 4, 'puding.jpg',          100),
('Blueberry Cheese',25000,4, 'blueberry.jpg',       100);

-- ============================================================
-- 4. PELANGGAN
-- ============================================================
CREATE TABLE `pelanggan` (
  `id_pelanggan`   INT AUTO_INCREMENT PRIMARY KEY,
  `nama_pelanggan` VARCHAR(100) NOT NULL,
  `no_hp`          VARCHAR(20)  DEFAULT NULL,
  `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 5. SUPPLIER
-- ============================================================
CREATE TABLE `supplier` (
  `id_supplier`   INT AUTO_INCREMENT PRIMARY KEY,
  `nama_supplier` VARCHAR(100) NOT NULL,
  `no_telepon`    VARCHAR(20)  NOT NULL,
  `alamat`        TEXT         NOT NULL,
  `email`         VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 6. BAHAN BAKU
-- ============================================================
CREATE TABLE `bahan` (
  `id_bahan`    INT AUTO_INCREMENT PRIMARY KEY,
  `nama_bahan`  VARCHAR(100) NOT NULL,
  `harga_bahan` INT          NOT NULL,
  `stok_bahan`  INT          NOT NULL DEFAULT 0,
  `id_supplier` INT          NOT NULL,
  FOREIGN KEY (`id_supplier`) REFERENCES `supplier`(`id_supplier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. TRANSAKSI (header)
-- ============================================================
CREATE TABLE `transaksi` (
  `id_transaksi`      INT AUTO_INCREMENT PRIMARY KEY,
  `no_transaksi`      VARCHAR(30)  NOT NULL UNIQUE,
  `tgl_transaksi`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_pelanggan`      INT          DEFAULT NULL,
  `id_kasir`          INT          NOT NULL,
  `subtotal`          INT          NOT NULL DEFAULT 0,
  `diskon`            INT          NOT NULL DEFAULT 0,
  `pajak`             INT          NOT NULL DEFAULT 0,
  `total`             INT          NOT NULL DEFAULT 0,
  `metode_pembayaran` ENUM('tunai','qris','debit') NOT NULL DEFAULT 'tunai',
  `jumlah_dibayar`    INT          NOT NULL DEFAULT 0,
  `kembalian`         INT          NOT NULL DEFAULT 0,
  FOREIGN KEY (`id_pelanggan`) REFERENCES `pelanggan`(`id_pelanggan`),
  FOREIGN KEY (`id_kasir`)     REFERENCES `users`(`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 8. DETAIL TRANSAKSI (item per transaksi)
-- ============================================================
CREATE TABLE `detail_transaksi` (
  `id_detail`    INT AUTO_INCREMENT PRIMARY KEY,
  `id_transaksi` INT NOT NULL,
  `id_produk`    INT NOT NULL,
  `nama_produk`  VARCHAR(100) NOT NULL,  -- snapshot agar aman jika produk dihapus
  `harga_satuan` INT NOT NULL,
  `qty`          INT NOT NULL DEFAULT 1,
  `subtotal`     INT NOT NULL,
  FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi`(`id_transaksi`),
  FOREIGN KEY (`id_produk`)    REFERENCES `produk`(`id_produk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 9. ABSENSI
-- ============================================================
CREATE TABLE `absensi` (
  `id_absensi` INT AUTO_INCREMENT PRIMARY KEY,
  `id_user`    INT  NOT NULL,
  `tgl_masuk`  DATE NOT NULL,
  `jam_masuk`  TIME NOT NULL,
  `jam_keluar` TIME DEFAULT NULL,
  FOREIGN KEY (`id_user`) REFERENCES `users`(`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 10. PENGADAAN BAHAN
-- ============================================================
CREATE TABLE `pengadaan_bahan` (
  `id_pengadaan`  INT AUTO_INCREMENT PRIMARY KEY,
  `id_bahan`      INT  NOT NULL,
  `id_supplier`   INT  NOT NULL,
  `jumlah`        INT  NOT NULL DEFAULT 1,
  `tgl_pengadaan` DATE NOT NULL,
  FOREIGN KEY (`id_bahan`)    REFERENCES `bahan`(`id_bahan`),
  FOREIGN KEY (`id_supplier`) REFERENCES `supplier`(`id_supplier`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

COMMIT;