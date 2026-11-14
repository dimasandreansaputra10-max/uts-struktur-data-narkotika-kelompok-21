package strukturdata.service;

import org.junit.jupiter.api.Test;
import strukturdata.model.AuditEntry;
import strukturdata.struktur.SearchResult;

import static org.junit.jupiter.api.Assertions.*;

class AuditServiceTest {

    @Test
    void catatOperasiDanUrutanLIFO() {
        AuditService service = new AuditService();
        assertEquals(0, service.getJumlahAudit());

        service.catat("IMPOR", "50 data diimpor");
        service.catat("TAMBAH", "PUTUSAN-051 ditambahkan");
        service.catat("FILTER", "Filter Sabu");

        assertEquals(3, service.getJumlahAudit());

        // Terbaru di puncak (LIFO)
        AuditEntry terbaru = service.getTerbaru();
        assertNotNull(terbaru);
        assertEquals("FILTER", terbaru.getOperasi());

        // Verifikasi traversal dari index 0 = terbaru
        assertEquals("FILTER", service.getStackAudit().get(0).getOperasi());
        assertEquals("TAMBAH", service.getStackAudit().get(1).getOperasi());
        assertEquals("IMPOR", service.getStackAudit().get(2).getOperasi());
    }

    @Test
    void searchPreservePadaAuditLog() {
        AuditService service = new AuditService();
        service.catat("AKSI_1", "Rincian 1");
        service.catat("AKSI_2", "Rincian 2");
        service.catat("AKSI_3", "Rincian 3");

        SearchResult<AuditEntry> res = service.cariPreserve(entry -> "AKSI_2".equals(entry.getOperasi()));
        assertTrue(res.ditemukan());
        assertEquals("AKSI_2", res.getElemen().getOperasi());
        assertEquals(2, res.getLangkah()); // langkah 1 = AKSI_3, langkah 2 = AKSI_2

        // Pastikan ukuran dan posisi stack tetap utuh
        assertEquals(3, service.getJumlahAudit());
        assertEquals("AKSI_3", service.getTerbaru().getOperasi());
    }
}
