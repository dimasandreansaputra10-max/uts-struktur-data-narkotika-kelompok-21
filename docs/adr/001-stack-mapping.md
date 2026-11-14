# ADR 001: Pemetaan Stack<T> ke StrukturData<T> dan Alasan Penyimpangan

## Status
Diterima (Draf - PR 2)

## Konteks
Aplikasi pengolahan putusan narkotika PN Jakarta Selatan menggunakan dua struktur data buatan sendiri:
1. `SinglyLinkedList<T>` sebagai struktur data utama.
2. `Stack<T>` sebagai struktur data sekunder untuk riwayat audit operasi pengguna (LIFO).

Terdapat antarmuka umum `StrukturData<T>` (berdasarkan spesifikasi soal Bagian 3.2):
```java
public interface StrukturData<T> {
    void add(T item);
    T get(int index);
    T remove(int index);
    int size();
    boolean isEmpty();
    boolean contains(T item);
    void clear();
}
```

## Keputusan Desain
`Stack<T>` diimplementasikan menggunakan linked nodes dengan satu pointer `top`.
Agar seragam dan dapat diperlakukan sebagai `StrukturData<T>`, `Stack<T>` mengimplementasikan `StrukturData<T>`.

### Pemetaan Operasi
1. `add(T item)` dipetakan langsung ke `push(item)`.
2. `size()`, `isEmpty()`, `clear()`, dan `contains(T item)` mengikuti semantik koleksi standar.
3. Operasi inti Stack disediakan secara eksplisit: `push(T item)`, `pop()`, `peek()`, `searchPreserve(Kriteria<T>)`, `restore(Stack<T>)`, dan `validateInvariant()`.

### Alasan Penyimpangan dan Konsekuensi
Secara konsep teoritis, struktur data Stack hanya mengizinkan akses pada elemen teratas (`top`) dengan aturan LIFO (Last-In-First-Out) dan tidak mengenal akses acak berbasis indeks (random index access).
Namun, antarmuka `StrukturData<T>` mensyaratkan:
- `T get(int index)`
- `T remove(int index)`

Untuk memenuhi kontrak antarmuka tersebut tanpa merusak integritas Stack:
1. Indeks diartikan secara logis dari puncak stack:
   - `index = 0` merepresentasikan elemen `top`.
   - `index = size - 1` merepresentasikan elemen dasar stack.
2. `remove(0)` setara persis dengan pemanggilan `pop()`.
3. Akses untuk `index > 0` pada `get(index)` dan `remove(index)` diimplementasikan melalui sequential traversal dari pointer `top`.
4. **Kompleksitas**: Operasi ini beroperasi dalam waktu **O(n)**, bukan O(1).
5. **Rekomendasi Penggunaan**: Pada logika bisnis (seperti audit log), pemanggil kode disarankan tetap memprioritaskan operasi asli Stack (`push`, `pop`, `peek`) demi efisiensi O(1) dan menjaga kemurnian prinsip LIFO.

