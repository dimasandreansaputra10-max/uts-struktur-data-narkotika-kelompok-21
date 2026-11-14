package strukturdata.struktur;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListTest {

    private SinglyLinkedList<String> listABC() {
        SinglyLinkedList<String> l = new SinglyLinkedList<>();
        l.addLast("A"); l.addLast("B"); l.addLast("C");
        return l;
    }

    @Test
    void kosong() {
        SinglyLinkedList<String> l = new SinglyLinkedList<>();
        assertTrue(l.isEmpty());
        assertEquals(0, l.size());
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(0));
        assertFalse(l.search(x -> true).ditemukan());
        l.validateInvariant();
    }

    @Test
    void addLastPadaListKosong() {
        SinglyLinkedList<String> l = new SinglyLinkedList<>();
        l.addLast("A");
        assertEquals("A", l.get(0));
        assertEquals(1, l.size());
        l.validateInvariant();
    }

    @Test
    void satuElemenLaluHapusMembuatKosong() {
        SinglyLinkedList<String> l = new SinglyLinkedList<>();
        l.add("A");
        assertEquals("A", l.remove(0));
        assertTrue(l.isEmpty());
        l.addLast("B"); // tail harus sudah null sehingga addLast tetap benar
        assertEquals("B", l.get(0));
        l.validateInvariant();
    }

    @Test
    void hapusPertama() {
        SinglyLinkedList<String> l = listABC();
        assertEquals("A", l.remove(0));
        assertEquals("[B, C]", l.toString());
        l.validateInvariant();
    }

    @Test
    void hapusTengah() {
        SinglyLinkedList<String> l = listABC();
        assertEquals("B", l.remove(1));
        assertEquals("[A, C]", l.toString());
        l.validateInvariant();
    }

    @Test
    void hapusTerakhirTailMundur() {
        SinglyLinkedList<String> l = listABC();
        assertEquals("C", l.remove(2));
        l.addLast("D");
        assertEquals("[A, B, D]", l.toString());
        l.validateInvariant();
    }

    @Test
    void addFirstDanInsert() {
        SinglyLinkedList<String> l = listABC();
        l.addFirst("X");
        l.insert(2, "Y");
        l.insert(l.size(), "Z");
        assertEquals("[X, A, Y, B, C, Z]", l.toString());
        assertThrows(IndexOutOfBoundsException.class, () -> l.insert(-1, "?"));
        assertThrows(IndexOutOfBoundsException.class, () -> l.insert(l.size() + 1, "?"));
        l.validateInvariant();
    }

    @Test
    void indeksDiLuarBatas() {
        SinglyLinkedList<String> l = listABC();
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(3));
        l.validateInvariant();
    }

    @Test
    void searchMenghitungLangkah() {
        SinglyLinkedList<String> l = listABC();
        SearchResult<String> r = l.search(x -> x.equals("C"));
        assertTrue(r.ditemukan());
        assertEquals(3, r.getLangkah());
        SearchResult<String> tidak = l.search(x -> x.equals("Z"));
        assertNull(tidak.getElemen());
        assertEquals(3, tidak.getLangkah());
        System.out.println("Langkah search C = " + r.getLangkah() + ", Z = " + tidak.getLangkah());
        l.validateInvariant();
    }

    @Test
    void containsDanClear() {
        SinglyLinkedList<String> l = listABC();
        assertTrue(l.contains("B"));
        assertEquals(2, l.getStepCount());
        assertFalse(l.contains("Q"));
        l.clear();
        assertTrue(l.isEmpty());
        l.validateInvariant();
    }
}