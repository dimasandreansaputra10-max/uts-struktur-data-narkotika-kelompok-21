package strukturdata.struktur;

/** Singly linked list generik buatan sendiri dengan head, tail, size, stepCount. */
public class SinglyLinkedList<T> implements StrukturData<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;
    private int stepCount;

    // ---------- penambahan ----------
    public void addFirst(T item) {
        Node<T> n = new Node<>(item);
        n.next = head;
        head = n;
        if (tail == null) tail = n;
        size++;
    }

    public void addLast(T item) {
        Node<T> n = new Node<>(item);
        if (tail == null) {          // list kosong
            head = n;
        } else {
            tail.next = n;
        }
        tail = n;
        size++;
    }

    /** Menyisipkan item sehingga berada pada posisi index (0..size). */
    public void insert(int index, T item) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("Indeks: " + index + ", size: " + size);
        if (index == 0) { addFirst(item); return; }
        if (index == size) { addLast(item); return; }
        Node<T> prev = nodeAt(index - 1);
        Node<T> n = new Node<>(item);
        n.next = prev.next;
        prev.next = n;
        size++;
    }

    @Override
    public void add(T item) { addLast(item); }

    // ---------- akses ----------
    @Override
    public T get(int index) {
        cekIndeks(index);
        return nodeAt(index).data;
    }

    private Node<T> nodeAt(int index) {
        Node<T> cur = head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur;
    }

    private void cekIndeks(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Indeks: " + index + ", size: " + size);
    }

    // ---------- penghapusan ----------
    @Override
    public T remove(int index) {
        cekIndeks(index);
        T removed;
        if (index == 0) {
            removed = head.data;
            head = head.next;
            if (head == null) tail = null;   // list jadi kosong
        } else {
            Node<T> prev = nodeAt(index - 1);
            Node<T> target = prev.next;
            removed = target.data;
            prev.next = target.next;
            if (target == tail) tail = prev; // hapus terakhir: tail mundur
        }
        size--;
        return removed;
    }

    // ---------- pencarian ----------
    /** Linear search; stepCount dihitung per node yang dikunjungi. */
    public SearchResult<T> search(Kriteria<T> kriteria) {
        stepCount = 0;
        Node<T> cur = head;
        while (cur != null) {
            stepCount++;
            if (kriteria.cocok(cur.data)) return new SearchResult<>(cur.data, stepCount);
            cur = cur.next;
        }
        return new SearchResult<>(null, stepCount);
    }

    @Override
    public boolean contains(T item) {
        stepCount = 0;
        Node<T> cur = head;
        while (cur != null) {
            stepCount++;
            if (item == null ? cur.data == null : item.equals(cur.data)) return true;
            cur = cur.next;
        }
        return false;
    }

    // ---------- lain-lain ----------
    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }
    public int getStepCount() { return stepCount; }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
        stepCount = 0;
    }

    /** Melempar IllegalStateException jika invariant dilanggar. */
    public void validateInvariant() {
        if (size < 0) throw new IllegalStateException("size negatif");
        if (size == 0) {
            if (head != null || tail != null)
                throw new IllegalStateException("List kosong tetapi head/tail tidak null");
            return;
        }
        if (head == null || tail == null)
            throw new IllegalStateException("List tidak kosong tetapi head/tail null");
        if (tail.next != null)
            throw new IllegalStateException("tail.next harus null");
        int count = 0;
        Node<T> cur = head;
        Node<T> last = null;
        while (cur != null) {
            count++;
            if (count > size) throw new IllegalStateException("Jumlah node melebihi size (mungkin siklus)");
            last = cur;
            cur = cur.next;
        }
        if (count != size)
            throw new IllegalStateException("Jumlah node " + count + " != size " + size);
        if (last != tail)
            throw new IllegalStateException("node terakhir bukan tail");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> cur = head;
        while (cur != null) {
            sb.append(cur.data);
            if (cur.next != null) sb.append(", ");
            cur = cur.next;
        }
        return sb.append("]").toString();
    }
}