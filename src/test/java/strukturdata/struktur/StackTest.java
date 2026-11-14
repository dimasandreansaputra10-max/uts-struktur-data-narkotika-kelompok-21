package strukturdata.struktur;

import org.junit.jupiter.api.Test;
import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

class StackTest {

    @Test
    void stackKosong() {
        Stack<String> s = new Stack<>();
        assertTrue(s.isEmpty());
        assertEquals(0, s.size());
        assertThrows(EmptyStackException.class, s::pop);
        assertThrows(EmptyStackException.class, s::peek);
        assertThrows(IndexOutOfBoundsException.class, () -> s.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> s.remove(0));
        assertFalse(s.contains("A"));
        s.validateInvariant();
    }

    @Test
    void pushDanPeek() {
        Stack<Integer> s = new Stack<>();
        s.push(10);
        assertEquals(1, s.size());
        assertFalse(s.isEmpty());
        assertEquals(10, s.peek());
        s.validateInvariant();

        s.push(20);
        assertEquals(2, s.size());
        assertEquals(20, s.peek());
        s.validateInvariant();
    }

    @Test
    void pushDanPopPrinsipLIFO() {
        Stack<String> s = new Stack<>();
        s.push("Pertama");
        s.push("Kedua");
        s.push("Ketiga");

        assertEquals(3, s.size());
        s.validateInvariant();

        assertEquals("Ketiga", s.pop());
        assertEquals(2, s.size());
        assertEquals("Kedua", s.peek());
        s.validateInvariant();

        assertEquals("Kedua", s.pop());
        assertEquals(1, s.size());
        assertEquals("Pertama", s.peek());
        s.validateInvariant();

        assertEquals("Pertama", s.pop());
        assertEquals(0, s.size());
        assertTrue(s.isEmpty());
        s.validateInvariant();

        assertThrows(EmptyStackException.class, s::pop);
    }

    @Test
    void searchPreserveDitemukan() {
        Stack<String> s = new Stack<>();
        s.push("Dasar");
        s.push("Tengah");
        s.push("Puncak");

        // Cari "Tengah"
        SearchResult<String> res = s.searchPreserve(item -> "Tengah".equals(item));
        assertTrue(res.ditemukan());
        assertEquals("Tengah", res.getElemen());
        // Dari puncak, langkah 1 = Puncak, langkah 2 = Tengah
        assertEquals(2, res.getLangkah());

        // Invariant dan urutan LIFO harus kembali utuh persis seperti semula
        s.validateInvariant();
        assertEquals(3, s.size());
        assertEquals("Puncak", s.pop());
        assertEquals("Tengah", s.pop());
        assertEquals("Dasar", s.pop());
        assertTrue(s.isEmpty());
        s.validateInvariant();
    }

    @Test
    void searchPreserveTidakDitemukan() {
        Stack<String> s = new Stack<>();
        s.push("A");
        s.push("B");
        s.push("C");

        SearchResult<String> res = s.searchPreserve(item -> "Z".equals(item));
        assertFalse(res.ditemukan());
        assertNull(res.getElemen());
        // Semua 3 elemen diperiksa
        assertEquals(3, res.getLangkah());

        // Urutan tetap utuh
        s.validateInvariant();
        assertEquals(3, s.size());
        assertEquals("C", s.pop());
        assertEquals("B", s.pop());
        assertEquals("A", s.pop());
        s.validateInvariant();
    }

    @Test
    void restoreStack() {
        Stack<Integer> s1 = new Stack<>();
        Stack<Integer> s2 = new Stack<>();

        // s2 memiliki elemen 1 (di-push pertama), lalu 2
        s2.push(1);
        s2.push(2);

        // restore memindahkan dari s2 ke s1: pop 2 -> push ke s1, pop 1 -> push ke s1
        s1.restore(s2);
        assertTrue(s2.isEmpty());
        s2.validateInvariant();

        assertEquals(2, s1.size());
        assertEquals(1, s1.pop());
        assertEquals(2, s1.pop());
        s1.validateInvariant();
    }

    @Test
    void implementasiStrukturDataInterface() {
        Stack<String> s = new Stack<>();
        // add memanggil push
        s.add("X");
        s.add("Y"); // Y di puncak
        assertEquals(2, s.size());
        assertEquals("Y", s.get(0)); // indeks 0 adalah top
        assertEquals("X", s.get(1));

        assertTrue(s.contains("X"));
        assertTrue(s.contains("Y"));
        assertFalse(s.contains("Z"));

        // remove(0) setara pop
        assertEquals("Y", s.remove(0));
        assertEquals(1, s.size());
        assertEquals("X", s.peek());

        s.clear();
        assertEquals(0, s.size());
        assertTrue(s.isEmpty());
        s.validateInvariant();
    }
}

