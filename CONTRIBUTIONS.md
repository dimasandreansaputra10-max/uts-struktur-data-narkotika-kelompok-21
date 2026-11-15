# Log Kontribusi Pengerjaan (CONTRIBUTIONS.md)

Proyek ini diselesaikan secara mandiri oleh:
- **Nama**: Dimas Andrean Saputra
- **Nim**: 202510370110157
- **Kelompok**: 21 (Dikerjakan Mandiri)
- **Mata Kuliah**: Struktur Data, Universitas Muhammadiyah Malang

---

## Log Riwayat Tahapan Pengerjaan

Pekerjaan dibagi menjadi 8 tahap. Pada repositori GitHub, tahap 1-7 (kode dan test) digabung dalam Pull Request #1 (`feature/struktur-data`). Pull Request #2 dan #3 memuat dokumentasi, dataset, dan perbaikan link. Tag rilis: `uts-v1.0`.

| Tahap | Deskripsi Pekerjaan | Komponen yang Dihasilkan / Diuji | Status |
|:---:|:---|:---|:---:|
| **1** | Fondasi antarmuka dan linked list generik | `StrukturData<T>`, `Kriteria<T>`, `SearchResult<T>`, `SinglyLinkedList<T>`, `SinglyLinkedListTest` (10 test) | Selesai (ter-merge) |
| **2** | Stack mandiri (LIFO) dan ADR 001 | `Stack<T>` (`push`, `pop`, `peek`, `searchPreserve`, `restore`, `validateInvariant`), `StackTest` (7 test), `docs/adr/001-pemilihan-struktur-data.md` | Selesai (ter-merge) |
| **3** | Model 21 kolom, validator, dan impor CSV | `PutusanNarkotika` (21 atribut, tipe nullable), `AuditEntry`, `CsvParser` manual, `PutusanValidator`, `CsvImporter`, `CsvImporterTest`, `PutusanValidatorTest` (9 test) | Selesai (ter-merge) |
| **4** | Layanan manipulasi data dan pencarian linear | `PutusanService` (tampil, tambah di awal/akhir/tengah, hapus berdasarkan indeks/caseId, cari dengan pencacah langkah), `PutusanServiceTest` (9 test) | Selesai (ter-merge) |
| **5** | Filter gabungan dan agregasi tanpa HashMap | `JenisCount`, `FilterAggregationService` (filter multi-kriteria pada list baru, agregasi statistik, distribusi jenis narkotika dengan pemecah `;`), `FilterAggregationServiceTest` (7 test) | Selesai (ter-merge) |
| **6** | Pengurutan vonis mandiri pada salinan list | `PutusanSortService` (Insertion Sort buatan sendiri pada salinan list, ascending dan descending), `PutusanSortServiceTest` (4 test) | Selesai (ter-merge) |
| **7** | Audit dengan Stack, ekspor CSV, dan menu konsol | `AuditService` (memakai Stack), `CsvExporter` (escape koma dan kutip), `Main` (menu konsol), `CsvExporterTest`, `AuditServiceTest` (3 test) | Selesai (ter-merge) |
| **8** | Uji skenario dan dokumentasi akhir | `ScenarioTest` (7 test, skenario Bagian 4.1), `docs/adr/002-strategi-pencarian-pengurutan.md`, `docs/trace/pointer-trace.md`, `docs/data-provenance.csv`, `README.md`, `CONTRIBUTIONS.md`, `AI_USAGE.md` | Selesai (ter-merge) |

---

## Pull Request di GitHub

| PR | Branch | Isi |
|:---:|:---|:---|
| #1 | `feature/struktur-data` | Konfigurasi Maven dan `.gitignore`, struktur data, model, impor CSV, layanan, menu konsol, dan test (tahap 1-7) |
| #2 | `docs/finalisasi` | Finalisasi dokumen |
| #3 | `docs/perbaikan-readme` | Dokumentasi, dataset, dan perbaikan link `README.md` |

Catatan: checklist menganjurkan satu PR per item. Karena dikerjakan mandiri, beberapa tahap digabung dalam satu PR.

---

## Pengujian Otomatis

`mvn clean test`: **56 test, 0 gagal, 0 error, BUILD SUCCESS**.

---

## Penggunaan AI

Sebagian kode dan dokumen dibuat dengan bantuan AI (Claude). Seluruh kode dibaca, dijalankan, dan diuji sendiri oleh penulis. Rincian penggunaan AI ada di `AI_USAGE.md`.