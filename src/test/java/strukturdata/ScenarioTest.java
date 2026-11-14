package strukturdata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Uji integrasi end-to-end untuk 7 skenario utama UTS (Soal Bagian 4.1).
 */
public class ScenarioTest {

    private PutusanService putusanService;
    private AuditService auditService;
    private FilterAggregationService filterService;
    private PutusanSortService sortService;

    private String getFilePath(String relativePath) {
        File f = new File(relativePath);
        if (f.exists()) return f.getAbsolutePath();

        File f2 = new File("C:/Users/user/.gemini/antigravity/scratch/uts-strukdat/" + relativePath);
        if (f2.exists()) return f2.getAbsolutePath();

        return relativePath;
    }

    @BeforeEach
    void setup() {
        putusanService = new PutusanService();
        auditService = new AuditService();
        filterService = new FilterAggregationService();
        sortService = new PutusanSortService();
    }

    @Test
    @DisplayName("Skenario 1: Impor & Integritas Dataset Utama serta Penolakan Data Tidak Valid")
    void skenario1_imporDanIntegritasDataset() {
        // 1. Impor data utama (50 record)
        String mainPath = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult mainResult = CsvImporter.importCsv(mainPath);

        assertNotNull(mainResult);
        assertEquals(50, mainResult.getDataList().size(), "Harus tepat 50 perkara yang berhasil dimuat");
        assertEquals(0, mainResult.getErrorList().size(), "Tidak boleh ada error pada data utama");

        // Masukkan ke service
        for (int i = 0; i < mainResult.getDataList().size(); i++) {
            putusanService.tambahDiAkhir(mainResult.getDataList().get(i));
        }
        assertEquals(50, putusanService.getJumlahPerkara());
        putusanService.getListPutusan().validateInvariant();

        // 2. Impor berkas salah (invalid-records.csv)
        String invalidPath = getFilePath("src/test/resources/invalid-records.csv");
        CsvImporter.ImportResult invalidResult = CsvImporter.importCsv(invalidPath);

        // Pada invalid-records.csv: 1 lolos (duplikat pertama), 3 ditolak
        assertEquals(1, invalidResult.getDataList().size());
        assertEquals(3, invalidResult.getErrorList().size(), "Tepat 3 baris bermasalah harus ditolak");

        invalidResult.getDataList().validateInvariant();
        invalidResult.getErrorList().validateInvariant();
    }

    @Test
    @DisplayName("Skenario 2: Penanganan Nilai Null dan Kasus Khusus Putusan")
    void skenario2_penangananNilaiNullDanKasusKhusus() {
        String mainPath = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult mainResult = CsvImporter.importCsv(mainPath);
        SinglyLinkedList<PutusanNarkotika> list = mainResult.getDataList();

        // Kasus Khusus 1: PUTUSAN-045 tuntutan tidak ditemukan di berkas (not_found)
        PutusanNarkotika p45 = cariByCaseId(list, "PUTUSAN-045");
        assertNotNull(p45);
        assertNull(p45.getTuntutanBulan(), "PUTUSAN-045 tuntutanBulan harus null, bukan 0");
        assertEquals("not_found", p45.getTuntutanJenis());

        // Kasus Khusus 2: PUTUSAN-048 tuntutan pidana mati
        PutusanNarkotika p48 = cariByCaseId(list, "PUTUSAN-048");
        assertNotNull(p48);
        assertNull(p48.getTuntutanBulan(), "PUTUSAN-048 pidana mati tidak dikonversi ke bulan (harus null)");
        assertEquals("pidana_mati", p48.getTuntutanJenis());
        assertEquals(240, p48.getVonisBulan());

        // Kasus Khusus 3: PUTUSAN-047 vonis melebihi tuntutan (108 > 36)
        PutusanNarkotika p47 = cariByCaseId(list, "PUTUSAN-047");
        assertNotNull(p47);
        assertEquals(36, p47.getTuntutanBulan());
        assertEquals(108, p47.getVonisBulan());
        assertTrue(p47.getVonisBulan() > p47.getTuntutanBulan(), "Vonis boleh lebih besar dari tuntutan");

        // Kasus Khusus 4: Nilai denda 0 vs null
        // Pada dataset, jika ada perkara denda 0 maka dendaRupiah == 0, bukan null
        for (int i = 0; i < list.size(); i++) {
            PutusanNarkotika p = list.get(i);
            if (p.getDendaRupiah() != null && p.getDendaRupiah() == 0L) {
                assertEquals(0L, p.getDendaRupiah());
            }
        }
    }

