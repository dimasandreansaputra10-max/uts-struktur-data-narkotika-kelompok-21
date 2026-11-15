# ADR 002: Strategi Pencarian, Pengurutan, dan Salinan List

## Status
Diterima

## Konteks
Aplikasi mengelola 50 perkara di `SinglyLinkedList<PutusanNarkotika>` buatan sendiri. Kebutuhan: mencari perkara, memfilter dengan beberapa kriteria, dan mengurutkan berdasarkan vonis tanpa mengubah urutan list utama.

## Keputusan

### 1. Pencarian: linear search
- Pencarian berdasarkan `caseId` atau `nomorPerkara` menelusuri node dari `head` satu per satu.
- Hasil berupa `SearchResult<T>` yang memuat elemen (atau `null`) dan jumlah langkah.
- Linked list tidak mendukung akses acak dan data tidak terurut menurut kunci pencarian, sehingga binary search tidak dipakai.
- Kompleksitas waktu: terbaik O(1), rata-rata dan terburuk O(n).

### 2. Pengurutan: Insertion Sort buatan sendiri
- Pengurutan vonis (ascending dan descending) memakai Insertion Sort tulisan sendiri, tanpa `Collections.sort`, `Arrays.sort`, atau `stream`.
- Pengurutan dijalankan pada salinan list; urutan list utama tidak berubah.
- Kompleksitas waktu: terbaik O(n), rata-rata dan terburuk O(n²). Memori tambahan O(n) untuk salinan.
- Insertion Sort dipilih karena sederhana dan cukup untuk n = 50. Merge Sort (O(n log n)) menjadi pilihan jika data jauh lebih besar.

### 3. Salinan list pada filter dan sort
- Filter gabungan mengembalikan `SinglyLinkedList` baru; list asal tidak dimutasi.
- Objek `PutusanNarkotika` tidak digandakan (shallow copy).
- Kolom kosong (misalnya berat barang bukti) tidak dianggap 0 dan dilewati pada filter berat.

## Pencacah
- Pencarian mencatat jumlah langkah, ditampilkan di menu dan di output test.
- Pencacah operasi untuk analisis kompleksitas: [CEK: tulis nama pencacah yang ada di kode, atau hapus baris ini jika belum ada].
- Detail implementasi sort (manipulasi pointer atau cara lain): [CEK: sesuaikan dengan kode `PutusanSortService`].

## Konsekuensi
- List utama terlindungi dari efek samping; `validateInvariant()` valid pada list utama dan salinan.
- Biaya memori tambahan O(n) untuk setiap filter dan sort.