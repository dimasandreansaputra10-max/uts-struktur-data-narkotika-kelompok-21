package strukturdata.service;

import strukturdata.model.JenisCount;
import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;

/**
 * Service untuk filter gabungan dan kalkulasi statistik agregasi.
 * Tidak memakai koleksi java.util yang dilarang — hanya menggunakan
 * SinglyLinkedList<T> atau Stack<T> buatan sendiri.
 */
public class FilterAggregationService {

    /**
     * Parameter filter gabungan multi-kriteria.
     * Nilai null menandakan kriteria tersebut diabaikan / tidak dibatasi.
     */
    public static class FilterKriteria {
        private String jenisNarkotika; // substring/contains pada jenis narkotika
        private Double minBeratGram;    // batas bawah berat barang bukti (inklusif)
        private Double maxBeratGram;    // batas atas berat barang bukti (inklusif)
        private String beratBasis;      // "netto", "brutto", atau null untuk semua
        private String pasalTerbukti;   // substring/contains pada pasal terbukti
        private Integer minVonisBulan;  // batas bawah vonis bulan (inklusif)
        private Integer maxVonisBulan;  // batas atas vonis bulan (inklusif)

        public FilterKriteria() {}

        public FilterKriteria denganJenisNarkotika(String jenis) {
            this.jenisNarkotika = jenis;
            return this;
        }

        public FilterKriteria denganRentangBerat(Double min, Double max, String basis) {
            this.minBeratGram = min;
            this.maxBeratGram = max;
            this.beratBasis = basis;
            return this;
        }

        public FilterKriteria denganPasalTerbukti(String pasal) {
            this.pasalTerbukti = pasal;
            return this;
        }

        public FilterKriteria denganRentangVonis(Integer min, Integer max) {
            this.minVonisBulan = min;
            this.maxVonisBulan = max;
            return this;
        }

        public String getJenisNarkotika() { return jenisNarkotika; }
        public Double getMinBeratGram() { return minBeratGram; }
        public Double getMaxBeratGram() { return maxBeratGram; }
        public String getBeratBasis() { return beratBasis; }
        public String getPasalTerbukti() { return pasalTerbukti; }
        public Integer getMinVonisBulan() { return minVonisBulan; }
        public Integer getMaxVonisBulan() { return maxVonisBulan; }
    }

    /**
     * Menyaring list putusan berdasarkan kombinasi multi-kriteria.
     * Hasil disimpan dalam instance SinglyLinkedList<PutusanNarkotika> baru,
     * sehingga list sumber tidak berubah.
     */
    public SinglyLinkedList<PutusanNarkotika> filter(SinglyLinkedList<PutusanNarkotika> source, FilterKriteria kriteria) {
        SinglyLinkedList<PutusanNarkotika> hasil = new SinglyLinkedList<>();
        if (source == null || kriteria == null) {
            return hasil;
        }

        for (int i = 0; i < source.size(); i++) {
            PutusanNarkotika p = source.get(i);
            if (apakahCocok(p, kriteria)) {
                hasil.addLast(p);
            }
        }

        hasil.validateInvariant();
        return hasil;
    }

    private boolean apakahCocok(PutusanNarkotika p, FilterKriteria k) {
        if (p == null) return false;

        // 1. Filter jenis narkotika (substring, case-insensitive)
        if (k.getJenisNarkotika() != null && !k.getJenisNarkotika().trim().isEmpty()) {
            if (p.getJenisNarkotika() == null ||
                    !p.getJenisNarkotika().toLowerCase().contains(k.getJenisNarkotika().toLowerCase().trim())) {
                return false;
            }
        }

        // 2. Filter berat barang bukti dan beratBasis
        // Aturan: Melewati nilai null (jika ada batasan rentang berat, record dengan berat null tidak lolos)
        boolean adaFilterBerat = (k.getMinBeratGram() != null || k.getMaxBeratGram() != null || k.getBeratBasis() != null);
        if (adaFilterBerat) {
            if (p.getBeratBarangBuktiGram() == null) {
                return false;
            }
            if (k.getMinBeratGram() != null && p.getBeratBarangBuktiGram() < k.getMinBeratGram()) {
                return false;
            }
            if (k.getMaxBeratGram() != null && p.getBeratBarangBuktiGram() > k.getMaxBeratGram()) {
                return false;
            }
            if (k.getBeratBasis() != null && !k.getBeratBasis().trim().isEmpty()) {
                if (p.getBeratBasis() == null || !p.getBeratBasis().equalsIgnoreCase(k.getBeratBasis().trim())) {
                    return false;
                }
            }
        }

        // 3. Filter pasal terbukti (substring, case-insensitive)
        if (k.getPasalTerbukti() != null && !k.getPasalTerbukti().trim().isEmpty()) {
            if (p.getPasalTerbukti() == null ||
                    !p.getPasalTerbukti().toLowerCase().contains(k.getPasalTerbukti().toLowerCase().trim())) {
                return false;
            }
        }

        // 4. Filter rentang vonis bulan
        if (k.getMinVonisBulan() != null) {
            if (p.getVonisBulan() == null || p.getVonisBulan() < k.getMinVonisBulan()) {
                return false;
            }
        }
        if (k.getMaxVonisBulan() != null) {
            if (p.getVonisBulan() == null || p.getVonisBulan() > k.getMaxVonisBulan()) {
                return false;
            }
        }

        return true;
    }