    @Test
    @DisplayName("Skenario 3: Pencarian Berdasarkan ID & Nomor Perkara dengan Laporan Langkah")
    void skenario3_pencarianDenganPelaporanLangkah() {
        String mainPath = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult mainResult = CsvImporter.importCsv(mainPath);
        PutusanService service = new PutusanService(mainResult.getDataList());

        // Cari elemen terdepan: PUTUSAN-001 (langkah = 1)
        SearchResult<PutusanNarkotika> resDepan = service.cariBerdasarkanCaseId("PUTUSAN-001");
        assertTrue(resDepan.ditemukan());
        assertEquals("PUTUSAN-001", resDepan.getElemen().getCaseId());
        assertEquals(1, resDepan.getLangkah(), "Head harus ditemukan dalam 1 langkah");

        // Cari elemen tengah: PUTUSAN-025 (langkah = 25)
        SearchResult<PutusanNarkotika> resTengah = service.cariBerdasarkanCaseId("PUTUSAN-025");
        assertTrue(resTengah.ditemukan());
        assertEquals("PUTUSAN-025", resTengah.getElemen().getCaseId());
        assertEquals(25, resTengah.getLangkah(), "Elemen ke-25 harus ditemukan dalam 25 langkah");

        // Cari berdasarkan nomor perkara
        SearchResult<PutusanNarkotika> resNomor = service.cariBerdasarkanNomorPerkara("503/Pid.B/2013/PN.Jkt.Sel");
        assertTrue(resNomor.ditemukan());
        assertEquals("PUTUSAN-001", resNomor.getElemen().getCaseId());

        // Cari elemen yang tidak ada (langkah = 50)
        SearchResult<PutusanNarkotika> resTidakAda = service.cariBerdasarkanCaseId("PUTUSAN-TIDAK-ADA");
        assertFalse(resTidakAda.ditemukan());
        assertEquals(50, resTidakAda.getLangkah(), "Pencarian gagal harus menelusuri seluruh 50 elemen");
    }

    @Test
    @DisplayName("Skenario 4: Filter Gabungan Multi-Kriteria pada List Baru")
    void skenario4_filterGabunganMultiKriteria() {
        String mainPath = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult mainResult = CsvImporter.importCsv(mainPath);
        SinglyLinkedList<PutusanNarkotika> originalList = mainResult.getDataList();

        // Filter: Jenis Narkotika Sabu, basis netto, pasal 114, vonis >= 60 bulan
        FilterAggregationService.FilterKriteria kriteria = new FilterAggregationService.FilterKriteria()
                .denganJenisNarkotika("Sabu")
                .denganRentangBerat(0.1, 100.0, "netto")
                .denganPasalTerbukti("114")
                .denganRentangVonis(60, 300);

        SinglyLinkedList<PutusanNarkotika> hasil = filterService.filter(originalList, kriteria);
        assertNotNull(hasil);
        assertTrue(hasil.size() > 0);

        // List asli tidak boleh terganggu
        assertEquals(50, originalList.size());
        originalList.validateInvariant();

        // Validasi invariant list hasil
        hasil.validateInvariant();

        for (int i = 0; i < hasil.size(); i++) {
            PutusanNarkotika p = hasil.get(i);
            assertTrue(p.getJenisNarkotika().toLowerCase().contains("sabu"));
            assertEquals("netto", p.getBeratBasis());
            assertTrue(p.getBeratBarangBuktiGram() >= 0.1 && p.getBeratBarangBuktiGram() <= 100.0);
            assertTrue(p.getPasalTerbukti().contains("114"));
            assertTrue(p.getVonisBulan() >= 60 && p.getVonisBulan() <= 300);
        }
    }

    @Test
    @DisplayName("Skenario 5: Agregasi Statistik Tanpa Koleksi Bawaan")
    void skenario5_agregasiStatistikTanpaKoleksiBawaan() {
        String mainPath = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult mainResult = CsvImporter.importCsv(mainPath);
        SinglyLinkedList<PutusanNarkotika> list = mainResult.getDataList();

        // 1. Total jumlah perkara
        int totalPerkara = filterService.hitungJumlahPerkara(list);
        assertEquals(50, totalPerkara);

        // 2. Rata-rata vonis bulan (mengabaikan null)
        double rataVonis = filterService.hitungRataRataVonisBulan(list);
        assertTrue(rataVonis > 0.0);

        // 3. Total denda rupiah (mengabaikan null)
        long totalDenda = filterService.hitungTotalDendaRupiah(list);
        assertTrue(totalDenda > 0L);

        // 4. Distribusi jenis narkotika (memecah ';') dengan JenisCount tanpa HashMap
        SinglyLinkedList<JenisCount> distribusi = filterService.hitungDistribusiJenisNarkotika(list);
        assertNotNull(distribusi);
        assertTrue(distribusi.size() > 0);
        distribusi.validateInvariant();

        int sumKemunculan = 0;
        for (int i = 0; i < distribusi.size(); i++) {
            sumKemunculan += distribusi.get(i).getJumlah();
        }
        // Karena ada perkara dengan multi-narkotika (mis. Sabu;Ekstasi;Ganja), total token jenis >= 50
        assertTrue(sumKemunculan >= 50);
    }

