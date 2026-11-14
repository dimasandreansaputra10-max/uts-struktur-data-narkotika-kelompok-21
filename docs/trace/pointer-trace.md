# Trace Manual Perubahan Pointer SinglyLinkedList<T>

Dokumen ini mendokumentasikan tabel perubahan pointer (`head`, `tail`, `next`, `size`) pada tiga operasi kritis penghapusan elemen di `SinglyLinkedList<T>`:
1. Hapus elemen pertama (`remove(0)`)
2. Hapus elemen tengah (`remove(index)` di mana `0 < index < size - 1`)
3. Hapus elemen terakhir (`remove(size - 1)`)

---

## 1. Hapus Elemen Pertama (`remove(0)`)

Misalkan list awal memiliki 3 elemen: `[A -> B -> C]` ($size = 3$).

### Kondisi Sebelum Operasi
- `head` menunjuk Node(A)
- Node(A).next menunjuk Node(B)
- Node(B).next menunjuk Node(C)
- Node(C).next menunjuk `null`
- `tail` menunjuk Node(C)
- `size` = 3

### Langkah Eksekusi Kode (`remove(0)`)
```java
T removed = head.data;      // A
head = head.next;           // head sekarang menunjuk Node(B)
if (head == null) tail = null;
size--;                     // size menjadi 2
```

### Tabel Perubahan Pointer
| Variabel / Pointer | Sebelum `remove(0)` | Setelah `remove(0)` | Keterangan |
|---|---|---|---|
| `head` | Node(A) | Node(B) | `head` dimajukan ke `head.next` |
| Node(A).next | Node(B) | Node(B) (orphan) | Node(A) dilepas dari list utama |
| Node(B).next | Node(C) | Node(C) | Tidak berubah |
| Node(C).next | `null` | `null` | Tetap `null` |
| `tail` | Node(C) | Node(C) | Tidak berubah karena list tidak menjadi kosong |
| `size` | 3 | 2 | Berkurang 1 |

*Kasus khusus satu elemen*: Jika $size = 1$, setelah `head = head.next`, `head` menjadi `null`, dan percabangan `tail = null` dieksekusi sehingga list kembali kosong secara valid.

---

## 2. Hapus Elemen Tengah (`remove(1)`)

Misalkan list awal memiliki 3 elemen: `[A -> B -> C]` ($size = 3$), kita menghapus Node(B) pada indeks 1.

### Kondisi Sebelum Operasi
- `head` menunjuk Node(A)
- Node(A).next menunjuk Node(B)
- Node(B).next menunjuk Node(C)
- `tail` menunjuk Node(C)
- `size` = 3

### Langkah Eksekusi Kode (`remove(1)`)
```java
Node<T> prev = nodeAt(index - 1); // prev menunjuk Node(A)
Node<T> target = prev.next;       // target menunjuk Node(B)
T removed = target.data;          // B
prev.next = target.next;          // Node(A).next langsung menunjuk Node(C)
if (target == tail) tail = prev;  // false
size--;                           // size menjadi 2
```

### Tabel Perubahan Pointer
| Variabel / Pointer | Sebelum `remove(1)` | Setelah `remove(1)` | Keterangan |
|---|---|---|---|
| `head` | Node(A) | Node(A) | Tetap di elemen pertama |
| Node(A).next | Node(B) | Node(C) | Pointer dilewatkan (*bypass*) melompati Node(B) |
| Node(B).next | Node(C) | Node(C) (orphan) | Node(B) terisolasi |
| Node(C).next | `null` | `null` | Tetap elemen terakhir |
| `tail` | Node(C) | Node(C) | Tetap menunjuk elemen terakhir |
| `size` | 3 | 2 | Berkurang 1 |

---

## 3. Hapus Elemen Terakhir (`remove(2)`)

Misalkan list awal memiliki 3 elemen: `[A -> B -> C]` ($size = 3$), kita menghapus Node(C) pada indeks 2.

### Kondisi Sebelum Operasi
- `head` menunjuk Node(A)
- Node(A).next menunjuk Node(B)
- Node(B).next menunjuk Node(C)
- `tail` menunjuk Node(C)
- `size` = 3

### Langkah Eksekusi Kode (`remove(2)`)
```java
Node<T> prev = nodeAt(index - 1); // prev menunjuk Node(B)
Node<T> target = prev.next;       // target menunjuk Node(C)
T removed = target.data;          // C
prev.next = target.next;          // Node(B).next menunjuk null (karena target.next == null)
if (target == tail) tail = prev;  // true! tail sekarang diperbarui menunjuk Node(B)
size--;                           // size menjadi 2
```

### Tabel Perubahan Pointer
| Variabel / Pointer | Sebelum `remove(2)` | Setelah `remove(2)` | Keterangan |
|---|---|---|---|
| `head` | Node(A) | Node(A) | Tetap di elemen pertama |
| Node(A).next | Node(B) | Node(B) | Tidak berubah |
| Node(B).next | Node(C) | `null` | Mengambil `target.next` yang bernilai `null` |
| `tail` | Node(C) | Node(B) | `tail` mundur ke `prev` karena target adalah `tail` lama |
| `size` | 3 | 2 | Berkurang 1 |

---

## Kesimpulan Konsistensi Invarian
Ketiga operasi penghapusan di atas secara ketat menjaga aturan invarian linked list (`validateInvariant()`):
1. `head` dan `tail` menunjuk simpul yang tepat.
2. Simpul terakhir (`tail`) selalu memiliki `.next == null`.
3. Tidak terbentuk siklus (*infinite loop*).
4. Jumlah simpul terhitung via traversal selalu persis sama dengan nilai atribut `size`.
