package strukturdata.io;

import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;
import strukturdata.validator.PutusanValidator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Pengimpor data putusan narkotika dari berkas CSV.
 * Merekam baris bermasalah ke dalam SinglyLinkedList<String> errorList tanpa menghentikan program.
 */
public class CsvImporter {

    public static class ImportResult {
        private final SinglyLinkedList<PutusanNarkotika> dataList;
        private final SinglyLinkedList<String> errorList;
        private final int totalBarisDiproses;

        public ImportResult(SinglyLinkedList<PutusanNarkotika> dataList, SinglyLinkedList<String> errorList, int totalBarisDiproses) {
            this.dataList = dataList;
            this.errorList = errorList;
            this.totalBarisDiproses = totalBarisDiproses;
        }

        public SinglyLinkedList<PutusanNarkotika> getDataList() { return dataList; }
        public SinglyLinkedList<String> getErrorList() { return errorList; }
        public int getTotalBarisDiproses() { return totalBarisDiproses; }
    }

    /**
     * Mengimpor berkas CSV dari path tertentu.
     *
     * @param filePath path berkas CSV
     * @return ImportResult memuat data yang valid dan daftar pesan kesalahan
     */
    public static ImportResult importCsv(String filePath) {
        SinglyLinkedList<PutusanNarkotika> dataList = new SinglyLinkedList<>();
        SinglyLinkedList<String> errorList = new SinglyLinkedList<>();
        SinglyLinkedList<String> existingCaseIds = new SinglyLinkedList<>();

        int totalBaris = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String baris = reader.readLine();
            // Abaikan header jika ada
            if (baris != null && baris.startsWith("caseId,")) {
                // Header dilewati
            } else if (baris != null) {
                // Baris pertama bukan header, proses sebagai data
                totalBaris++;
                prosesBaris(baris, totalBaris, dataList, errorList, existingCaseIds);
            }

            while ((baris = reader.readLine()) != null) {
                if (baris.trim().isEmpty()) {
                    continue; // Lewati baris kosong
                }
                totalBaris++;
                prosesBaris(baris, totalBaris, dataList, errorList, existingCaseIds);
            }

        } catch (IOException e) {
            errorList.addLast("Gagal membaca berkas CSV: " + e.getMessage());
        }

        // Pastikan invarian kedua list terjaga
        dataList.validateInvariant();
        errorList.validateInvariant();

        return new ImportResult(dataList, errorList, totalBaris);
    }

    private static void prosesBaris(String baris, int nomorBaris,
                                    SinglyLinkedList<PutusanNarkotika> dataList,
                                    SinglyLinkedList<String> errorList,
                                    SinglyLinkedList<String> existingCaseIds) {
        SinglyLinkedList<String> tokens = CsvParser.parseBaris(baris);
        PutusanValidator.ValidationResult result = PutusanValidator.validasi(tokens, existingCaseIds);

        if (result.isValid()) {
            PutusanNarkotika p = result.getPutusan();
            dataList.addLast(p);
            existingCaseIds.addLast(p.getCaseId());
        } else {
            errorList.addLast("Baris " + nomorBaris + " ditolak: " + result.getPesanKesalahan());
        }
    }
}

