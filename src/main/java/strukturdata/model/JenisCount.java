package strukturdata.model;

/**
 * Model data pasangan nama jenis narkotika dan jumlah kemunculannya.
 * Digunakan untuk agregasi distribusi frekuensi tanpa memakai HashMap.
 */
public class JenisCount {
    private final String nama;
    private int jumlah;

    public JenisCount(String nama, int jumlah) {
        this.nama = nama;
        this.jumlah = jumlah;
    }

    public String getNama() {
        return nama;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void tambahJumlah(int delta) {
        this.jumlah += delta;
    }

    public void increment() {
        this.jumlah++;
    }

    @Override
    public String toString() {
        return nama + ": " + jumlah;
    }
}
