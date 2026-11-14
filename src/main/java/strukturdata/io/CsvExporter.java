package strukturdata.io;

import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Eksportir data putusan perkara narkotika ke format berkas CSV.
 * Menerapkan standar RFC 4180: mengapit teks yang memuat koma, kutip ganda, atau baris baru,
 * serta me-reka kutip ganda menjadi ganda ("").
 */
public class CsvExporter {

    private static final String HEADER = "caseId,nomorPerkara,pengadilan,tanggalPutusan,terdakwaKode,jenisNarkotika,beratBarangBuktiGram,pasalDakwaan,pasalTerbukti,tuntutanBulan,vonisBulan,dendaRupiah,barangBukti,keadaanMemberatkan,keadaanMeringankan,sourcePages,bentukDakwaan,tuntutanJenis,dendaCatatan,notes,beratBasis";

    /**
     * Mengekspor list putusan ke berkas target CSV.
     *
     * @param list data putusan
     * @param targetFilePath path berkas tujuan
     * @throws IOException jika terjadi kegagalan penulisan I/O
     */
    public static void exportCsv(SinglyLinkedList<PutusanNarkotika> list, String targetFilePath) throws IOException {
        if (list == null) return;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetFilePath), StandardCharsets.UTF_8))) {

            writer.write(HEADER);
            writer.write("\n");

            for (int i = 0; i < list.size(); i++) {
                PutusanNarkotika p = list.get(i);
                if (p == null) continue;

                StringBuilder sb = new StringBuilder();
                appendField(sb, p.getCaseId(), true);
                appendField(sb, p.getNomorPerkara(), true);
                appendField(sb, p.getPengadilan(), true);
                appendField(sb, p.getTanggalPutusan() != null ? p.getTanggalPutusan().toString() : "", true);
                appendField(sb, p.getTerdakwaKode(), true);
                appendField(sb, p.getJenisNarkotika(), true);
                appendField(sb, p.getBeratBarangBuktiGram() != null ? p.getBeratBarangBuktiGram().toString() : "", true);
                appendField(sb, p.getPasalDakwaan(), true);
                appendField(sb, p.getPasalTerbukti(), true);
                appendField(sb, p.getTuntutanBulan() != null ? p.getTuntutanBulan().toString() : "", true);
                appendField(sb, p.getVonisBulan() != null ? p.getVonisBulan().toString() : "", true);
                appendField(sb, p.getDendaRupiah() != null ? p.getDendaRupiah().toString() : "", true);
                appendField(sb, p.getBarangBukti(), true);
                appendField(sb, p.getKeadaanMemberatkan(), true);
                appendField(sb, p.getKeadaanMeringankan(), true);
                appendField(sb, p.getSourcePages(), true);
                appendField(sb, p.getBentukDakwaan(), true);
                appendField(sb, p.getTuntutanJenis(), true);
                appendField(sb, p.getDendaCatatan(), true);
                appendField(sb, p.getNotes(), true);
                appendField(sb, p.getBeratBasis(), false); // kolom terakhir tanpa koma penutup

                writer.write(sb.toString());
                writer.write("\n");
            }
        }
    }

    private static void appendField(StringBuilder sb, String value, boolean appendComma) {
        if (value == null) {
            value = "";
        }

        boolean butuhKutip = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");

        if (butuhKutip) {
            sb.append('\"');
            sb.append(value.replace("\"", "\"\""));
            sb.append('\"');
        } else {
            sb.append(value);
        }

        if (appendComma) {
            sb.append(',');
        }
    }
}
