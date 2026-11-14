package strukturdata.validator;

import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Validator baris CSV dan model data PutusanNarkotika.
 * Memeriksa 21 kolom, keunikan caseId, format tanggal, angka non-negatif,
 * konsistensi tuntutanJenis dengan tuntutanBulan, serta mengembalikan pesan kesalahan.
 */
public class PutusanValidator {

    public static class ValidationResult {
        private final boolean valid;
        private final String pesanKesalahan;
        private final PutusanNarkotika putusan;

        public ValidationResult(boolean valid, String pesanKesalahan, PutusanNarkotika putusan) {
            this.valid = valid;
            this.pesanKesalahan = pesanKesalahan;
            this.putusan = putusan;
        }

        public boolean isValid() { return valid; }
        public String getPesanKesalahan() { return pesanKesalahan; }
        public PutusanNarkotika getPutusan() { return putusan; }
    }

    /**
     * Memvalidasi list token kolom dari satu baris CSV dan mengecek apakah caseId sudah ada.
     *
     * @param tokens list token string per kolom (harus 21 kolom)
     * @param existingCaseIds list caseId yang sudah terdaftar sebelumnya
     * @return ValidationResult berisi status validitas, pesan kesalahan, dan objek jika valid
     */
    public static ValidationResult validasi(SinglyLinkedList<String> tokens, SinglyLinkedList<String> existingCaseIds) {
        if (tokens == null || tokens.size() != 21) {
            int actual = (tokens == null) ? 0 : tokens.size();
            return new ValidationResult(false, "Jumlah kolom tidak valid: diharapkan 21 kolom, ditemukan " + actual, null);
        }

        // Ambil ke-21 kolom
        String caseId = tokens.get(0);
        String nomorPerkara = tokens.get(1);
        String pengadilan = tokens.get(2);
        String tanggalStr = tokens.get(3);
        String terdakwaKode = tokens.get(4);
        String jenisNarkotika = tokens.get(5);
        String beratStr = tokens.get(6);
        String pasalDakwaan = tokens.get(7);
        String pasalTerbukti = tokens.get(8);
        String tuntutanBulanStr = tokens.get(9);
        String vonisBulanStr = tokens.get(10);
        String dendaRupiahStr = tokens.get(11);
        String barangBukti = tokens.get(12);
        String keadaanMemberatkan = tokens.get(13);
        String keadaanMeringankan = tokens.get(14);
        String sourcePages = tokens.get(15);
        String bentukDakwaan = tokens.get(16);
        String tuntutanJenis = tokens.get(17);
        String dendaCatatan = tokens.get(18);
        String notes = tokens.get(19);
        String beratBasis = tokens.get(20);

        // 1. caseId wajib diisi dan tidak boleh duplikat
        if (caseId == null || caseId.isEmpty()) {
            return new ValidationResult(false, "caseId wajib diisi", null);
        }
        if (existingCaseIds != null && existingCaseIds.contains(caseId)) {
            return new ValidationResult(false, "caseId duplikat: " + caseId, null);
        }

        // 2. nomorPerkara wajib diisi
        if (nomorPerkara == null || nomorPerkara.isEmpty()) {
            return new ValidationResult(false, "nomorPerkara wajib diisi untuk " + caseId, null);
        }

        // 3. tanggalPutusan harus format valid YYYY-MM-DD
        LocalDate tanggalPutusan = null;
        if (tanggalStr != null && !tanggalStr.isEmpty()) {
            try {
                tanggalPutusan = LocalDate.parse(tanggalStr);
            } catch (DateTimeParseException e) {
                return new ValidationResult(false, "Format tanggalPutusan tidak valid (" + tanggalStr + ") untuk " + caseId, null);
            }
        }

        // 4. beratBarangBuktiGram (nullable, jika ada harus >= 0)
        Double beratBarangBuktiGram = null;
        if (beratStr != null && !beratStr.isEmpty()) {
            try {
                beratBarangBuktiGram = Double.parseDouble(beratStr);
                if (beratBarangBuktiGram < 0) {
                    return new ValidationResult(false, "beratBarangBuktiGram tidak boleh negatif (" + beratBarangBuktiGram + ") untuk " + caseId, null);
                }
            } catch (NumberFormatException e) {
                return new ValidationResult(false, "beratBarangBuktiGram bukan angka valid (" + beratStr + ") untuk " + caseId, null);
            }
        }

        // 5. tuntutanBulan (nullable, jika ada harus >= 0)
        Integer tuntutanBulan = null;
        if (tuntutanBulanStr != null && !tuntutanBulanStr.isEmpty()) {
            try {
                // Di dataset terdapat angka dengan pecahan .0 (mis. 156.0)
                double val = Double.parseDouble(tuntutanBulanStr);
                if (val < 0) {
                    return new ValidationResult(false, "tuntutanBulan tidak boleh negatif (" + tuntutanBulanStr + ") untuk " + caseId, null);
                }
                tuntutanBulan = (int) Math.round(val);
            } catch (NumberFormatException e) {
                return new ValidationResult(false, "tuntutanBulan bukan angka valid (" + tuntutanBulanStr + ") untuk " + caseId, null);
            }
        }

        // 6. Konsistensi tuntutanJenis dengan tuntutanBulan
        if ("pidana_mati".equalsIgnoreCase(tuntutanJenis)) {
            if (tuntutanBulan != null) {
                return new ValidationResult(false, "tuntutanJenis pidana_mati tidak boleh memiliki nilai tuntutanBulan", null);
            }
        } else if ("not_found".equalsIgnoreCase(tuntutanJenis)) {
            if (tuntutanBulan != null) {
                return new ValidationResult(false, "tuntutanJenis not_found tidak boleh memiliki nilai tuntutanBulan", null);
            }
        }

        // 7. vonisBulan (wajib angka, >= 0)
        Integer vonisBulan = null;
        if (vonisBulanStr == null || vonisBulanStr.isEmpty()) {
            return new ValidationResult(false, "vonisBulan wajib diisi untuk " + caseId, null);
        }
        try {
            double vVal = Double.parseDouble(vonisBulanStr);
            if (vVal < 0) {
                return new ValidationResult(false, "vonisBulan tidak boleh negatif (" + vonisBulanStr + ") untuk " + caseId, null);
            }
            vonisBulan = (int) Math.round(vVal);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "vonisBulan bukan angka valid (" + vonisBulanStr + ") untuk " + caseId, null);
        }

