package strukturdata.io;

import org.junit.jupiter.api.Test;
import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CsvImporterTest {

    private String getFilePath(String relativePath) {
        // Coba cari dari working dir atau subpath data/src
        File f = new File(relativePath);
        if (f.exists()) return f.getAbsolutePath();

        File f2 = new File("C:/Users/user/.gemini/antigravity/scratch/uts-strukdat/" + relativePath);
        if (f2.exists()) return f2.getAbsolutePath();

        return relativePath;
    }

    @Test
    void imporDataUtama50RecordValid() {
        String path = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult result = CsvImporter.importCsv(path);

        assertNotNull(result);
        assertEquals(50, result.getDataList().size(), "Harus tepat 50 record yang berhasil diimpor");
        assertEquals(0, result.getErrorList().size(), "Tidak boleh ada record valid yang ditolak");

        // Cek invariant kedua list
        result.getDataList().validateInvariant();
        result.getErrorList().validateInvariant();

        // Verifikasi record pertama (PUTUSAN-001)
        PutusanNarkotika p1 = result.getDataList().get(0);
        assertEquals("PUTUSAN-001", p1.getCaseId());
        assertEquals("503/Pid.B/2013/PN.Jkt.Sel", p1.getNomorPerkara());
        assertEquals("PN Jakarta Selatan", p1.getPengadilan());
        assertEquals("Sabu", p1.getJenisNarkotika());
        assertEquals(4.7855, p1.getBeratBarangBuktiGram(), 0.0001);
        assertEquals(156, p1.getTuntutanBulan());
        assertEquals(132, p1.getVonisBulan());
        assertEquals(800000000L, p1.getDendaRupiah());
        assertEquals("netto", p1.getBeratBasis());

        // Verifikasi kasus khusus 045: tuntutanBulan null (not_found)
        PutusanNarkotika p45 = null;
        for (int i = 0; i < result.getDataList().size(); i++) {
            if ("PUTUSAN-045".equals(result.getDataList().get(i).getCaseId())) {
                p45 = result.getDataList().get(i);
                break;
            }
        }
        assertNotNull(p45);
        assertNull(p45.getTuntutanBulan(), "Tuntutan bulan PUTUSAN-045 harus null (not_found)");
        assertEquals("not_found", p45.getTuntutanJenis());

        // Verifikasi kasus khusus 047: vonis melebihi tuntutan (108 > 36)
        PutusanNarkotika p47 = null;
        for (int i = 0; i < result.getDataList().size(); i++) {
            if ("PUTUSAN-047".equals(result.getDataList().get(i).getCaseId())) {
                p47 = result.getDataList().get(i);
                break;
            }
        }
        assertNotNull(p47);
        assertEquals(36, p47.getTuntutanBulan());
        assertEquals(108, p47.getVonisBulan());
        assertTrue(p47.getVonisBulan() > p47.getTuntutanBulan());

        // Verifikasi kasus khusus 048: tuntutan pidana mati (tuntutanBulan null)
        PutusanNarkotika p48 = null;
        for (int i = 0; i < result.getDataList().size(); i++) {
            if ("PUTUSAN-048".equals(result.getDataList().get(i).getCaseId())) {
                p48 = result.getDataList().get(i);
                break;
            }
        }
        assertNotNull(p48);
        assertNull(p48.getTuntutanBulan(), "Tuntutan bulan PUTUSAN-048 harus null (pidana mati)");
        assertEquals("pidana_mati", p48.getTuntutanJenis());
        assertEquals(240, p48.getVonisBulan());
    }

    @Test
    void imporInvalidRecordsDitolakDenganPesanKesalahan() {
        String path = getFilePath("src/test/resources/invalid-records.csv");
        CsvImporter.ImportResult result = CsvImporter.importCsv(path);

        assertNotNull(result);
        // Pada invalid-records.csv:
        // Baris 1: INVALID-SHORT-001 (kolom kurang dari 21) -> ditolak
        // Baris 2: INVALID-MISSING-NOMOR (nomorPerkara kosong) -> ditolak
        // Baris 3: INVALID-DUPLICATE-001 (pertama kali masuk) -> valid
        // Baris 4: INVALID-DUPLICATE-001 (duplikat caseId) -> ditolak
        assertEquals(1, result.getDataList().size(), "Hanya 1 record (duplikat pertama) yang lolos");
        assertEquals(3, result.getErrorList().size(), "3 baris bermasalah harus dicatat di errorList");

        // Periksa pesan kesalahan pada error list
        assertTrue(result.getErrorList().get(0).contains("Jumlah kolom tidak valid"));
        assertTrue(result.getErrorList().get(1).contains("nomorPerkara wajib diisi"));
        assertTrue(result.getErrorList().get(2).contains("caseId duplikat"));

        // Validasi invariant kedua struktur data
        result.getDataList().validateInvariant();
        result.getErrorList().validateInvariant();
    }
}

