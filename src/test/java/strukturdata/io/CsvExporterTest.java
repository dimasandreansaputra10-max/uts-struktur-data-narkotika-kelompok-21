package strukturdata.io;

import org.junit.jupiter.api.Test;
import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CsvExporterTest {

    private String getFilePath(String relativePath) {
        File f = new File(relativePath);
        if (f.exists()) return f.getAbsolutePath();

        File f2 = new File("C:/Users/user/.gemini/antigravity/scratch/uts-strukdat/" + relativePath);
        if (f2.exists()) return f2.getAbsolutePath();

        return relativePath;
    }

    @Test
    void exportDanReimporMenghasilkanDataIdentik() throws IOException {
        String sourcePath = getFilePath("data/putusan.csv");
        CsvImporter.ImportResult original = CsvImporter.importCsv(sourcePath);
        assertEquals(50, original.getDataList().size());

        File tempExport = File.createTempFile("ekspor-test-", ".csv");
        tempExport.deleteOnExit();

        CsvExporter.exportCsv(original.getDataList(), tempExport.getAbsolutePath());
        assertTrue(tempExport.exists());
        assertTrue(tempExport.length() > 0);

        // Impor kembali file hasil ekspor
        CsvImporter.ImportResult reimpor = CsvImporter.importCsv(tempExport.getAbsolutePath());
        assertEquals(50, reimpor.getDataList().size(), "Hasil reimpor harus tepat 50 record");
        assertEquals(0, reimpor.getErrorList().size(), "Tidak boleh ada error saat parsing kembali ekspor");

        // Cek data pertama dan data kasus khusus
        PutusanNarkotika p1 = reimpor.getDataList().get(0);
        assertEquals("PUTUSAN-001", p1.getCaseId());
        assertEquals("503/Pid.B/2013/PN.Jkt.Sel", p1.getNomorPerkara());
        assertEquals(4.7855, p1.getBeratBarangBuktiGram(), 0.0001);
        assertEquals(132, p1.getVonisBulan());

        // Cek kasus kutip ganda dan koma pada barang bukti
        assertTrue(p1.getBarangBukti().contains("Rp200.000"));
    }
}
