package strukturdata.service;

import strukturdata.model.AuditEntry;
import strukturdata.struktur.Kriteria;
import strukturdata.struktur.SearchResult;
import strukturdata.struktur.Stack;

import java.time.LocalDateTime;

/**
 * Service pengelola riwayat audit operasi sistem berbasis Stack (LIFO).
 * Setiap mutasi data dan aksi penting disimpan sebagai AuditEntry.
 */
public class AuditService {

    private final Stack<AuditEntry> stackAudit;

    public AuditService() {
        this.stackAudit = new Stack<>();
    }

    /**
     * Mencatat aksi baru ke dalam audit stack (push O(1)).
     */
    public void catat(String operasi, String rincian) {
        stackAudit.push(new AuditEntry(operasi, rincian, LocalDateTime.now()));
        stackAudit.validateInvariant();
    }

    /**
     * Mengambil seluruh riwayat audit tanpa merusak struktur stack.
     * Mengembalikan list entri dari yang paling baru (LIFO).
     */
    public Stack<AuditEntry> getStackAudit() {
        return stackAudit;
    }

    /**
     * Jumlah entri audit yang tersimpan.
     */
    public int getJumlahAudit() {
        return stackAudit.size();
    }

    /**
     * Mengambil entri audit terbaru (peek).
     */
    public AuditEntry getTerbaru() {
        if (stackAudit.isEmpty()) return null;
        return stackAudit.peek();
    }

    /**
     * Mencari audit entri berdasarkan kriteria dengan menjaga urutan stack tetap utuh.
     */
    public SearchResult<AuditEntry> cariPreserve(Kriteria<AuditEntry> kriteria) {
        return stackAudit.searchPreserve(kriteria);
    }
}
