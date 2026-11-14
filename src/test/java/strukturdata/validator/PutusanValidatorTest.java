package strukturdata.validator;

import org.junit.jupiter.api.Test;
import strukturdata.io.CsvParser;
import strukturdata.struktur.SinglyLinkedList;

import static org.junit.jupiter.api.Assertions.*;

class PutusanValidatorTest {

    private SinglyLinkedList<String> createValid21Fields() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.addLast("PUTUSAN-TEST-001"); // 0: caseId
        list.addLast("123/Pid.Sus/2024/PN.Jkt.Sel"); // 1: nomorPerkara
        list.addLast("PN Jakarta Selatan"); // 2: pengadilan
        list.addLast("2024-05-15"); // 3: tanggalPutusan
        list.addLast("T-TEST"); // 4: terdakwaKode
        list.addLast("Sabu"); // 5: jenisNarkotika
        list.addLast("1.25"); // 6: beratBarangBuktiGram
        list.addLast("Pasal 112 ayat (1)"); // 7: pasalDakwaan
        list.addLast("Pasal 112 ayat (1) UU 35/2009"); // 8: pasalTerbukti
        list.addLast("48"); // 9: tuntutanBulan
        list.addLast("40"); // 10: vonisBulan
        list.addLast("800000000"); // 11: dendaRupiah
        list.addLast("1 bungkus plastik sabu"); // 12: barangBukti
        list.addLast("Merusak generasi muda"); // 13: keadaanMemberatkan
        list.addLast("Mengakui perbuatan"); // 14: keadaanMeringankan
        list.addLast("hlm 1-10"); // 15: sourcePages
        list.addLast("TUNGGAL"); // 16: bentukDakwaan
        list.addLast("pidana_penjara_bulan"); // 17: tuntutanJenis
        list.addLast("subsidair 2 bulan"); // 18: dendaCatatan
        list.addLast("catatan valid"); // 19: notes
        list.addLast("netto"); // 20: beratBasis
        return list;
    }

    @Test
    void valid21FieldsLolosValidasi() {
        SinglyLinkedList<String> tokens = createValid21Fields();
        PutusanValidator.ValidationResult res = PutusanValidator.validasi(tokens, new SinglyLinkedList<>());
        assertTrue(res.isValid());
        assertNull(res.getPesanKesalahan());
        assertNotNull(res.getPutusan());
        assertEquals("PUTUSAN-TEST-001", res.getPutusan().getCaseId());
    }

    @Test
    void jumlahKolomKurangDitolak() {
        SinglyLinkedList<String> tokens = new SinglyLinkedList<>();
        tokens.addLast("PUTUSAN-001");
        tokens.addLast("NOMOR");
        PutusanValidator.ValidationResult res = PutusanValidator.validasi(tokens, new SinglyLinkedList<>());
        assertFalse(res.isValid());
        assertTrue(res.getPesanKesalahan().contains("Jumlah kolom tidak valid"));
    }

    @Test
    void caseIdKosongAtauDuplikatDitolak() {
        SinglyLinkedList<String> tokens1 = createValid21Fields();
        tokens1.remove(0);
        tokens1.insert(0, ""); // caseId kosong
        PutusanValidator.ValidationResult res1 = PutusanValidator.validasi(tokens1, new SinglyLinkedList<>());
        assertFalse(res1.isValid());
        assertTrue(res1.getPesanKesalahan().contains("caseId wajib diisi"));

        SinglyLinkedList<String> existing = new SinglyLinkedList<>();
        existing.addLast("PUTUSAN-TEST-001");
        SinglyLinkedList<String> tokens2 = createValid21Fields();
        PutusanValidator.ValidationResult res2 = PutusanValidator.validasi(tokens2, existing);
        assertFalse(res2.isValid());
        assertTrue(res2.getPesanKesalahan().contains("caseId duplikat"));
    }

    @Test
    void tanggalFormatTidakValidDitolak() {
        SinglyLinkedList<String> tokens = createValid21Fields();
        tokens.remove(3);
        tokens.insert(3, "31-31-2024"); // format salah
        PutusanValidator.ValidationResult res = PutusanValidator.validasi(tokens, new SinglyLinkedList<>());
        assertFalse(res.isValid());
        assertTrue(res.getPesanKesalahan().contains("Format tanggalPutusan tidak valid"));
    }

    @Test
    void beratNegatifDitolak() {
        SinglyLinkedList<String> tokens = createValid21Fields();
        tokens.remove(6);
        tokens.insert(6, "-2.5");
        PutusanValidator.ValidationResult res = PutusanValidator.validasi(tokens, new SinglyLinkedList<>());
        assertFalse(res.isValid());
        assertTrue(res.getPesanKesalahan().contains("beratBarangBuktiGram tidak boleh negatif"));
    }

    @Test
    void pidanaMatiDenganTuntutanBulanDitolak() {
        SinglyLinkedList<String> tokens = createValid21Fields();
        // Index 9: tuntutanBulan = 48
        // Index 17: tuntutanJenis = pidana_mati
        tokens.remove(17);
        tokens.insert(17, "pidana_mati");
        PutusanValidator.ValidationResult res = PutusanValidator.validasi(tokens, new SinglyLinkedList<>());
        assertFalse(res.isValid());
        assertTrue(res.getPesanKesalahan().contains("pidana_mati tidak boleh memiliki nilai tuntutanBulan"));
    }

    @Test
    void parserMenanganiTandaKutipDanKoma() {
        String csvLine = "PUTUSAN-001,503/Pid.B/2013/PN.Jkt.Sel,\"PN Jakarta, Selatan\",2013-06-13,T-001,\"Sabu, Ekstasi\",4.7855,Pasal 114,Pasal 114 UU 35,156.0,132,800000000,\"5 klip, uang \"\"Rp200.000\"\"\",Memberatkan,Meringankan,p1-10,PARSIAL,pidana_penjara_bulan,,notes,netto";
        SinglyLinkedList<String> parsed = CsvParser.parseBaris(csvLine);
        assertEquals(21, parsed.size());
        assertEquals("PN Jakarta, Selatan", parsed.get(2));
        assertEquals("Sabu, Ekstasi", parsed.get(5));
        assertEquals("5 klip, uang \"Rp200.000\"", parsed.get(12));
    }
}

