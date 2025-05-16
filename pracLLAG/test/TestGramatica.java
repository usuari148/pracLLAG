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
    void testCasosEspecialsEliminarSimbolsInutils() {
        // Paraula buida (&)
        Gramatica g1 = creaGramaticaAmbSimbols(new String[][] {
                {"S", "&"},
                {"A", "S"}
        });
        g1.eliminarSimbolsInutils();
        assertNotNull(g1.obtenirSimbol("S"));
        assertEquals(List.of("&"), g1.obtenirSimbol("S").getProduccions());
        assertEquals(List.of("S"), g1.obtenirSimbol("A").getProduccions());

        // Cicle fecund i accessible
        Gramatica g2 = creaGramaticaAmbSimbols(new String[][] {
                {"S", "A"},
                {"A", "B"},
                {"B", "S"} // Cicle S → A → B → S
        });
        g2.eliminarSimbolsInutils();
        assertNotNull(g2.obtenirSimbol("S"));
        assertNotNull(g2.obtenirSimbol("A"));
        assertNotNull(g2.obtenirSimbol("B"));

        // Cicle no fecund
        Gramatica g3 = creaGramaticaAmbSimbols(new String[][] {
                {"S", "A"},
                {"A", "B"},
                {"B", "S"} // Cap arriba a producció terminal
        });
        g3.eliminarSimbolsInutils();
        SimbolNoTerminal s3 = g3.obtenirSimbol("S");
        assertNotNull(s3); // S sempre es manté
        assertTrue(s3.getProduccions().isEmpty()); // Però sense produccions
        assertNull(g3.obtenirSimbol("A")); // Eliminats per ser inútils
        assertNull(g3.obtenirSimbol("B"));

        // Producció amb símbol útil i un d'inútil (B no definit)
        Gramatica g4 = creaGramaticaAmbSimbols(new String[][] {
                {"S", "aA", "aB"},
                {"A", "b"}
        });
        g4.eliminarSimbolsInutils();
        assertEquals(List.of("aA"), g4.obtenirSimbol("S").getProduccions()); // només es conserva "aA"
        assertNotNull(g4.obtenirSimbol("A")); // A és útil
        assertNull(g4.obtenirSimbol("B")); // B no està definit → es tracta com a inútil
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

}
