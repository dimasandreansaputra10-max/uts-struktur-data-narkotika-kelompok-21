package strukturdata.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import strukturdata.io.CsvImporter;
import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SearchResult;
import strukturdata.struktur.SinglyLinkedList;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PutusanServiceTest {

    private PutusanService service;

    private String getFilePath(String relativePath) {
        File f = new File(relativePath);
        if (f.exists()) return f.getAbsolutePath();

        File f2 = new File("C:/Users/user/.gemini/antigravity/scratch/uts-strukdat/" + relativePath);
        if (f2.exists()) return f2.getAbsolutePath();

        return relativePath;
    }

    private PutusanNarkotika createDummy(String caseId, String nomorPerkara) {
        return new PutusanNarkotika(
                caseId, nomorPerkara, "PN Jakarta Selatan", LocalDate.of(2024, 1, 1),
                "T-999", "Sabu", 1.5, "Pasal 112", "Pasal 112 UU 35",
                48, 42, 800000000L, "Barang bukti uji", "Memberatkan",
                "Meringankan", "hlm 1-5", "TUNGGAL", "pidana_penjara_bulan",
                "subsidair 1 bulan", "catatan", "netto"
        );
    }

    @BeforeEach
    void setup() {
        String path = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult res = CsvImporter.importCsv(path);
        service = new PutusanService(res.getDataList());
    }

    @Test
    void inisialisasiDanJumlahPerkara() {
        assertEquals(50, service.getJumlahPerkara());
        service.getListPutusan().validateInvariant();
    }

    @Test
    void tambahDiAwalMemakaiAddFirst() {
        PutusanNarkotika baru = createDummy("PUTUSAN-NEW-FIRST", "001/Pid.Sus/2026/PN.Jkt.Sel");
        service.tambahDiAwal(baru);

        assertEquals(51, service.getJumlahPerkara());
        assertEquals("PUTUSAN-NEW-FIRST", service.getListPutusan().get(0).getCaseId());
        service.getListPutusan().validateInvariant();
    }

    @Test
    void tambahDiAkhirMemakaiAddLast() {
        PutusanNarkotika baru = createDummy("PUTUSAN-NEW-LAST", "999/Pid.Sus/2026/PN.Jkt.Sel");
        service.tambahDiAkhir(baru);

        assertEquals(51, service.getJumlahPerkara());
        assertEquals("PUTUSAN-NEW-LAST", service.getListPutusan().get(50).getCaseId());
        service.getListPutusan().validateInvariant();
    }

    @Test
    void sisipkanPadaTengahList() {
        PutusanNarkotika baru = createDummy("PUTUSAN-NEW-MID", "555/Pid.Sus/2026/PN.Jkt.Sel");
        service.sisipkanPada(2, baru);

        assertEquals(51, service.getJumlahPerkara());
        assertEquals("PUTUSAN-NEW-MID", service.getListPutusan().get(2).getCaseId());
        service.getListPutusan().validateInvariant();
    }

    @Test
    void tambahCaseIdDuplikatMelemparException() {
        PutusanNarkotika duplikat = createDummy("PUTUSAN-001", "123/DUP");
        assertThrows(IllegalArgumentException.class, () -> service.tambahDiAkhir(duplikat));
    }

    @Test
    void hapusPadaIndeks() {
        // Hapus elemen pertama (indeks 0)
        PutusanNarkotika p0 = service.getListPutusan().get(0);
        PutusanNarkotika removed = service.hapusPada(0);

        assertEquals(p0.getCaseId(), removed.getCaseId());
        assertEquals(49, service.getJumlahPerkara());
        service.getListPutusan().validateInvariant();
    }

    @Test
    void hapusBerdasarkanCaseId() {
        PutusanNarkotika removed = service.hapusBerdasarkanCaseId("PUTUSAN-010");
        assertNotNull(removed);
        assertEquals("PUTUSAN-010", removed.getCaseId());
        assertEquals(49, service.getJumlahPerkara());

        // Verifikasi tidak ditemukan lagi
        SearchResult<PutusanNarkotika> cari = service.cariBerdasarkanCaseId("PUTUSAN-010");
        assertFalse(cari.ditemukan());
        service.getListPutusan().validateInvariant();

        // Hapus caseId yang tidak ada
        assertNull(service.hapusBerdasarkanCaseId("PUTUSAN-NON-EXISTENT"));
        assertEquals(49, service.getJumlahPerkara());
    }

    @Test
    void cariBerdasarkanCaseIdDenganJumlahLangkah() {
        // Elemen pertama: harus selesai dalam 1 langkah
        SearchResult<PutusanNarkotika> res1 = service.cariBerdasarkanCaseId("PUTUSAN-001");
        assertTrue(res1.ditemukan());
        assertEquals("PUTUSAN-001", res1.getElemen().getCaseId());
        assertEquals(1, res1.getLangkah(), "Pencarian elemen di head harus selesai dalam 1 langkah");

        // Elemen ke-10 (PUTUSAN-010): harus selesai dalam 10 langkah
        SearchResult<PutusanNarkotika> res10 = service.cariBerdasarkanCaseId("PUTUSAN-010");
        assertTrue(res10.ditemukan());
        assertEquals("PUTUSAN-010", res10.getElemen().getCaseId());
        assertEquals(10, res10.getLangkah(), "Pencarian elemen ke-10 harus selesai dalam 10 langkah");

        // Elemen tidak ada: harus menelusuri seluruh 50 node
        SearchResult<PutusanNarkotika> resNotFound = service.cariBerdasarkanCaseId("PUTUSAN-999");
        assertFalse(resNotFound.ditemukan());
        assertNull(resNotFound.getElemen());
        assertEquals(50, resNotFound.getLangkah(), "Pencarian elemen yang tidak ada harus menelusuri 50 langkah");
    }

    @Test
    void cariBerdasarkanNomorPerkaraDenganJumlahLangkah() {
        // Cari perkara PUTUSAN-001 dengan nomor "503/Pid.B/2013/PN.Jkt.Sel"
        SearchResult<PutusanNarkotika> res = service.cariBerdasarkanNomorPerkara("503/Pid.B/2013/PN.Jkt.Sel");
        assertTrue(res.ditemukan());
        assertEquals("PUTUSAN-001", res.getElemen().getCaseId());
        assertEquals(1, res.getLangkah());

        // Cari partial match (substring)
        SearchResult<PutusanNarkotika> resSubstring = service.cariBerdasarkanNomorPerkara("503/Pid.B");
        assertTrue(resSubstring.ditemukan());
        assertEquals("PUTUSAN-001", resSubstring.getElemen().getCaseId());
        assertEquals(1, resSubstring.getLangkah());
    }
}
