import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class TestGramatica extends Gramatica{

    @Test
    void testAfegirSimbolNoTerminalIOtenir() {
        Gramatica g = new Gramatica();
        SimbolNoTerminal s = new SimbolNoTerminal("A");
        g.afegirSimbolNoTerminal(s);

        assertEquals(s, g.obtenirSimbol("A"));
    }

    @Test
    void testAfegirProduccioASimbolExist() {
        Gramatica g = new Gramatica();
        SimbolNoTerminal s = new SimbolNoTerminal("B");
        g.afegirSimbolNoTerminal(s);

        g.afegirProduccio("B", "aC");

        ArrayList<String> esperat = new ArrayList<>();
        esperat.add("aC");

        assertEquals(esperat, g.obtenirSimbol("B").getProduccions());
    }

    @Test
    void testAfegirProduccioASimbolInexistent() {
        Gramatica g = new Gramatica();
        // Ha de mostrar el missatge però no llençar excepcions
        assertDoesNotThrow(() -> g.afegirProduccio("X", "a"));
        assertNull(g.obtenirSimbol("X"));
    }

    @Test
    void testObtenirSimbolsTerminals() {
        Gramatica g = new Gramatica();
        SimbolNoTerminal s1 = new SimbolNoTerminal("S");
        s1.afegirProduccio("aA");
        s1.afegirProduccio("bB");
        SimbolNoTerminal s2 = new SimbolNoTerminal("A");
        s2.afegirProduccio("c");

        g.afegirSimbolNoTerminal(s1);
        g.afegirSimbolNoTerminal(s2);

        Set<Character> esperat = Set.of('a', 'b', 'c');
        assertEquals(esperat, g.obtenirSimbolsTerminals());
    }

    @Test
    void testEliminarSimbolsInutils() {
        Gramatica g = new Gramatica();

        // S → A | a
        SimbolNoTerminal s = new SimbolNoTerminal("S");
        s.afegirProduccio("A");
        s.afegirProduccio("a");

        // A → B
        SimbolNoTerminal a = new SimbolNoTerminal("A");
        a.afegirProduccio("B");

        // B → C (C no és fecund ni accessible)
        SimbolNoTerminal b = new SimbolNoTerminal("B");
        b.afegirProduccio("C");

        // C → D (D tampoc no defineix res útil)
        SimbolNoTerminal c = new SimbolNoTerminal("C");
        c.afegirProduccio("D");

        g.afegirSimbolNoTerminal(s);
        g.afegirSimbolNoTerminal(a);
        g.afegirSimbolNoTerminal(b);
        g.afegirSimbolNoTerminal(c);

        g.eliminarSimbolsInutils();

        // Només hauria de quedar "S → a"
        SimbolNoTerminal snRestant = g.obtenirSimbol("S");
        assertNotNull(snRestant);
        assertEquals(List.of("a"), snRestant.getProduccions());

        // A, B, C han estat eliminats
        assertNull(g.obtenirSimbol("A"));
        assertNull(g.obtenirSimbol("B"));
        assertNull(g.obtenirSimbol("C"));
    }
    private Gramatica creaGramaticaAmbSimbols(String[][] definicions) {
        Gramatica g = new Gramatica();
        for (String[] definicio : definicions) {
            SimbolNoTerminal sn = new SimbolNoTerminal(definicio[0]);
            for (int i = 1; i < definicio.length; i++) {
                sn.afegirProduccio(definicio[i]);
            }
            g.afegirSimbolNoTerminal(sn);
        }
        return g;
    }

    @Test
    void testSimbolsNoFecunds() {
        // S → A
        // A → B
        // B → C
        // C sense produccions
        Gramatica g = creaGramaticaAmbSimbols(new String[][] {
                {"S", "A"},
                {"A", "B"},
                {"B", "C"},
                {"C"} // cap producció
        });

        g.eliminarSimbolsInutils();

        // Només hauria de quedar S i cap producció
        SimbolNoTerminal s = g.obtenirSimbol("S");
        assertNotNull(s);
        assertTrue(s.getProduccions().isEmpty());
    }

    @Test
    void testSimbolsNoAccessibles() {
        // S → a
        // A → b (no és accessible des de S)
        Gramatica g = creaGramaticaAmbSimbols(new String[][] {
                {"S", "a"},
                {"A", "b"}
        });

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertNull(g.obtenirSimbol("A"));
        assertEquals(List.of("a"), g.obtenirSimbol("S").getProduccions());
    }

    @Test
    void testSimbolAmbProduccionsMixtes() {
        // S → A | a
        // A → B
        // B no té produccions útils
        Gramatica g = creaGramaticaAmbSimbols(new String[][] {
                {"S", "A", "a"},
                {"A", "B"},
                {"B"} // sense produccions útils
        });

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertEquals(List.of("a"), g.obtenirSimbol("S").getProduccions());
        assertNull(g.obtenirSimbol("A"));
        assertNull(g.obtenirSimbol("B"));
    }

    @Test
    void testSimbolAmbProduccioAmbMesclaDeSimbols() {
        // S → aA | Bc
        // A → b
        // B → d
        Gramatica g = creaGramaticaAmbSimbols(new String[][] {
                {"S", "aA", "Bc"},
                {"A", "b"},
                {"B", "d"}
        });

        g.eliminarSimbolsInutils();

        // Tots són útils
        assertNotNull(g.obtenirSimbol("S"));
        assertNotNull(g.obtenirSimbol("A"));
        assertNotNull(g.obtenirSimbol("B"));
        assertEquals(Set.of("aA", "Bc"), new HashSet<>(g.obtenirSimbol("S").getProduccions()));
    }

    @Test
    void testProduccionsEliminadesPerReferenciarInutils() {
        // S → A | b
        // A → C
        // C → D
        // D → (sense produccions)
        Gramatica g = creaGramaticaAmbSimbols(new String[][] {
                {"S", "A", "b"},
                {"A", "C"},
                {"C", "D"},
                {"D"}
        });

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertEquals(List.of("b"), g.obtenirSimbol("S").getProduccions());
        assertNull(g.obtenirSimbol("A"));
        assertNull(g.obtenirSimbol("C"));
        assertNull(g.obtenirSimbol("D"));
    }

    @Test
    void testSimbolInicialAmbProduccioBuida() {
        // S → &
        Gramatica g = creaGramaticaAmbSimbols(new String[][] {
                {"S", "&"}
        });

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertEquals(List.of("&"), g.obtenirSimbol("S").getProduccions());
    }

    @Test
    void testTotEsUti() {
        // S → A
        // A → B
        // B → c
        Gramatica g = creaGramaticaAmbSimbols(new String[][] {
                {"S", "A"},
                {"A", "B"},
                {"B", "c"}
        });

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertNotNull(g.obtenirSimbol("A"));
        assertNotNull(g.obtenirSimbol("B"));
    }
    @Test
    public void testSenseSimbolsInutils() {
        Gramatica g = new Gramatica();

        SimbolNoTerminal S = new SimbolNoTerminal("S");
        S.afegirProduccio("aA");
        SimbolNoTerminal A = new SimbolNoTerminal("A");
        A.afegirProduccio("b");

        g.afegirSimbolNoTerminal(S);
        g.afegirSimbolNoTerminal(A);

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertNotNull(g.obtenirSimbol("A"));
    }
    @Test
    public void testSimbolNoAccessible() {
        Gramatica g = new Gramatica();

        SimbolNoTerminal S = new SimbolNoTerminal("S");
        S.afegirProduccio("aA");
        SimbolNoTerminal A = new SimbolNoTerminal("A");
        A.afegirProduccio("b");
        SimbolNoTerminal B = new SimbolNoTerminal("B");
        B.afegirProduccio("c");

        g.afegirSimbolNoTerminal(S);
        g.afegirSimbolNoTerminal(A);
        g.afegirSimbolNoTerminal(B); // B mai és referenciat

        g.eliminarSimbolsInutils();

        assertNull(g.obtenirSimbol("B")); // B hauria de ser eliminat
        assertNotNull(g.obtenirSimbol("S"));
        assertNotNull(g.obtenirSimbol("A"));
    }
    @Test
    public void testSimbolNoFecund() {
        Gramatica g = new Gramatica();

        SimbolNoTerminal S = new SimbolNoTerminal("S");
        S.afegirProduccio("A");
        SimbolNoTerminal A = new SimbolNoTerminal("A");
        A.afegirProduccio("B");
        SimbolNoTerminal B = new SimbolNoTerminal("B");
        B.afegirProduccio("C"); // C no està definit

        g.afegirSimbolNoTerminal(S);
        g.afegirSimbolNoTerminal(A);
        g.afegirSimbolNoTerminal(B);

        g.eliminarSimbolsInutils();

        assertNull(g.obtenirSimbol("A"));
        assertNull(g.obtenirSimbol("B"));
        assertNotNull(g.obtenirSimbol("S")); // S es manté, però sense produccions
        assertTrue(g.obtenirSimbol("S").getProduccions().isEmpty());
    }
    @Test
    public void testSimbolReferenciatPeroNoDefinit() {
        Gramatica g = new Gramatica();

        SimbolNoTerminal S = new SimbolNoTerminal("S");
        S.afegirProduccio("A");

        g.afegirSimbolNoTerminal(S); // A mai es defineix

        g.eliminarSimbolsInutils();

        assertTrue(g.obtenirSimbol("S").getProduccions().isEmpty());
        assertNull(g.obtenirSimbol("A"));
    }
    @Test
    public void testProduccionsEliminadesParcialment() {
        Gramatica g = new Gramatica();

        SimbolNoTerminal S = new SimbolNoTerminal("S");
        S.afegirProduccio("A");    // A serà útil
        S.afegirProduccio("B");    // B no és fecund
        SimbolNoTerminal A = new SimbolNoTerminal("A");
        A.afegirProduccio("a");
        SimbolNoTerminal B = new SimbolNoTerminal("B");
        B.afegirProduccio("C");    // C no definit

        g.afegirSimbolNoTerminal(S);
        g.afegirSimbolNoTerminal(A);
        g.afegirSimbolNoTerminal(B);

        g.eliminarSimbolsInutils();

        SimbolNoTerminal snS = g.obtenirSimbol("S");

        assertEquals(1, snS.getProduccions().size());
        assertEquals("A", snS.getProduccions().get(0));
        assertNotNull(g.obtenirSimbol("A"));
        assertNull(g.obtenirSimbol("B")); // B hauria d’eliminar-se
    }

    @Test
    void testSimbolFecundAmbParaulaBuida() {
        Gramatica g = creaGramaticaAmbSimbols(new String[][] {
                {"S", "&"},
                {"A", "S"}
        });

        Set<String> fecunds = g.calcularSimbolsFecunds();

        assertTrue(fecunds.contains("S"));
        assertTrue(fecunds.contains("A"));
    }
    @Test
    void testProduccioParcialmentEliminada() {
        Gramatica g = creaGramaticaAmbSimbols(new String[][]{
                {"S", "A"},
                {"A", "a"},
                {"B", "c"} // B és inútil
        });

        g.eliminarSimbolsInutils();

        // 'S' hauria de seguir existint
        assertNotNull(g.obtenirSimbol("S"));
        assertEquals(List.of("A"), g.obtenirSimbol("S").getProduccions());

        // 'A' també és útil
        assertNotNull(g.obtenirSimbol("A"));
        assertEquals(List.of("a"), g.obtenirSimbol("A").getProduccions());

        // 'B' ha de ser eliminat
        assertNull(g.obtenirSimbol("B"));
    }

    // Cas mixt: S → A B (B no fecund), A → a
    @Test
    void testCasMixt() {
        Gramatica g = creaGramaticaAmbSimbols(new String[][]{
                {"S", "AB"},
                {"A", "a"},
                {"B", "CD"},
                {"C", "c"},
                {"D", "d"},
                {"E", "e"} // Inútil
        });

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertEquals(List.of("AB"), g.obtenirSimbol("S").getProduccions());

        assertNotNull(g.obtenirSimbol("A"));
        assertNotNull(g.obtenirSimbol("B"));
        assertNotNull(g.obtenirSimbol("C"));
        assertNotNull(g.obtenirSimbol("D"));

        // 'E' no contribueix a cap producció, s'ha d'eliminar
        assertNull(g.obtenirSimbol("E"));
    }

    //  Cicle útil (A ↔ B amb producció terminal)
    @Test
    void testCicleEntreSimbolsFecundsIAccessibles() {
        Gramatica g = creaGramaticaAmbSimbols(new String[][]{
                {"S", "A"},
                {"A", "B"},
                {"B", "A"},
                {"B", "b"}
        });

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertNotNull(g.obtenirSimbol("A"));
        assertNotNull(g.obtenirSimbol("B"));
    }

    // Cicle inútil (A → B → A, sense terminals)
    @Test
    void testCicleInutilSenseSortida() {
        Gramatica g = creaGramaticaAmbSimbols(new String[][]{
                {"S", "A"},
                {"A", "B"},
                {"B", "A"}
        });

        g.eliminarSimbolsInutils();

        assertNotNull(g.obtenirSimbol("S"));
        assertTrue(g.obtenirSimbol("S").getProduccions().isEmpty());
        assertNull(g.obtenirSimbol("A"));
        assertNull(g.obtenirSimbol("B"));
    }

    // Simbols fecunds però no accessibles
    @Test
    void testSimbolsFecundsPeroNoAccessibles() {
        Gramatica g = creaGramaticaAmbSimbols(new String[][]{
                {"S", "a"},
                {"A", "b"}, // No accessible
                {"B", "c"}  // No accessible
        });

        g.eliminarSimbolsInutils();

        // Només 'S' ha de quedar
        assertNotNull(g.obtenirSimbol("S"));
        assertEquals(List.of("a"), g.obtenirSimbol("S").getProduccions());

        assertNull(g.obtenirSimbol("A"));
        assertNull(g.obtenirSimbol("B"));
    }
}
