# ADR 001: Pemilihan Struktur Data (Linked List + Stack) dan Pemetaan Stack ke StrukturData<T>

## Status
Diterima

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

## Alasan Pemilihan Struktur Data

**SinglyLinkedList<T> sebagai struktur utama**
- Data perkara sering ditambah dan dihapus; pada linked list operasi di depan (`addFirst`) dan di belakang (`addLast`, berkat pointer `tail`) berjalan O(1) tanpa menggeser elemen.
- Tidak perlu alokasi ulang atau penyalinan seperti array saat ukuran berubah.
- Konsekuensi: akses berdasarkan indeks (`get`, `insert`, `remove` di tengah) dan pencarian memerlukan penelusuran O(n).

**Stack<T> sebagai struktur riwayat audit**
- Riwayat operasi ditampilkan dari yang terbaru ke yang terlama, sesuai sifat LIFO.
- `push` dan `pop` berjalan O(1) dengan satu pointer `top`.
- Dipisah dari list utama agar log audit tidak mengganggu urutan data perkara.

## Keputusan Desain
`Stack<T>` diimplementasikan menggunakan linked nodes dengan satu pointer `top`.
Agar seragam dan dapat diperlakukan sebagai `StrukturData<T>`, `Stack<T>` mengimplementasikan `StrukturData<T>`.

### Pemetaan Operasi
1. `add(T item)` dipetakan langsung ke `push(item)`.
2. `size()`, `isEmpty()`, `clear()`, dan `contains(T item)` mengikuti semantik koleksi standar.
3. Operasi inti Stack disediakan secara eksplisit: `push(T item)`, `pop()`, `peek()`, `searchPreserve(Kriteria<T>)`, `restore(Stack<T>)`, dan `validateInvariant()`.

### Alasan Penyimpangan dan Konsekuensi
Secara konsep, Stack hanya mengizinkan akses pada elemen teratas (`top`) dengan aturan LIFO dan tidak mengenal akses acak berbasis indeks. Namun antarmuka `StrukturData<T>` mensyaratkan `T get(int index)` dan `T remove(int index)`.

Untuk memenuhi kontrak tersebut tanpa merusak integritas Stack:
1. Indeks diartikan secara logis dari puncak stack: `index = 0` adalah elemen `top`, dan `index = size - 1` adalah elemen dasar.
2. `remove(0)` setara dengan `pop()`.
3. Akses untuk `index > 0` pada `get(index)` dan `remove(index)` dilakukan melalui penelusuran sekuensial dari `top`.
4. **Kompleksitas**: operasi tersebut O(n), bukan O(1).
5. **Rekomendasi penggunaan**: pada logika bisnis (seperti audit log), pemanggil sebaiknya tetap memakai operasi asli Stack (`push`, `pop`, `peek`) agar O(1) dan prinsip LIFO terjaga.