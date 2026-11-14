package strukturdata.struktur;

/** Pengganti Predicate agar tidak memakai java.util.function. */
public interface Kriteria<T> {
    boolean cocok(T item);
}