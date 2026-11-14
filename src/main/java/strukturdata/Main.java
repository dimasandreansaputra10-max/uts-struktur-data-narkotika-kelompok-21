package strukturdata;

import strukturdata.io.CsvExporter;
import strukturdata.io.CsvImporter;
import strukturdata.model.AuditEntry;
import strukturdata.model.JenisCount;
import strukturdata.model.PutusanNarkotika;
import strukturdata.service.AuditService;
import strukturdata.service.FilterAggregationService;
import strukturdata.service.PutusanService;
import strukturdata.service.PutusanSortService;
import strukturdata.struktur.SearchResult;
import strukturdata.struktur.SinglyLinkedList;
import strukturdata.struktur.Stack;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Titik masuk utama aplikasi konsol Sistem Putusan Narkotika PN Jakarta Selatan.
 * Memuat menu interaktif: Impor, Tampil, Tambah, Hapus, Cari, Filter, Agregasi, Urutan, Audit, Ekspor, Keluar.
 * UI konsol rapi dengan tabel teks, penanganan input angka yang aman, dan tanpa crash.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final PutusanService putusanService = new PutusanService();
    private static final AuditService auditService = new AuditService();
    private static final FilterAggregationService filterService = new FilterAggregationService();
    private static final PutusanSortService sortService = new PutusanSortService();

    public static void main(String[] args) {
        // Otomatis coba impor data default jika ada
        cobaImporOtomatisDefault();

        boolean running = true;
        while (running) {
            tampilkanMenuUtama();
            int pilihan = bacaPilihanAngka(0, 10);
            System.out.println();

            switch (pilihan) {
                case 1 -> menuImporCsv();
                case 2 -> menuTampilkanData();
                case 3 -> menuTambahData();
                case 4 -> menuHapusData();
                case 5 -> menuCariData();
                case 6 -> menuFilterGabungan();
                case 7 -> menuAgregasiStatistik();
                case 8 -> menuUrutkanData();
                case 9 -> menuRiwayatAudit();
                case 10 -> menuEksporCsv();
                case 0 -> {
                    System.out.println("Terima kasih. Program selesai.");
                    running = false;
                }
                default -> System.out.println("Pilihan tidak valid.");
            }
            if (running) {
                System.out.println();
                System.out.print("Tekan Enter untuk kembali ke menu...");
                scanner.nextLine();
            }
        }
    }

    private static void cobaImporOtomatisDefault() {
        String[] defaultPaths = {"data/putusan.csv", "data/putusan-50.csv"};
        for (String p : defaultPaths) {
            File f = new File(p);
            if (f.exists()) {
                CsvImporter.ImportResult res = CsvImporter.importCsv(f.getAbsolutePath());
                if (res.getDataList().size() > 0) {
                    for (int i = 0; i < res.getDataList().size(); i++) {
                        putusanService.tambahDiAkhir(res.getDataList().get(i));
                    }
                    auditService.catat("AUTO_IMPOR", "Memuat otomatis " + res.getDataList().size() + " perkara dari " + p);
                    break;
                }
            }
        }
    }

    private static void tampilkanMenuUtama() {
        System.out.println("================================================================================");
        System.out.println("        SISTEM INFORMASI PUTUSAN NARKOTIKA PN JAKARTA SELATAN (UTS)            ");
        System.out.println("================================================================================");
        System.out.println(" Jumlah Perkara Tersimpan: " + putusanService.getJumlahPerkara() + " | Riwayat Audit: " + auditService.getJumlahAudit() + " aksi");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. Impor Data dari Berkas CSV");
        System.out.println(" 2. Tampilkan Daftar Putusan");
        System.out.println(" 3. Tambah Perkara Baru");
        System.out.println(" 4. Hapus Perkara");
        System.out.println(" 5. Cari Perkara (Berdasarkan Case ID / Nomor Perkara)");
        System.out.println(" 6. Filter Gabungan Multi-Kriteria");
        System.out.println(" 7. Agregasi Statistik & Distribusi Narkotika");
        System.out.println(" 8. Urutkan Perkara Berdasarkan Vonis (Sorting)");
        System.out.println(" 9. Tampilkan Riwayat Audit (Stack LIFO)");
        System.out.println(" 10. Ekspor Data ke Berkas CSV");
        System.out.println(" 0. Keluar");
        System.out.println("================================================================================");
        System.out.print("Pilih menu [0-10]: ");
    }

    // =========================================================================
    // Handler Menu-menu
    // =========================================================================

    private static void menuImporCsv() {
        System.out.println("--- IMPOR DATA DARI CSV ---");
        System.out.print("Masukkan path berkas CSV (default: data/putusan.csv): ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            path = "data/putusan.csv";
        }

        File f = new File(path);
        if (!f.exists()) {
            System.out.println("[ERROR] Berkas tidak ditemukan di path: " + f.getAbsolutePath());
            return;
        }

        CsvImporter.ImportResult res = CsvImporter.importCsv(f.getAbsolutePath());
        int sukses = 0;
        for (int i = 0; i < res.getDataList().size(); i++) {
            try {
                putusanService.tambahDiAkhir(res.getDataList().get(i));
                sukses++;
            } catch (Exception e) {
                res.getErrorList().addLast("Ditolak service: " + e.getMessage());
            }
        }

        auditService.catat("IMPOR_CSV", "Impor dari " + path + ": " + sukses + " berhasil, " + res.getErrorList().size() + " ditolak");
        System.out.println("[INFO] Berhasil mengimpor " + sukses + " perkara.");
        if (res.getErrorList().size() > 0) {
            System.out.println("[PERINGATAN] Terdapat " + res.getErrorList().size() + " baris ditolak:");
            for (int i = 0; i < res.getErrorList().size(); i++) {
                System.out.println("  - " + res.getErrorList().get(i));
            }
        }
    }

    private static void menuTampilkanData() {
        System.out.println("--- DAFTAR PUTUSAN PERKARA NARKOTIKA ---");
        tampilkanTabelPutusan(putusanService.getListPutusan());
    }

    private static void menuTambahData() {
        System.out.println("--- TAMBAH PERKARA BARU ---");
        System.out.print("Case ID (mis. PUTUSAN-099): ");
        String caseId = scanner.nextLine().trim();
        if (caseId.isEmpty()) {
            System.out.println("[ERROR] Case ID wajib diisi.");
            return;
        }

        System.out.print("Nomor Perkara: ");
        String noPerkara = scanner.nextLine().trim();
        if (noPerkara.isEmpty()) {
            System.out.println("[ERROR] Nomor perkara wajib diisi.");
            return;
        }

        System.out.print("Pengadilan (default: PN Jakarta Selatan): ");
        String pengadilan = scanner.nextLine().trim();
        if (pengadilan.isEmpty()) pengadilan = "PN Jakarta Selatan";

        System.out.print("Tanggal Putusan (YYYY-MM-DD): ");
        String tglStr = scanner.nextLine().trim();
        LocalDate tanggal = null;
        if (!tglStr.isEmpty()) {
            try {
                tanggal = LocalDate.parse(tglStr);
            } catch (DateTimeParseException e) {
                System.out.println("[ERROR] Format tanggal harus YYYY-MM-DD.");
                return;
            }
        }

        System.out.print("Kode Terdakwa (mis. T-099): ");
        String terdakwa = scanner.nextLine().trim();

        System.out.print("Jenis Narkotika (pisahkan titik-koma jika jamak): ");
        String jenis = scanner.nextLine().trim();

        System.out.print("Berat Barang Bukti (gram, tekan Enter jika kosong): ");
        String beratStr = scanner.nextLine().trim();
        Double berat = beratStr.isEmpty() ? null : Double.parseDouble(beratStr);

        System.out.print("Pasal Dakwaan: ");
        String pasalDakwaan = scanner.nextLine().trim();

        System.out.print("Pasal Terbukti: ");
        String pasalTerbukti = scanner.nextLine().trim();

        System.out.print("Tuntutan Penjara (bulan, tekan Enter jika pidana mati/kosong): ");
        String tuntutanStr = scanner.nextLine().trim();
        Integer tuntutan = tuntutanStr.isEmpty() ? null : Integer.parseInt(tuntutanStr);

        System.out.print("Vonis Penjara (bulan): ");
        int vonis = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Denda (Rupiah, tekan Enter jika kosong): ");
        String dendaStr = scanner.nextLine().trim();
        Long denda = dendaStr.isEmpty() ? null : Long.parseLong(dendaStr);

        System.out.print("Basis Berat (netto/brutto/tidak_jelas): ");
        String basis = scanner.nextLine().trim();
        if (basis.isEmpty()) basis = "netto";

        String tuntutanJenis = (tuntutan == null) ? "not_found" : "pidana_penjara_bulan";

        PutusanNarkotika p = new PutusanNarkotika(
                caseId, noPerkara, pengadilan, tanggal, terdakwa, jenis, berat,
                pasalDakwaan, pasalTerbukti, tuntutan, vonis, denda, "Barang bukti input",
                "Memberatkan", "Meringankan", "manual_input", "TUNGGAL",
                tuntutanJenis, "", "", basis
        );

        System.out.println("Posisi penambahan:");
        System.out.println("1. Di awal list");
        System.out.println("2. Di akhir list");
        System.out.println("3. Di posisi indeks tertentu");
        System.out.print("Pilih posisi [1-3]: ");
        int pos = bacaPilihanAngka(1, 3);

        try {
            if (pos == 1) {
                putusanService.tambahDiAwal(p);
            } else if (pos == 2) {
                putusanService.tambahDiAkhir(p);
            } else {
                System.out.print("Masukkan indeks (0.." + putusanService.getJumlahPerkara() + "): ");
                int idx = bacaPilihanAngka(0, putusanService.getJumlahPerkara());
                putusanService.sisipkanPada(idx, p);
            }
            auditService.catat("TAMBAH_DATA", "Menambahkan perkara baru " + caseId + " (" + noPerkara + ")");
            System.out.println("[INFO] Perkara " + caseId + " berhasil ditambahkan.");
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal menambahkan data: " + e.getMessage());
        }
    }

    private static void menuHapusData() {
        System.out.println("--- HAPUS PERKARA ---");
        System.out.println("1. Hapus berdasarkan Case ID");
        System.out.println("2. Hapus berdasarkan Indeks List");
        System.out.print("Pilih opsi [1-2]: ");
        int opsi = bacaPilihanAngka(1, 2);

        if (opsi == 1) {
            System.out.print("Masukkan Case ID yang akan dihapus: ");
            String caseId = scanner.nextLine().trim();
            PutusanNarkotika removed = putusanService.hapusBerdasarkanCaseId(caseId);
            if (removed != null) {
                auditService.catat("HAPUS_DATA", "Menghapus perkara " + caseId + " (" + removed.getNomorPerkara() + ")");
                System.out.println("[INFO] Perkara " + caseId + " berhasil dihapus.");
            } else {
                System.out.println("[PERINGATAN] Perkara dengan Case ID '" + caseId + "' tidak ditemukan.");
            }
        } else {
            if (putusanService.getJumlahPerkara() == 0) {
                System.out.println("[PERINGATAN] List kosong, tidak ada data untuk dihapus.");
                return;
            }
            System.out.print("Masukkan indeks (0.." + (putusanService.getJumlahPerkara() - 1) + "): ");
            int idx = bacaPilihanAngka(0, putusanService.getJumlahPerkara() - 1);
            PutusanNarkotika removed = putusanService.hapusPada(idx);
            auditService.catat("HAPUS_DATA", "Menghapus perkara indeks " + idx + ": " + removed.getCaseId());
            System.out.println("[INFO] Perkara pada indeks " + idx + " (" + removed.getCaseId() + ") berhasil dihapus.");
        }
    }

    private static void menuCariData() {
        System.out.println("--- PENCARIAN PERKARA ---");
        System.out.println("1. Cari berdasarkan Case ID");
        System.out.println("2. Cari berdasarkan Nomor Perkara");
        System.out.print("Pilih opsi [1-2]: ");
        int opsi = bacaPilihanAngka(1, 2);

        if (opsi == 1) {
            System.out.print("Masukkan Case ID: ");
            String id = scanner.nextLine().trim();
            SearchResult<PutusanNarkotika> res = putusanService.cariBerdasarkanCaseId(id);
            laporkanHasilCari(res, "Case ID = " + id);
        } else {
            System.out.print("Masukkan Nomor Perkara (lengkap / sebagian): ");
            String no = scanner.nextLine().trim();
            SearchResult<PutusanNarkotika> res = putusanService.cariBerdasarkanNomorPerkara(no);
            laporkanHasilCari(res, "Nomor Perkara = " + no);
        }
    }

    private static void laporkanHasilCari(SearchResult<PutusanNarkotika> res, String kueri) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Hasil Pencarian untuk: " + kueri);
        System.out.println("Jumlah langkah penelusuran linier: " + res.getLangkah() + " langkah");
        if (res.ditemukan()) {
            PutusanNarkotika p = res.getElemen();
            System.out.println("[STATUS] DITEMUKAN");
            System.out.println("  Case ID         : " + p.getCaseId());
            System.out.println("  Nomor Perkara   : " + p.getNomorPerkara());
            System.out.println("  Tanggal Putusan : " + p.getTanggalPutusan());
            System.out.println("  Jenis Narkotika : " + p.getJenisNarkotika());
            System.out.println("  Berat Barang    : " + (p.getBeratBarangBuktiGram() != null ? p.getBeratBarangBuktiGram() + " gram (" + p.getBeratBasis() + ")" : "null"));
            System.out.println("  Pasal Terbukti  : " + p.getPasalTerbukti());
            System.out.println("  Tuntutan / Vonis: " + (p.getTuntutanBulan() != null ? p.getTuntutanBulan() + " bln" : "null") + " / " + p.getVonisBulan() + " bln");
            System.out.println("  Denda Rupiah    : " + (p.getDendaRupiah() != null ? "Rp " + p.getDendaRupiah() : "null"));
            auditService.catat("CARI_DATA", "Pencarian [" + kueri + "] ditemukan dalam " + res.getLangkah() + " langkah");
        } else {
            System.out.println("[STATUS] TIDAK DITEMUKAN");
            auditService.catat("CARI_DATA", "Pencarian [" + kueri + "] tidak ditemukan (" + res.getLangkah() + " langkah)");
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void menuFilterGabungan() {
        System.out.println("--- FILTER GABUNGAN MULTI-KRITERIA ---");
        System.out.println("(Tekan Enter langsung pada kriteria yang tidak ingin dibatasi)");

        System.out.print("Jenis Narkotika (mis. Sabu / Ganja): ");
        String jenis = scanner.nextLine().trim();

        System.out.print("Batas Minimal Berat Gram (mis. 0.5): ");
        String minBStr = scanner.nextLine().trim();
        Double minB = minBStr.isEmpty() ? null : Double.parseDouble(minBStr);

        System.out.print("Batas Maksimal Berat Gram (mis. 5.0): ");
        String maxBStr = scanner.nextLine().trim();
        Double maxB = maxBStr.isEmpty() ? null : Double.parseDouble(maxBStr);

        System.out.print("Basis Berat (netto / brutto): ");
        String basis = scanner.nextLine().trim();

        System.out.print("Nomor Pasal Terbukti (mis. 114 / 112): ");
        String pasal = scanner.nextLine().trim();

        System.out.print("Batas Minimal Vonis Bulan: ");
        String minVStr = scanner.nextLine().trim();
        Integer minV = minVStr.isEmpty() ? null : Integer.parseInt(minVStr);

        System.out.print("Batas Maksimal Vonis Bulan: ");
        String maxVStr = scanner.nextLine().trim();
        Integer maxV = maxVStr.isEmpty() ? null : Integer.parseInt(maxVStr);

        FilterAggregationService.FilterKriteria k = new FilterAggregationService.FilterKriteria()
                .denganJenisNarkotika(jenis.isEmpty() ? null : jenis)
                .denganRentangBerat(minB, maxB, basis.isEmpty() ? null : basis)
                .denganPasalTerbukti(pasal.isEmpty() ? null : pasal)
                .denganRentangVonis(minV, maxV);

        SinglyLinkedList<PutusanNarkotika> hasil = filterService.filter(putusanService.getListPutusan(), k);

        auditService.catat("FILTER_DATA", "Filter kriteria gabungan menghasilkan " + hasil.size() + " perkara");
        System.out.println("\n[HASIL FILTER] Ditemukan " + hasil.size() + " perkara yang cocok:");
        tampilkanTabelPutusan(hasil);
    }

    private static void menuAgregasiStatistik() {
        System.out.println("--- AGREGASI STATISTIK PUTUSAN ---");
        SinglyLinkedList<PutusanNarkotika> list = putusanService.getListPutusan();

        int totalPerkara = filterService.hitungJumlahPerkara(list);
        double rataVonis = filterService.hitungRataRataVonisBulan(list);
        long totalDenda = filterService.hitungTotalDendaRupiah(list);

        System.out.println("Total Jumlah Perkara    : " + totalPerkara + " perkara");
        System.out.printf("Rata-rata Vonis Penjara : %.2f bulan (%.1f tahun)\n", rataVonis, (rataVonis / 12.0));
        System.out.println("Total Denda Rupiah      : Rp " + String.format("%,d", totalDenda));

        System.out.println("\nDistribusi Jenis Narkotika (dipecah multi-value ';'):");
        SinglyLinkedList<JenisCount> dist = filterService.hitungDistribusiJenisNarkotika(list);
        for (int i = 0; i < dist.size(); i++) {
            JenisCount jc = dist.get(i);
            System.out.printf("  - %-20s : %d perkara\n", jc.getNama(), jc.getJumlah());
        }

        auditService.catat("AGREGASI_STATISTIK", "Kalkulasi statistik pada " + totalPerkara + " perkara");
    }

    private static void menuUrutkanData() {
        System.out.println("--- PENGURUTAN PERKARA BERDASARKAN VONIS ---");
        System.out.println("1. Urutkan Vonis Terkecil ke Terbesar (Ascending)");
        System.out.println("2. Urutkan Vonis Terbesar ke Terkecil (Descending)");
        System.out.print("Pilih opsi [1-2]: ");
        int opsi = bacaPilihanAngka(1, 2);

        SinglyLinkedList<PutusanNarkotika> hasil;
        if (opsi == 1) {
            hasil = sortService.urutkanBerdasarkanVonisAscending(putusanService.getListPutusan());
            auditService.catat("SORT_VONIS", "Pengurutan vonis ascending pada salinan list");
            System.out.println("\n[HASIL PENGURUTAN ASCENDING]");
        } else {
            hasil = sortService.urutkanBerdasarkanVonisDescending(putusanService.getListPutusan());
            auditService.catat("SORT_VONIS", "Pengurutan vonis descending pada salinan list");
            System.out.println("\n[HASIL PENGURUTAN DESCENDING]");
        }

        tampilkanTabelPutusan(hasil);
        System.out.println("[INFO] Pengurutan dilakukan pada salinan data. Urutan list utama tetap utuh.");
    }

    private static void menuRiwayatAudit() {
        System.out.println("--- RIWAYAT AUDIT OPERASI PENGGUNA (STACK LIFO) ---");
        Stack<AuditEntry> stack = auditService.getStackAudit();
        if (stack.isEmpty()) {
            System.out.println("(Riwayat audit masih kosong)");
            return;
        }

        System.out.println("Urutan dari aksi paling baru ke paling lama:");
        for (int i = 0; i < stack.size(); i++) {
            AuditEntry entry = stack.get(i); // indeks 0 adalah top (paling baru)
            System.out.println((i + 1) + ". " + entry);
        }
    }

    private static void menuEksporCsv() {
        System.out.println("--- EKSPOR DATA KE BERKAS CSV ---");
        System.out.print("Masukkan path target berkas (default: data/ekspor-putusan.csv): ");
        String target = scanner.nextLine().trim();
        if (target.isEmpty()) {
            target = "data/ekspor-putusan.csv";
        }

        try {
            CsvExporter.exportCsv(putusanService.getListPutusan(), target);
            auditService.catat("EKSPOR_CSV", "Mengekspor " + putusanService.getJumlahPerkara() + " perkara ke " + target);
            System.out.println("[INFO] Berhasil mengekspor " + putusanService.getJumlahPerkara() + " perkara ke: " + target);
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal mengekspor file: " + e.getMessage());
        }
    }

    // =========================================================================
    // Helper Tampilan Tabel & Pembacaan Input
    // =========================================================================

    private static void tampilkanTabelPutusan(SinglyLinkedList<PutusanNarkotika> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("(Tidak ada data untuk ditampilkan)");
            return;
        }

        System.out.println("+---------------+----------------------------+-----------------+------------+-------------+");
        System.out.println("| Case ID       | Nomor Perkara              | Narkotika       | Berat (g)  | Vonis (bln) |");
        System.out.println("+---------------+----------------------------+-----------------+------------+-------------+");

        for (int i = 0; i < list.size(); i++) {
            PutusanNarkotika p = list.get(i);
            String id = truncate(p.getCaseId(), 13);
            String no = truncate(p.getNomorPerkara(), 26);
            String narko = truncate(p.getJenisNarkotika(), 15);
            String berat = (p.getBeratBarangBuktiGram() != null)
                    ? String.format("%.2f", p.getBeratBarangBuktiGram())
                    : "-";
            String vonis = (p.getVonisBulan() != null) ? p.getVonisBulan().toString() : "-";

            System.out.printf("| %-13s | %-26s | %-15s | %10s | %11s |\n", id, no, narko, berat, vonis);
        }
        System.out.println("+---------------+----------------------------+-----------------+------------+-------------+");
        System.out.println("Total: " + list.size() + " baris data.");
    }

    private static String truncate(String text, int max) {
        if (text == null) return "-";
        if (text.length() <= max) return text;
        return text.substring(0, max - 2) + "..";
    }

    /**
     * Membaca pilihan angka dalam rentang yang diberikan.
     * Seluruh input dibaca dengan {@code scanner.nextLine()} sehingga tidak ada
     * konsumsi garis baru yang tersisa yang dapat menyebabkan pesan error
     * "Input harus berupa angka" muncul setelah menekan Enter.
     * Jika input bukan angka atau berada di luar rentang, pengguna akan
     * diminta mengulang hingga memasukkan nilai yang sah.
     */
    private static int bacaAngka(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println("Input di luar rentang [" + min + "-" + max + "]. Ulangi.");
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka [" + min + "-" + max + "]. Ulangi.");
            }
        }
    }

    // Legacy method kept for compatibility; redirects to the new helper.
    private static int bacaPilihanAngka(int min, int max) {
        return bacaAngka("", min, max);
    }
}
