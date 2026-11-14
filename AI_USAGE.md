# Catatan Penggunaan AI (AI_USAGE.md)

Dokumen ini mencatat penggunaan AI (model: Claude, Anthropic) dan verifikasi manual yang dilakukan oleh mahasiswa selama pengerjaan UTS Struktur Data. AI dipakai sebagai alat bantu belajar dan debugging, bukan pengganti pemahaman.

---

## 1. Memahami Maksud Soal
- **Dibantu oleh AI**:
  - Menjelaskan batasan teknis soal: satu struktur utama buatan sendiri, struktur sekunder, larangan koleksi `java.util` sebagai penyimpan inti, dan kewajiban `validateInvariant()`.
  - Menjelaskan 16 atribut data putusan dan letak masing-masing pada format putusan pidana khusus (pedoman Mahkamah Agung).
  - Menjelaskan 7 skenario uji wajib dan 9 fitur wajib.
  - Mengusulkan aturan agregasi berat barang bukti (berat bersih, tidak dikonversi) serta penanganan nilai kosong dan putusan bebas/lepas pada rata-rata vonis.
- **Diperiksa / Diverifikasi Manual**:
  - Membaca ulang soal dan mencocokkan setiap penjelasan AI dengan teks soal.
  - Memutuskan sendiri aturan agregasi yang dipakai kelompok.

---

## 2. Gambaran Algoritma dan Rancangan Program
- **Dibantu oleh AI**:
  - Membandingkan pilihan struktur utama dan menyarankan singly linked list dengan pointer `head` dan `tail`.
  - Menyarankan stack berbasis array untuk struktur sekunder (audit).
  - Menjelaskan alur pencarian linear dengan penghitung langkah.
  - Menjelaskan alur merge sort pada linked list (pemecahan dengan slow/fast pointer dan penyambungan ulang node).
  - Menjelaskan perubahan pointer saat hapus elemen pertama, tengah, dan terakhir.
  - Menjelaskan rancangan parser CSV yang menangani tanda kutip dan koma di dalam sel.
  - Memberi gambaran analisis kompleksitas tiap operasi.
- **Diperiksa / Diverifikasi Manual**:
  - Memilih sendiri struktur dan algoritma, lalu menuliskan alasannya di ADR.
  - Menelusuri perubahan pointer secara manual dan mencocokkannya dengan hasil program.

---

## 3. Menemukan Kode yang Error
- **Dibantu oleh AI**:
  - Menunjukkan penyebab error kompilasi pada kode tes: pemanggilan `assertEquals` yang ambigu antara `int` dan `Integer`.
  - Menunjukkan penyebab tes skenario 7 gagal: data uji tanggal rusak tidak sesuai dengan tanggal yang dihasilkan rumus data uji, sehingga satu record yang seharusnya ditolak ikut diterima.
- **Diperiksa / Diverifikasi Manual**:
  - Memahami penyebab error sebelum memperbaikinya.
  - Menjalankan ulang tes setelah perbaikan dan memastikan hasilnya sesuai harapan.

---

## 4. Membantu Kode Agar Dapat Berjalan
- **Dibantu oleh AI**:
  - Menyusun draf awal kode (`StrukturData`, `SinglyLinkedList`, `ArrayStack`, `CsvParser`, `PutusanService`, `Main`, dan tes JUnit) serta memperbaikinya sampai dapat dikompilasi dan semua tes lulus pada lingkungan uji AI.
  - Menyarankan konfigurasi `pom.xml` untuk Java dan JUnit 5.
- **Diperiksa / Diverifikasi Manual**:
  - Menjalankan `mvn test` sendiri di komputer kelompok dan memastikan hasilnya hijau.
  - Memastikan tidak ada `ArrayList`, `LinkedList`, `Stack`, `Vector`, `Queue`, `Deque`, `PriorityQueue`, iterator, atau stream sebagai penyimpan inti.
  - Memastikan `validateInvariant()` dipanggil pada setiap tes.
  - Membaca dan memahami kode sehingga setiap anggota dapat menjelaskan cara kerjanya.

---
## 5. Membuat File Mentahan CSV
- **Dibantu oleh AI**:
  - Membuat templat berkas `data/putusan-50.csv` (header 16 kolom sesuai urutan yang dibaca parser) dan `docs/data-provenance.csv` (header: `case_id, field_name, value, source_file, source_page, extractor, reviewer, status`).
  - Menjelaskan aturan isi tiap kolom: format tanggal `yyyy-MM-dd`, angka tanpa pemisah ribuan, pemisah `;` untuk butir majemuk, tanda kutip ganda untuk sel yang mengandung koma, dan nilai tidak ditemukan dikosongkan (tidak diperkirakan).
  - [ISI SESUAI KENYATAAN: sebutkan apakah AI juga dipakai untuk membantu mengekstrak isi putusan ke baris CSV, putusan mana saja, dan alat AI yang dipakai. Jika tidak dipakai, tulis: "AI tidak dipakai untuk mengekstrak isi putusan."]
- **Diperiksa / Diverifikasi Manual**:
  - Mencocokkan setiap nilai pada CSV dengan putusan asli, halaman demi halaman, dan mencatat sumbernya di `docs/data-provenance.csv`.
  - Mengubah `status` dari `needs_review` menjadi `verified` hanya setelah diperiksa oleh anggota lain (reviewer).
  - Mengosongkan nilai yang tidak ditemukan dan tidak memperkirakannya.
  - Menjalankan fitur impor untuk memastikan 50 record terbaca dan record tidak valid ditolak dengan pesan error.

---

## 6. Menulis Prompt
- **Dibantu oleh AI**:
  - Membantu menyusun dan merapikan prompt yang kami pakai, misalnya prompt untuk memahami soal, meminta gambaran algoritma, menjelaskan pesan error, dan meminta perbaikan kode.
  - Memberi saran agar prompt memuat konteks (soal, batasan teknis, kode, dan pesan error) supaya jawaban lebih tepat.
- **Diperiksa / Diverifikasi Manual**:
  - Memilih dan mengubah prompt sesuai kebutuhan kami.
  - Menyimpan contoh prompt asli yang kami pakai pada `docs/ai/prompts.md` [buat berkas ini dan salin prompt asli Anda].
  - Tetap memeriksa semua jawaban AI sebelum dipakai.

---