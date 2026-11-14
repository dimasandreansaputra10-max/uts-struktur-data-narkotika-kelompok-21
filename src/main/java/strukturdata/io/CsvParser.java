package strukturdata.io;

import strukturdata.struktur.SinglyLinkedList;

/**
 * Parser CSV manual karakter per karakter yang menangani tanda kutip ganda (RFC 4180).
 * Tidak menggunakan String.split(",") sesuai Aturan Keras No. 7.
 */
public class CsvParser {

    /**
     * Mem-parsing satu baris teks CSV menjadi list token String.
     * Menangani field yang diapit tanda kutip ganda dan escape kutip ganda ("").
     *
     * @param baris baris CSV yang akan diuraikan
     * @return SinglyLinkedList berisi String dari masing-masing kolom
     */
    public static SinglyLinkedList<String> parseBaris(String baris) {
        SinglyLinkedList<String> fields = new SinglyLinkedList<>();
        if (baris == null) {
            return fields;
        }

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int len = baris.length();

        for (int i = 0; i < len; i++) {
            char c = baris.charAt(i);

            if (c == '\"') {
                if (inQuotes && i + 1 < len && baris.charAt(i + 1) == '\"') {
                    // Escaped quote: "" -> "
                    current.append('\"');
                    i++; // lewati kutip kedua
                } else {
                    // Toggle quote mode
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Akhir satu field
                fields.addLast(current.toString().trim());
                current.setLength(0);
            } else if (c == '\r') {
                // Abaikan CR jika ada di akhir baris
                continue;
            } else {
                current.append(c);
            }
        }
        // Masukkan field terakhir
        fields.addLast(current.toString().trim());

        return fields;
    }
}

