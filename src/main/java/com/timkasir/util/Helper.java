package com.timkasir.util;

import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.Locale;

public class Helper {

    private static final NumberFormat IDR = NumberFormat.getCurrencyInstance(
            new Locale("id", "ID"));

    /** Format angka ke Rupiah, contoh: Rp22.000 */
    public static String formatRupiah(long amount) {
        return IDR.format(amount).replace(",00", "");
    }

    /** MD5 hash untuk password */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 error", e);
        }
    }

    /** Generate nomor transaksi: TRX-YYYYMMDD-XXXXX */
    public static String generateNoTransaksi() {
        String date = java.time.LocalDate.now()
                .toString().replace("-", "");
        int rand = (int) (Math.random() * 90000) + 10000;
        return "TRX-" + date + "-" + rand;
    }
}