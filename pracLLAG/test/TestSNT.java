import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class TestSNT {

    @Test
    void testConstructorISimbol() {
        SimbolNoTerminal snt = new SimbolNoTerminal("A");
        assertEquals("A", snt.getSimbol());
    }

    @Test
    void testAfegirUnaProduccio() {
        SimbolNoTerminal snt = new SimbolNoTerminal("A");
        snt.afegirProduccio("aB");

        ArrayList<String> esperat = new ArrayList<>();
        esperat.add("aB");

        assertEquals(esperat, snt.getProduccions());
    }

    @Test
    void testAfegirDiversesProduccions() {
        SimbolNoTerminal snt = new SimbolNoTerminal("S");
        snt.afegirProduccio("aA");
        snt.afegirProduccio("bB");
        snt.afegirProduccio("&");  // epsilon

        ArrayList<String> esperat = new ArrayList<>();
        esperat.add("aA");
        esperat.add("bB");
        esperat.add("&");

        assertEquals(esperat, snt.getProduccions());
    }

    @Test
    void testToStringAmbUnaProduccio() {
        SimbolNoTerminal snt = new SimbolNoTerminal("S");
        snt.afegirProduccio("a");

        assertEquals("S → a", snt.toString());
    }

    @Test
    void testToStringAmbDiversesProduccions() {
        SimbolNoTerminal snt = new SimbolNoTerminal("S");
        snt.afegirProduccio("a");
        snt.afegirProduccio("b");
        snt.afegirProduccio("c");

        assertEquals("S → a | b | c", snt.toString());
    }

    @Test
    void testInicialmentSenseProduccions() {
        SimbolNoTerminal snt = new SimbolNoTerminal("X");
        assertTrue(snt.getProduccions().isEmpty());
        assertEquals("X → ", snt.toString());  // per comprovar què passa sense produccions
    }
}
