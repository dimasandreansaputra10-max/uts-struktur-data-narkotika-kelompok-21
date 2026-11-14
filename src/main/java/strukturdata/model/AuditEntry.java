package strukturdata.model;

import java.time.LocalDateTime;

/**
 * Model entri riwayat audit operasi sistem (LIFO di Stack).
 */
public class AuditEntry {
    private final String operasi;
    private final String rincian;
    private final LocalDateTime waktu;

    public AuditEntry(String operasi, String rincian) {
        this.operasi = operasi;
        this.rincian = rincian;
        this.waktu = LocalDateTime.now();
    }

    public AuditEntry(String operasi, String rincian, LocalDateTime waktu) {
        this.operasi = operasi;
        this.rincian = rincian;
        this.waktu = waktu;
    }

    public String getOperasi() {
        return operasi;
    }

    public String getRincian() {
        return rincian;
    }

    public LocalDateTime getWaktu() {
        return waktu;
    }

    @Override
    public String toString() {
        return "[" + waktu + "] " + operasi + ": " + rincian;
    }
}

