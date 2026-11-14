package strukturdata.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import strukturdata.io.CsvImporter;
import strukturdata.model.JenisCount;
import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FilterAggregationServiceTest {

    private FilterAggregationService service;
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
        service = new FilterAggregationService();
        String path = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult res = CsvImporter.importCsv(path);
        dataset = res.getDataList();
    }

    @Test
    void filterBerdasarkanJenisNarkotika() {
        FilterAggregationService.FilterKriteria k = new FilterAggregationService.FilterKriteria()
                .denganJenisNarkotika("Ganja");

        SinglyLinkedList<PutusanNarkotika> hasil = service.filter(dataset, k);
        assertNotNull(hasil);
        assertTrue(hasil.size() > 0);
        // List sumber tidak berubah ukurannya
        assertEquals(50, dataset.size());

        for (int i = 0; i < hasil.size(); i++) {
            assertTrue(hasil.get(i).getJenisNarkotika().toLowerCase().contains("ganja"));
        }
        hasil.validateInvariant();
    }

    @Test
    void filterBerdasarkanRentangBeratDanBasisNetto() {
        // Berat antara 0.1 gram s/d 1.0 gram basis netto
        FilterAggregationService.FilterKriteria k = new FilterAggregationService.FilterKriteria()
                .denganRentangBerat(0.1, 1.0, "netto");

        SinglyLinkedList<PutusanNarkotika> hasil = service.filter(dataset, k);
        assertNotNull(hasil);
        assertTrue(hasil.size() > 0);

        for (int i = 0; i < hasil.size(); i++) {
            PutusanNarkotika p = hasil.get(i);
            assertNotNull(p.getBeratBarangBuktiGram());
            assertTrue(p.getBeratBarangBuktiGram() >= 0.1 && p.getBeratBarangBuktiGram() <= 1.0);
            assertEquals("netto", p.getBeratBasis());
        }
        hasil.validateInvariant();
    }

    @Test
    void filterBerdasarkanPasalTerbuktiDanVonis() {
        FilterAggregationService.FilterKriteria k = new FilterAggregationService.FilterKriteria()
                .denganPasalTerbukti("114")
                .denganRentangVonis(60, 150);

        SinglyLinkedList<PutusanNarkotika> hasil = service.filter(dataset, k);
        assertNotNull(hasil);
        assertTrue(hasil.size() > 0);

        for (int i = 0; i < hasil.size(); i++) {
            PutusanNarkotika p = hasil.get(i);
            assertTrue(p.getPasalTerbukti().contains("114"));
            assertTrue(p.getVonisBulan() >= 60 && p.getVonisBulan() <= 150);
        }
        hasil.validateInvariant();
    }

    @Test
    void filterGabunganSemuaKriteria() {
        FilterAggregationService.FilterKriteria k = new FilterAggregationService.FilterKriteria()
                .denganJenisNarkotika("Sabu")
                .denganRentangBerat(1.0, 10.0, "netto")
                .denganPasalTerbukti("114")
                .denganRentangVonis(60, 180);

        SinglyLinkedList<PutusanNarkotika> hasil = service.filter(dataset, k);
        assertNotNull(hasil);
        hasil.validateInvariant();

        for (int i = 0; i < hasil.size(); i++) {
            PutusanNarkotika p = hasil.get(i);
            assertTrue(p.getJenisNarkotika().toLowerCase().contains("sabu"));
            assertTrue(p.getBeratBarangBuktiGram() >= 1.0 && p.getBeratBarangBuktiGram() <= 10.0);
            assertEquals("netto", p.getBeratBasis());
            assertTrue(p.getPasalTerbukti().contains("114"));
            assertTrue(p.getVonisBulan() >= 60 && p.getVonisBulan() <= 180);
        }
    }

    @Test
    void agregasiJumlahDanRataRataVonis() {
        int jumlah = service.hitungJumlahPerkara(dataset);
        assertEquals(50, jumlah);

        double rataRata = service.hitungRataRataVonisBulan(dataset);
        assertTrue(rataRata > 0.0, "Rata-rata vonis harus lebih besar dari 0");
        // Vonis berkisar puluhan hingga ratusan bulan
        assertTrue(rataRata >= 30.0 && rataRata <= 120.0);
    }

    @Test
    void agregasiTotalDenda() {
        long totalDenda = service.hitungTotalDendaRupiah(dataset);
        assertTrue(totalDenda > 0L, "Total denda harus lebih dari 0");
    }

    @Test
    void agregasiDistribusiJenisNarkotikaDenganPecahTitikKoma() {
        SinglyLinkedList<JenisCount> distribusi = service.hitungDistribusiJenisNarkotika(dataset);
        assertNotNull(distribusi);
        assertTrue(distribusi.size() > 0);
        distribusi.validateInvariant();

        // Cari jenis Sabu dan Ekstasi
        JenisCount sabu = null;
        JenisCount ekstasi = null;
        for (int i = 0; i < distribusi.size(); i++) {
            JenisCount jc = distribusi.get(i);
            if ("Sabu".equalsIgnoreCase(jc.getNama())) {
                sabu = jc;
            } else if ("Ekstasi".equalsIgnoreCase(jc.getNama())) {
                ekstasi = jc;
            }
        }

        assertNotNull(sabu, "Distribusi harus memuat jenis Sabu");
        assertTrue(sabu.getJumlah() >= 20, "Sabu harus muncul banyak di perkara narkotika");

        assertNotNull(ekstasi, "Distribusi harus memuat Ekstasi (dipecah dari Sabu;Ekstasi;Ganja)");
        assertTrue(ekstasi.getJumlah() >= 1);
    }
}
