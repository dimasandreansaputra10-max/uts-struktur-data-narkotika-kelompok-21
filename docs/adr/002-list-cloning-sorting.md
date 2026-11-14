# ADR 002: Strategi Salinan List (List Cloning) pada Operasi Filter dan Sorting

## Status
Diterima (PR 8)

## Konteks
Aplikasi Sistem Informasi Putusan Pengadilan Negeri Jakarta Selatan mengelola daftar 50 perkara narkotika di memori utama menggunakan struktur data mandiri `SinglyLinkedList<PutusanNarkotika>`.
Kebutuhan fungsional mensyaratkan:
1. Pengguna dapat melakukan filter multi-kriteria (jenis narkotika, berat, pasal terbukti, rentang vonis).
2. Pengguna dapat mengurutkan perkara berdasarkan vonis penjara secara ascending maupun descending.
3. **Aturan Keras**: Urutan asli data pada list utama tidak boleh berubah, dan list utama tidak boleh rusak atau terfragmentasi setelah operasi filter maupun pengurutan dijalankan.

## Keputusan Desain
1. **Filter Gabungan**:
   - Fungsi `filter` pada `FilterAggregationService` mengembalikan sebuah instance baru `SinglyLinkedList<PutusanNarkotika>`.
   - Node-node baru dibentuk untuk menampung referensi objek perkara yang lolos kriteria.
   - List asal sama sekali tidak mengalami mutasi pointer (`head`, `tail`, atau `next`).

2. **Pengurutan (Sorting)**:
   - Fungsi `urutkanBerdasarkanVonisAscending` dan `urutkanBerdasarkanVonisDescending` pada `PutusanSortService` diawali dengan pemanggilan eksplisit method `salinList(source)`.
   - `salinList` membentuk daftar simpul (*linked nodes*) baru yang merujuk pada elemen-elemen data asli.
   - Algoritma Insertion Sort beroperasi secara eksklusif memanipulasi pointer `next`, `head`, dan `tail` pada salinan list tersebut.
   - Hasil akhir yang dikembalikan adalah list terurut baru, sementara list asal tetap mempertahankan urutan kronologis data aslinya.

## Konsekuensi & Kompleksitas
- **Memori**: Membutuhkan alokasi memori tambahan $O(n)$ untuk node-node simpul pembungkus pada list salinan (objek `PutusanNarkotika` tidak digandakan / *shallow copy*, sehingga efisien).
- **Integritas**: List utama terlindungi dari *side-effects*, dan pemanggilan `validateInvariant()` pada list utama maupun salinan selalu bernilai valid.
