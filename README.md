# UTS Struktur Data: Sistem Informasi Putusan Narkotika PN Jakarta Selatan

## Identitas Mahasiswa
- **Nama**: Dimas Andrean Saputra
- **Mata Kuliah**: Struktur Data (Semester Ganjil 2026/2027)
- **Program Studi / Universitas**: Teknik Informatika, Universitas Muhammadiyah Malang
- **Kelompok**: 21 (Dikerjakan Mandiri)
- **Dosen Pengampu**: Galih W. Wicaksono

---

## Deskripsi Singkat
Aplikasi konsol berbasis Java berorientasi objek yang menyimpan, mengelola, menyaring, mengagregasikan, dan menganalisis 50 record putusan perkara tindak pidana narkotika dari **Pengadilan Negeri Jakarta Selatan**.

Aplikasi dibangun murni menggunakan **struktur data generik buatan sendiri** tanpa menggunakan struktur koleksi bawaan `java.util` (seperti `ArrayList`, `LinkedList`, `Stack`, `Queue`, `HashMap`, `HashSet`, `Iterator`, atau `Stream API`).

---

## Struktur Data Mandiri
| Peran | Struktur Data | Karakteristik & Penggunaan |
|:---|:---|:---|
| **Utama** | `SinglyLinkedList<T>` | Menyimpan 50 objek perkara (`PutusanNarkotika`) secara dinamis dengan pointer `head`, `tail`, `size`, serta penghitung langkah penelusuran (`stepCount`). |
| **Sekunder** | `Stack<T>` | Menyimpan seluruh riwayat log audit operasi pengguna secara LIFO (Last-In-First-Out) dengan fitur `searchPreserve` dan `restore`. |
| **Agregasi** | `JenisCount` | Model pasangan `(nama, jumlah)` untuk menghitung frekuensi distribusi narkotika tanpa memerlukan `HashMap`. |

### Analisis Kompleksitas Waktu & Pembuktian Langkah
| Operasi | Kompleksitas | Catatan / Pembuktian Langkah |
|:---|:---:|:---|
| `addFirst(item)` | $O(1)$ | Penyisipan langsung di simpul `head`. |
| `addLast(item)` | $O(1)$ | Penyisipan langsung menggunakan pointer `tail`. |
| `insert(index, item)` | $O(n)$ | Penelusuran traversal hingga simpul $index - 1$. |
| `remove(0)` | $O(1)$ | Penghapusan simpul `head`. |
| `remove(index)` | $O(n)$ | Penelusuran traversal hingga simpul target. |
| `search(kriteria)` | $O(n)$ | Pencarian linier: elemen pertama = 1 langkah; elemen ke-25 = 25 langkah; tidak ditemukan = $n$ langkah. |
| `push(item)` (Stack) | $O(1)$ | Penambahan simpul baru di `top`. |
| `pop()` (Stack) | $O(1)$ | Pengambilan simpul dari `top`. |
| `peek()` (Stack) | $O(1)$ | Pembacaan simpul di `top`. |
| `searchPreserve` (Stack) | $O(n)$ | Memeriksa elemen menggunakan stack sementara dan mengembalikan urutan LIFO semula. |
| `insertionSort` | $O(n^2)$ | Pengurutan vonis mandiri yang dieksekusi pada **salinan list** tanpa merusak list utama. |

---

## Panduan Menjalankan Aplikasi & Unit Test

### Prasyarat
- Java Development Kit (JDK 17 atau lebih baru)
- Apache Maven 3.6+

### 1. Menjalankan Seluruh Unit Test (56 Automated Tests)
```bash
mvn test
```

### 2. Mengompilasi dan Menjalankan Aplikasi Konsol Interaktif
```bash
mvn compile
mvn exec:java -Dexec.mainClass="strukturdata.Main"
```

---

## Menu Aplikasi Konsol
1. **Impor Data dari CSV**: Membaca 50 record dari `data/putusan.csv`. Baris tidak valid ditolak dan dicatat pada list error tanpa menghentikan program.
2. **Tampilkan Daftar Putusan**: Format tabel teks rapi dengan perataan kolom dan pemotongan teks panjang.
3. **Tambah Perkara Baru**: Pilihan posisi penambahan (di awal, akhir, atau indeks tertentu) dengan validasi keunikan `caseId`.
4. **Hapus Perkara**: Hapus berdasarkan `caseId` atau indeks posisi linked list.
5. **Cari Perkara**: Pencarian berdasarkan `caseId` atau `nomorPerkara` lengkap dengan pelaporan jumlah langkah penelusuran.
6. **Filter Gabungan Multi-Kriteria**: Penyaringan menurut jenis narkotika, rentang berat (memperhatikan basis `netto`/`brutto`), pasal terbukti, dan rentang vonis. Hasil disimpan dalam list baru.
7. **Agregasi Statistik & Distribusi Narkotika**: Total perkara, rata-rata vonis bulan, total denda rupiah, serta distribusi kemunculan jenis narkotika (memecah multi-value titik-koma `;`).
8. **Urutkan Perkara Berdasarkan Vonis**: Pengurutan ascending / descending pada salinan list.
9. **Tampilkan Riwayat Audit**: Menampilkan log seluruh operasi dari stack LIFO (terbaru ke terlama).
10. **Ekspor Data ke CSV**: Menulis kembali data ke file CSV standar RFC 4180 dengan penanganan kutip dan koma.
0. **Keluar**: Mengakhiri aplikasi secara bersih.

---

## Dokumentasi Tambahan
- [ADR 001 - Pemetaan Stack ke StrukturData](docs/adr/001-pemilihan-struktur-data.md)
- [ADR 002 - Strategi Salinan List pada Filter dan Sorting](docs/adr/002-strategi-pencarian-pengurutan.md)
- [Trace Pointer Manual Penghapusan Linked List](docs/trace/pointer-trace.md)
- [Catatan Penggunaan AI (AI_USAGE.md)](AI_USAGE.md)
- [Daftar Kontribusi Pengerjaan (CONTRIBUTIONS.md)](CONTRIBUTIONS.md)
