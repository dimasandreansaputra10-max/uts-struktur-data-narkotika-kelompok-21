# Log Kontribusi Pengerjaan (CONTRIBUTIONS.md)

Proyek ini diselesaikan secara mandiri oleh:
- **Nama**: Dimas Andrean Saputra
- **Kelompok**: 21 (Dikerjakan Mandiri)
- **Mata Kuliah**: Struktur Data, Universitas Muhammadiyah Malang

---

## Log Riwayat Tahapan Pengerjaan (Pull Requests / Milestones)

| PR / Tahap | Deskripsi Pekerjaan | Komponen yang Dihasilkan / Diuji | Status |
|:---:|:---|:---|:---:|
| **PR 1** | Pondasi antarmuka dan linked list generik | `StrukturData<T>`, `Kriteria<T>`, `SearchResult<T>`, `SinglyLinkedList<T>`, `SinglyLinkedListTest` (10 test) | SELESAI |
| **PR 2** | Implementasi Stack mandiri (LIFO) & ADR 001 | `Stack<T>` (`push`, `pop`, `peek`, `searchPreserve`, `restore`, `validateInvariant`), `StackTest` (7 test), `docs/adr/001-stack-mapping.md` | SELESAI |
| **PR 3** | Model 21 kolom, Validator, & CSV Importer | `PutusanNarkotika` (21 atribut, tipe nullable), `AuditEntry`, `CsvParser` manual, `PutusanValidator`, `CsvImporter`, `CsvImporterTest`, `PutusanValidatorTest` (9 test) | SELESAI |
| **PR 4** | Service layer manipulasi & pencarian linier | `PutusanService` (tampil, tambah di awal/akhir/tengah, hapus indeks/caseId, cari dengan pencacah langkah), `PutusanServiceTest` (9 test) | SELESAI |
| **PR 5** | Filter gabungan & Agregasi tanpa HashMap | `JenisCount`, `FilterAggregationService` (filter multi-kriteria pada list baru, agregasi statistik, distribusi narkotika dengan pemecah `;`), `FilterAggregationServiceTest` (7 test) | SELESAI |
| **PR 6** | Pengurutan vonis mandiri pada salinan list | `PutusanSortService` (Insertion Sort mandiri pada salinan list, urut ascending & descending), `PutusanSortServiceTest` (4 test) | SELESAI |
| **PR 7** | Audit log stack, eksportir CSV, & Main UI | `AuditService` (terintegrasi Stack), `CsvExporter` (escape koma dan kutip RFC 4180), `Main` (konsol tabel terformat rapi), `CsvExporterTest`, `AuditServiceTest` (3 test) | SELESAI |
| **PR 8** | Uji skenario komprehensif & dokumentasi final | `ScenarioTest` (7 skenario Bagian 4.1, 7 test), `docs/adr/002-list-cloning-sorting.md`, `docs/trace/pointer-trace.md`, `README.md`, `CONTRIBUTIONS.md`, `AI_USAGE.md` | SELESAI |

**Total Pengujian Otomatis**: 56 unit & integration tests berhasil dieksekusi dengan status 100% BUILD SUCCESS.
