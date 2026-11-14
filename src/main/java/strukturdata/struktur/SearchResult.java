package strukturdata.struktur;

/** Hasil pencarian: elemen (atau null jika tidak ketemu) dan jumlah langkah. */
public class SearchResult<T> {
    private final T elemen;
    private final int langkah;

    public SearchResult(T elemen, int langkah) {
        this.elemen = elemen;
        this.langkah = langkah;
    }

    public T getElemen() { return elemen; }
    public int getLangkah() { return langkah; }
    public boolean ditemukan() { return elemen != null; }

    @Override
    public String toString() {
        return "SearchResult{elemen=" + elemen + ", langkah=" + langkah + "}";
    }
}