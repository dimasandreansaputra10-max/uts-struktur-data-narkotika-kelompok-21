package strukturdata.struktur;

/** Kontrak umum struktur data generik (Bagian 3.2). */
public interface StrukturData<T> {
    void add(T item);
    T get(int index);
    T remove(int index);
    int size();
    boolean isEmpty();
    boolean contains(T item);
    void clear();
}