        // 8. dendaRupiah (nullable, jika ada harus >= 0)
        Long dendaRupiah = null;
        if (dendaRupiahStr != null && !dendaRupiahStr.isEmpty()) {
            try {
                // Tangani format desimal atau notasi eksponensial jika ada
                double dVal = Double.parseDouble(dendaRupiahStr);
                if (dVal < 0) {
                    return new ValidationResult(false, "dendaRupiah tidak boleh negatif (" + dendaRupiahStr + ") untuk " + caseId, null);
                }
                dendaRupiah = Math.round(dVal);
            } catch (NumberFormatException e) {
                return new ValidationResult(false, "dendaRupiah bukan angka valid (" + dendaRupiahStr + ") untuk " + caseId, null);
            }
        }

        PutusanNarkotika putusan = new PutusanNarkotika(
                caseId, nomorPerkara, pengadilan, tanggalPutusan, terdakwaKode, jenisNarkotika,
                beratBarangBuktiGram, pasalDakwaan, pasalTerbukti, tuntutanBulan, vonisBulan,
                dendaRupiah, barangBukti, keadaanMemberatkan, keadaanMeringankan, sourcePages,
                bentukDakwaan, tuntutanJenis, dendaCatatan, notes, beratBasis
        );

        return new ValidationResult(true, null, putusan);
    }
}

