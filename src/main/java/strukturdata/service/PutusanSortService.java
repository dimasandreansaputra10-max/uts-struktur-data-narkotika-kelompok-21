package strukturdata.service;

import strukturdata.model.PutusanNarkotika;
import strukturdata.struktur.SinglyLinkedList;

/**
 * Service untuk pengurutan (sorting) data putusan narkotika.
 * Aturan Keras:
 * 1. Pengurutan WAJIB dilakukan pada SALINAN list (list utama tidak boleh rusak/berubah urutan).
 * 2. Menggunakan algoritma pengurutan buatan sendiri (Merge Sort / Insertion Sort) tanpa Arrays.sort() atau Collections.sort().
 */
public class PutusanSortService {

    /**
     * Counter operasi untuk analisis kompleksitas sorting.
     * comparisonCount – jumlah perbandingan nilai vonis.
     * moveCount – jumlah operasi pemindahan (remove+insert).
     */
    private int comparisonCount;
    private int moveCount;

    /** Mengembalikan jumlah perbandingan pada pemanggilan terakhir. */
    public int getComparisonCount() { return comparisonCount; }

    /** Mengembalikan jumlah pemindahan elemen pada pemanggilan terakhir. */
    public int getMoveCount() { return moveCount; }

    /** Reset counter sebelum setiap pemanggilan sorting. */
    private void resetCounters() { comparisonCount = 0; moveCount = 0; }


    /**
     * Membuat salinan dangkal (shallow copy) dari SinglyLinkedList<T>.
     */
    public <T> SinglyLinkedList<T> salinList(SinglyLinkedList<T> source) {
        SinglyLinkedList<T> salinan = new SinglyLinkedList<>();
        if (source == null) return salinan;

        for (int i = 0; i < source.size(); i++) {
            salinan.addLast(source.get(i));
        }
        salinan.validateInvariant();
        return salinan;
    }

    /**
     * Mengurutkan salinan list putusan berdasarkan vonisBulan secara ascending (kecil ke besar).
     * List asli yang diberikan sebagai argumen tidak akan termodifikasi.
     *
     * @param source list putusan asli
     * @return SinglyLinkedList baru yang sudah terurut ascending
     */
    public SinglyLinkedList<PutusanNarkotika> urutkanBerdasarkanVonisAscending(SinglyLinkedList<PutusanNarkotika> source) {
        SinglyLinkedList<PutusanNarkotika> salinan = salinList(source);
        if (salinan.size() <= 1) return salinan;

        resetCounters();
        insertionSort(salinan, true);
        salinan.validateInvariant();
        return salinan;
    }

    /**
     * Mengurutkan salinan list putusan berdasarkan vonisBulan secara descending (besar ke kecil).
     * List asli yang diberikan sebagai argumen tidak akan termodifikasi.
     *
     * @param source list putusan asli
     * @return SinglyLinkedList baru yang sudah terurut descending
     */
    public SinglyLinkedList<PutusanNarkotika> urutkanBerdasarkanVonisDescending(SinglyLinkedList<PutusanNarkotika> source) {
        SinglyLinkedList<PutusanNarkotika> salinan = salinList(source);
        if (salinan.size() <= 1) return salinan;

        insertionSort(salinan, false);
        salinan.validateInvariant();
        return salinan;
    }

    /**
     * Implementasi Insertion Sort pada SinglyLinkedList.
     * Menggunakan operasi manipulasi list murni (remove dan insert).
     *
     * @param list list yang akan diurutkan di tempat
     * @param ascending true jika urut naik, false jika urut turun
     */
    private void insertionSort(SinglyLinkedList<PutusanNarkotika> list, boolean ascending) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            PutusanNarkotika current = list.get(i);
            int vCurrent = (current.getVonisBulan() == null) ? 0 : current.getVonisBulan();

            int targetIndex = i;
            // Cari posisi sisip di rentang [0..i-1]
            for (int j = 0; j < i; j++) {
                PutusanNarkotika pj = list.get(j);
                int vJ = (pj.getVonisBulan() == null) ? 0 : pj.getVonisBulan();

                boolean harusSebelum;
                if (ascending) {
                    harusSebelum = (vCurrent < vJ);
                } else {
                    harusSebelum = (vCurrent > vJ);
                }

                if (harusSebelum) {
                    targetIndex = j;
                    break;
                }
            }

            // Jika posisi berubah, pindahkan elemen ke targetIndex
            if (targetIndex != i) {
                list.remove(i);
                list.insert(targetIndex, current);
            }
        }
    }
}
