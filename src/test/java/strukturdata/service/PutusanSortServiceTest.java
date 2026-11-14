package strukturdata.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import strukturdata.io.CsvImporter;
import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class PutusanSortServiceTest {

    private PutusanSortService sortService;
    private SinglyLinkedList<PutusanNarkotika> dataset;

    private String getFilePath(String relativePath) {
        File f = new File(relativePath);
        if (f.exists()) return f.getAbsolutePath();

        File f2 = new File("C:/Users/user/.gemini/antigravity/scratch/uts-strukdat/" + relativePath);
        if (f2.exists()) return f2.getAbsolutePath();

        return relativePath;
    }

    @BeforeEach
    void setup() {
        sortService = new PutusanSortService();
        String path = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult res = CsvImporter.importCsv(path);
        dataset = res.getDataList();
    }

    @Test
    void salinListMembuatListBaruIdentik() {
        SinglyLinkedList<PutusanNarkotika> copy = sortService.salinList(dataset);
        assertNotNull(copy);
        assertNotSame(dataset, copy, "Objek list harus merupakan instance baru");
        assertEquals(dataset.size(), copy.size());

        for (int i = 0; i < dataset.size(); i++) {
            assertEquals(dataset.get(i).getCaseId(), copy.get(i).getCaseId());
        }
        copy.validateInvariant();
    }

    @Test
    void urutkanVonisAscendingMenghasilkanUrutanNaikDanListAsliUtuh() {
        String caseIdPertamaAsli = dataset.get(0).getCaseId();
        int sizeAsli = dataset.size();

        SinglyLinkedList<PutusanNarkotika> sorted = sortService.urutkanBerdasarkanVonisAscending(dataset);

        // Verifikasi list asli TIDAK termodifikasi
        assertEquals(sizeAsli, dataset.size(), "Ukuran list utama tidak boleh berubah");
        assertEquals(caseIdPertamaAsli, dataset.get(0).getCaseId(), "Urutan list utama tidak boleh berubah");
        dataset.validateInvariant();

        // Verifikasi hasil pengurutan
        assertEquals(50, sorted.size());
        sorted.validateInvariant();

        for (int i = 0; i < sorted.size() - 1; i++) {
            int v1 = sorted.get(i).getVonisBulan();
            int v2 = sorted.get(i + 1).getVonisBulan();
            assertTrue(v1 <= v2, "Vonis index " + i + " (" + v1 + ") harus <= vonis index " + (i + 1) + " (" + v2 + ")");
        }

        // Cek elemen vonis terkecil dan terbesar
        assertTrue(sorted.get(0).getVonisBulan() <= sorted.get(sorted.size() - 1).getVonisBulan());
    }

    @Test
    void urutkanVonisDescendingMenghasilkanUrutanTurunDanListAsliUtuh() {
        String caseIdPertamaAsli = dataset.get(0).getCaseId();
        int sizeAsli = dataset.size();

        SinglyLinkedList<PutusanNarkotika> sorted = sortService.urutkanBerdasarkanVonisDescending(dataset);

        // Verifikasi list asli tetap utuh
        assertEquals(sizeAsli, dataset.size());
        assertEquals(caseIdPertamaAsli, dataset.get(0).getCaseId());
        dataset.validateInvariant();

        // Verifikasi urutan turun
        assertEquals(50, sorted.size());
        sorted.validateInvariant();

        for (int i = 0; i < sorted.size() - 1; i++) {
            int v1 = sorted.get(i).getVonisBulan();
            int v2 = sorted.get(i + 1).getVonisBulan();
            assertTrue(v1 >= v2, "Vonis index " + i + " (" + v1 + ") harus >= vonis index " + (i + 1) + " (" + v2 + ")");
        }

        // Vonis tertinggi harus di depan (mis. PUTUSAN-048 vonis 240 bulan)
        assertEquals(240, sorted.get(0).getVonisBulan());
    }

    @Test
    void urutkanListKosongDanSatuElemen() {
        SinglyLinkedList<PutusanNarkotika> kosong = new SinglyLinkedList<>();
        SinglyLinkedList<PutusanNarkotika> resKosong = sortService.urutkanBerdasarkanVonisAscending(kosong);
        assertEquals(0, resKosong.size());
        resKosong.validateInvariant();

        SinglyLinkedList<PutusanNarkotika> satu = new SinglyLinkedList<>();
        satu.addLast(dataset.get(0));
        SinglyLinkedList<PutusanNarkotika> resSatu = sortService.urutkanBerdasarkanVonisAscending(satu);
        assertEquals(1, resSatu.size());
        assertEquals(dataset.get(0).getCaseId(), resSatu.get(0).getCaseId());
        resSatu.validateInvariant();
    }
}
