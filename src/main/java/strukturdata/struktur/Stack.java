package strukturdata.struktur;

import java.util.EmptyStackException;

/**
 * Struktur data Stack (LIFO) generik buatan sendiri.
 * Mengimplementasikan StrukturData<T> untuk keseragaman antarmuka.
 *
 * @param <T> tipe elemen yang disimpan
 */
public class Stack<T> implements StrukturData<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;
    private int stepCount;

    public Stack() {
        this.top = null;
        this.size = 0;
        this.stepCount = 0;
    }

    /**
     * Memasukkan elemen ke puncak stack (O(1)).
     */
    public void push(T item) {
        top = new Node<>(item, top);
        size++;
    }

    /**
     * Mengambil dan menghapus elemen dari puncak stack (O(1)).
     * Melempar EmptyStackException jika stack kosong.
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T item = top.data;
        top = top.next;
        size--;
        return item;
    }

    /**
     * Melihat elemen di puncak stack tanpa menghapusnya (O(1)).
     * Melempar EmptyStackException jika stack kosong.
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }

    /**
     * Mencari elemen berdasarkan kriteria dengan menjaga urutan stack tetap utuh.
     * Menggunakan stack bantuan internal untuk memindahkan elemen selama inspeksi,
     * lalu memulihkannya kembali (preserve).
     *
     * @param kriteria predikat pencarian
     * @return SearchResult berisi elemen yang ditemukan (atau null) dan jumlah langkah
     */
    public SearchResult<T> searchPreserve(Kriteria<T> kriteria) {
        stepCount = 0;
        Stack<T> temp = new Stack<>();
        T ditemukan = null;

        while (!this.isEmpty()) {
            stepCount++;
            T current = this.pop();
            temp.push(current);
            if (kriteria.cocok(current)) {
                ditemukan = current;
                break;
            }
        }

        // Pulihkan kembali elemen dari temp ke stack ini
        this.restore(temp);

        return new SearchResult<>(ditemukan, stepCount);
    }

    /**
     * Memulihkan elemen dari stack pembantu kembali ke stack ini.
     * Karena saat dipindahkan ke temp urutannya terbalik,
     * mem-pop dari temp dan push kembali ke stack ini akan mengembalikan urutan semula.
     *
     * @param tempStack stack pembantu yang berisi elemen
     */
    public void restore(Stack<T> tempStack) {
        if (tempStack == null) return;
        while (!tempStack.isEmpty()) {
            this.push(tempStack.pop());
        }
    }

    public int getStepCount() {
        return stepCount;
    }

    // =========================================================================
    // Implementasi interface StrukturData<T>
    // =========================================================================

    /**
     * Pemetaan method add(T item) ke operasi push(item).
     */
    @Override
    public void add(T item) {
        push(item);
    }

    /**
     * Mengakses elemen pada indeks logis dari puncak (indeks 0 = top).
     * Catatan: Operasi get berbasis indeks pada stack bersifat O(n) dan menyimpang
     * dari sifat murni LIFO demi memenuhi kontrak umum StrukturData<T>.
     */
    @Override
    public T get(int index) {
        cekIndeks(index);
        Node<T> cur = top;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.data;
    }

    /**
     * Menghapus elemen pada indeks logis dari puncak (indeks 0 = top/pop).
     * Jika index == 0, setara dengan pop(). Untuk index > 0, dilakukan penelusuran traversal.
     */
    @Override
    public T remove(int index) {
        cekIndeks(index);
        if (index == 0) {
            return pop();
        }
        Node<T> prev = top;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        Node<T> target = prev.next;
        T removed = target.data;
        prev.next = target.next;
        size--;
        return removed;
    }

    private void cekIndeks(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indeks: " + index + ", size: " + size);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(T item) {
        stepCount = 0;
        Node<T> cur = top;
        while (cur != null) {
            stepCount++;
            if (item == null ? cur.data == null : item.equals(cur.data)) {
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    @Override
    public void clear() {
        top = null;
        size = 0;
        stepCount = 0;
    }

    /**
     * Memvalidasi invarian struktur data Stack:
     * 1. size >= 0.
     * 2. Jika size == 0, maka top harus null.
     * 3. Jika size > 0, top tidak boleh null.
     * 4. Jumlah node yang ditelusuri dari top ke null harus persis sama dengan size.
     */
    public void validateInvariant() {
        if (size < 0) {
            throw new IllegalStateException("size stack negatif: " + size);
        }
        if (size == 0) {
            if (top != null) {
                throw new IllegalStateException("Stack kosong tetapi top != null");
            }
            return;
        }
        if (top == null) {
            throw new IllegalStateException("Stack tidak kosong tetapi top == null");
        }

        int count = 0;
        Node<T> cur = top;
        while (cur != null) {
            count++;
            if (count > size) {
                throw new IllegalStateException("Jumlah node melebihi size (kemungkinan terjadi siklus)");
            }
            cur = cur.next;
        }
        if (count != size) {
            throw new IllegalStateException("Jumlah node terhitung (" + count + ") != size (" + size + ")");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[top: ");
        Node<T> cur = top;
        while (cur != null) {
            sb.append(cur.data);
            if (cur.next != null) {
                sb.append(", ");
            }
            cur = cur.next;
        }
        sb.append("]");
        return sb.toString();
    }
}