    // =========================================================================
    // Agregasi Statistik
    // =========================================================================

    /**
     * Menghitung total jumlah perkara pada list.
     */
    public int hitungJumlahPerkara(SinglyLinkedList<PutusanNarkotika> list) {
        return (list == null) ? 0 : list.size();
    }

    /**
     * Menghitung rata-rata vonis bulan (mengabaikan record yang memiliki vonis null).
     * Jika tidak ada record valid, mengembalikan 0.0.
     */
    public double hitungRataRataVonisBulan(SinglyLinkedList<PutusanNarkotika> list) {
        if (list == null || list.isEmpty()) return 0.0;

        double total = 0.0;
        int count = 0;

        for (int i = 0; i < list.size(); i++) {
            PutusanNarkotika p = list.get(i);
            if (p != null && p.getVonisBulan() != null) {
                total += p.getVonisBulan();
                count++;
            }
        }

        return (count == 0) ? 0.0 : (total / count);
    }

    /**
     * Menghitung total denda dalam rupiah (mengabaikan nilai null).
     */
    public long hitungTotalDendaRupiah(SinglyLinkedList<PutusanNarkotika> list) {
        if (list == null || list.isEmpty()) return 0L;

        long total = 0L;
        for (int i = 0; i < list.size(); i++) {
            PutusanNarkotika p = list.get(i);
            if (p != null && p.getDendaRupiah() != null) {
                total += p.getDendaRupiah();
            }
        }
        return total;
    }

    /**
     * Menghitung distribusi frekuensi kemunculan jenis narkotika.
     * Nilai jenis narkotika dipecah per tanda titik-koma ';' (mis. "Sabu; Ekstasi" dihitung Sabu +1, Ekstasi +1).
     * Menggunakan SinglyLinkedList<JenisCount> tanpa HashMap.
     */
    public SinglyLinkedList<JenisCount> hitungDistribusiJenisNarkotika(SinglyLinkedList<PutusanNarkotika> list) {
        SinglyLinkedList<JenisCount> distribusi = new SinglyLinkedList<>();
        if (list == null || list.isEmpty()) return distribusi;

        for (int i = 0; i < list.size(); i++) {
            PutusanNarkotika p = list.get(i);
            if (p == null || p.getJenisNarkotika() == null) continue;

            String raw = p.getJenisNarkotika();
            // Split manual per karakter ';' tanpa regex atau koleksi bawaan
            SinglyLinkedList<String> items = splitSemicolon(raw);

            for (int j = 0; j < items.size(); j++) {
                String namaJenis = items.get(j).trim();
                if (namaJenis.isEmpty()) continue;

                // Cari apakah nama jenis sudah ada di list distribusi
                JenisCount target = null;
                for (int k = 0; k < distribusi.size(); k++) {
                    JenisCount jc = distribusi.get(k);
                    if (jc.getNama().equalsIgnoreCase(namaJenis)) {
                        target = jc;
                        break;
                    }
                }

                if (target != null) {
                    target.increment();
                } else {
                    distribusi.addLast(new JenisCount(namaJenis, 1));
                }
            }
        }

        distribusi.validateInvariant();
        return distribusi;
    }

    private SinglyLinkedList<String> splitSemicolon(String str) {
        SinglyLinkedList<String> tokens = new SinglyLinkedList<>();
        if (str == null) return tokens;

        StringBuilder sb = new StringBuilder();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == ';') {
                tokens.addLast(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.addLast(sb.toString().trim());
        return tokens;
    }
}
