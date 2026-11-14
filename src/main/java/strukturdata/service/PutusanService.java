package strukturdata.service;

import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SearchResult;
import strukturdata.struktur.SinglyLinkedList;

/**
 * Service untuk operasi bisnis putusan narkotika:
 * 1. Menampilkan seluruh data.
 * 2. Menambah data (di awal, akhir, atau posisi tertentu) menggunakan operasi asli list.
 * 3. Menghapus data (berdasarkan indeks atau caseId) menggunakan operasi asli list.
 * 4. Mencari perkara berdasarkan caseId atau nomorPerkara dengan pelaporan jumlah langkah (stepCount).
 */
public class PutusanService {

    private final SinglyLinkedList<PutusanNarkotika> listPutusan;

    public PutusanService() {
        this.listPutusan = new SinglyLinkedList<>();
    }

    public PutusanService(SinglyLinkedList<PutusanNarkotika> initialList) {
        if (initialList == null) {
            this.listPutusan = new SinglyLinkedList<>();
        } else {
            this.listPutusan = initialList;
        }
        this.listPutusan.validateInvariant();
    }

    /**
     * Mendapatkan referensi list putusan saat ini.
     */
    public SinglyLinkedList<PutusanNarkotika> getListPutusan() {
        return listPutusan;
    }

    /**
     * Mendapatkan jumlah perkara yang tersimpan.
     */
    public int getJumlahPerkara() {
        return listPutusan.size();
    }

    // =========================================================================
    // Operasi Penambahan (Memakai operasi asli SinglyLinkedList)
    // =========================================================================

    /**
     * Menambahkan putusan di awal list (O(1)).
     */
    public void tambahDiAwal(PutusanNarkotika putusan) {
        if (putusan == null) throw new IllegalArgumentException("Putusan tidak boleh null");
        cekCaseIdUnik(putusan.getCaseId());
        listPutusan.addFirst(putusan);
        listPutusan.validateInvariant();
    }

    /**
     * Menambahkan putusan di akhir list (O(1) menggunakan tail).
     */
    public void tambahDiAkhir(PutusanNarkotika putusan) {
        if (putusan == null) throw new IllegalArgumentException("Putusan tidak boleh null");
        cekCaseIdUnik(putusan.getCaseId());
        listPutusan.addLast(putusan);
        listPutusan.validateInvariant();
    }

    /**
     * Menyisipkan putusan pada indeks posisi tertentu (0..size).
     */
    public void sisipkanPada(int indeks, PutusanNarkotika putusan) {
        if (putusan == null) throw new IllegalArgumentException("Putusan tidak boleh null");
        cekCaseIdUnik(putusan.getCaseId());
        listPutusan.insert(indeks, putusan);
        listPutusan.validateInvariant();
    }

    private void cekCaseIdUnik(String caseId) {
        if (caseId == null || caseId.isEmpty()) {
            throw new IllegalArgumentException("caseId tidak boleh kosong");
        }
        for (int i = 0; i < listPutusan.size(); i++) {
            if (caseId.equalsIgnoreCase(listPutusan.get(i).getCaseId())) {
                throw new IllegalArgumentException("caseId sudah terdaftar: " + caseId);
            }
        }
    }

    // =========================================================================
    // Operasi Penghapusan (Memakai operasi asli SinglyLinkedList)
    // =========================================================================

    /**
     * Menghapus putusan pada indeks tertentu (0..size-1).
     */
    public PutusanNarkotika hapusPada(int indeks) {
        PutusanNarkotika removed = listPutusan.remove(indeks);
        listPutusan.validateInvariant();
        return removed;
    }

    /**
     * Menghapus putusan berdasarkan caseId.
     * Mengembalikan objek yang dihapus, atau null jika tidak ditemukan.
     */
    public PutusanNarkotika hapusBerdasarkanCaseId(String caseId) {
        if (caseId == null) return null;
        for (int i = 0; i < listPutusan.size(); i++) {
            if (caseId.equalsIgnoreCase(listPutusan.get(i).getCaseId())) {
                return hapusPada(i);
            }
        }
        return null;
    }

    // =========================================================================
    // Operasi Pencarian dengan Pelaporan Langkah (SearchResult)
    // =========================================================================

    /**
     * Mencari putusan berdasarkan caseId (case-insensitive).
     * Mengembalikan SearchResult berisi objek dan jumlah langkah perbandingan.
     */
    public SearchResult<PutusanNarkotika> cariBerdasarkanCaseId(String caseId) {
        if (caseId == null || caseId.trim().isEmpty()) {
            return new SearchResult<>(null, 0);
        }
        String target = caseId.trim();
        return listPutusan.search(item -> item != null && target.equalsIgnoreCase(item.getCaseId()));
    }

    /**
     * Mencari putusan berdasarkan nomorPerkara (bisa kecocokan penuh atau substring).
     * Mengembalikan SearchResult berisi objek dan jumlah langkah perbandingan.
     */
    public SearchResult<PutusanNarkotika> cariBerdasarkanNomorPerkara(String nomorPerkara) {
        if (nomorPerkara == null || nomorPerkara.trim().isEmpty()) {
            return new SearchResult<>(null, 0);
        }
        String target = nomorPerkara.trim();
        return listPutusan.search(item -> item != null && item.getNomorPerkara() != null &&
                (item.getNomorPerkara().equalsIgnoreCase(target) || item.getNomorPerkara().contains(target)));
    }
}