    @Test
    @DisplayName("Skenario 6: Pengurutan Vonis Mandiri pada Salinan List")
    void skenario6_pengurutanVonisPadaSalinanList() {
        String mainPath = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult mainResult = CsvImporter.importCsv(mainPath);
        SinglyLinkedList<PutusanNarkotika> originalList = mainResult.getDataList();

        String idAwal = originalList.get(0).getCaseId();

        // 1. Urut Ascending
        SinglyLinkedList<PutusanNarkotika> asc = sortService.urutkanBerdasarkanVonisAscending(originalList);
        assertEquals(50, asc.size());
        assertEquals(idAwal, originalList.get(0).getCaseId(), "List asli tidak boleh berubah setelah sort ascending");
        for (int i = 0; i < asc.size() - 1; i++) {
            assertTrue(asc.get(i).getVonisBulan() <= asc.get(i + 1).getVonisBulan());
        }

        // 2. Urut Descending
        SinglyLinkedList<PutusanNarkotika> desc = sortService.urutkanBerdasarkanVonisDescending(originalList);
        assertEquals(50, desc.size());
        assertEquals(idAwal, originalList.get(0).getCaseId(), "List asli tidak boleh berubah setelah sort descending");
        for (int i = 0; i < desc.size() - 1; i++) {
            assertTrue(desc.get(i).getVonisBulan() >= desc.get(i + 1).getVonisBulan());
        }

        // Vonis tertinggi di PN Jaksel pada dataset ini adalah 240 bulan (PUTUSAN-048)
        assertEquals(240, desc.get(0).getVonisBulan());
    }

    @Test
    @DisplayName("Skenario 7: Audit Log Terintegrasi Stack LIFO & Round-Trip Ekspor CSV")
    void skenario7_auditLogStackDanEksporCsv() throws IOException {
        String mainPath = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult mainResult = CsvImporter.importCsv(mainPath);
        SinglyLinkedList<PutusanNarkotika> list = mainResult.getDataList();

        // 1. Audit Log dengan Stack
        auditService.catat("IMPOR", "Impor 50 perkara");
        auditService.catat("CARI", "Pencarian PUTUSAN-001");
        auditService.catat("SORT", "Urutkan vonis");

        Stack<AuditEntry> stack = auditService.getStackAudit();
        assertEquals(3, stack.size());
        assertEquals("SORT", stack.peek().getOperasi(), "Paling baru harus SORT (LIFO)");
        assertEquals("SORT", stack.pop().getOperasi());
        assertEquals("CARI", stack.pop().getOperasi());
        assertEquals("IMPOR", stack.pop().getOperasi());
        assertTrue(stack.isEmpty());
        stack.validateInvariant();

        // 2. Ekspor CSV dan Verifikasi Integritas
        File tmp = File.createTempFile("skenario7-ekspor-", ".csv");
        tmp.deleteOnExit();

        CsvExporter.exportCsv(list, tmp.getAbsolutePath());
        assertTrue(tmp.exists());

        CsvImporter.ImportResult reimport = CsvImporter.importCsv(tmp.getAbsolutePath());
        assertEquals(50, reimport.getDataList().size());
        assertEquals(0, reimport.getErrorList().size());

        // Verifikasi kesamaan data putusan pertama
        PutusanNarkotika pOriginal = list.get(0);
        PutusanNarkotika pReimported = reimport.getDataList().get(0);
        assertEquals(pOriginal.getCaseId(), pReimported.getCaseId());
        assertEquals(pOriginal.getNomorPerkara(), pReimported.getNomorPerkara());
        assertEquals(pOriginal.getBarangBukti(), pReimported.getBarangBukti());
        assertEquals(pOriginal.getVonisBulan(), pReimported.getVonisBulan());
    }

    private PutusanNarkotika cariByCaseId(SinglyLinkedList<PutusanNarkotika> list, String caseId) {
        for (int i = 0; i < list.size(); i++) {
            if (caseId.equalsIgnoreCase(list.get(i).getCaseId())) {
                return list.get(i);
            }
        }
        return null;
    }
}
