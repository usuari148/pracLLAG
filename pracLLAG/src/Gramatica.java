import java.util.*;

public class Gramatica {
    private ArrayList<SimbolNoTerminal> simbols;

    // Constructor
    public Gramatica() {
        this.simbols = new ArrayList<>();
    }

    // Mètode per afegir un símbol no terminal a la gramàtica
    public void afegirSimbolNoTerminal(SimbolNoTerminal simbol) {
        simbols.add(simbol);
    }

    // Mètode per afegir una producció a un símbol existent
    public void afegirProduccio(String simbol, String produccio) {
        for (SimbolNoTerminal sn : simbols) {
            if (sn.getSimbol().equals(simbol)) {
                sn.afegirProduccio(produccio);
                return;
            }
        }
        System.out.println("Símbol no trobat: " + simbol);
    }

    // Mètode per mostrar la gramàtica
    public void mostrarProduccions() {
        for (SimbolNoTerminal sn : simbols) {
            System.out.println(sn);
        }
    }
    public void mostrarGramatica() {
        // Conjunt de símbols no terminals
        Set<String> noTerminals = new LinkedHashSet<>();
        for (SimbolNoTerminal sn : simbols) {
            noTerminals.add(sn.getSimbol());
        }

        // Conjunt de terminals
        Set<Character> terminals = obtenirSimbolsTerminals();

        System.out.println("Sigui la gramàtica G = (N, T, P, S) amb:\n");

        // Mostrar N
        System.out.print("N = {");
        Iterator<String> itN = noTerminals.iterator();
        while (itN.hasNext()) {
            System.out.print(itN.next());
            if (itN.hasNext()) System.out.print(", ");
        }
        System.out.println("}     (símbols no terminals)");

        // Mostrar T
        System.out.print("T = {");
        Iterator<Character> itT = terminals.iterator();
        while (itT.hasNext()) {
            System.out.print(itT.next());
            if (itT.hasNext()) System.out.print(", ");
        }
        System.out.println("}     (símbols terminals)");

        // Mostrar produccions
        System.out.println("\nProduccions P:");
        for (SimbolNoTerminal sn : simbols) {
            List<String> produccions = sn.getProduccions();
            if (!produccions.isEmpty()) {
                System.out.print(sn.getSimbol() + " → ");
                for (int i = 0; i < produccions.size(); i++) {
                    System.out.print(produccions.get(i));
                    if (i < produccions.size() - 1) {
                        System.out.print(" | ");
                    }
                }
                System.out.println();
            }
        }
    }

    // Mètode per obtenir un símbol no terminal per nom
    public SimbolNoTerminal obtenirSimbol(String simbol) {
        for (SimbolNoTerminal sn : simbols) {
            if (sn.getSimbol().equals(simbol)) {
                return sn;
            }
        }
        return null;
    }

    // Mètode per generar la gramàtica
    public void generarGramatica() {
        Scanner scanner = new Scanner(System.in);

        String[] nomsSimbols = "SABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
        int index = 0;

        Set<String> simbolsDefinits = new HashSet<>();
        Set<String> simbolsReferenciats = new LinkedHashSet<>();

        System.out.println("NOTA: El símbol & representa la paraula buida.\n");

        // Fase 1: generació inicial
        while (index < nomsSimbols.length) {
            String simbol = nomsSimbols[index];

            System.out.println("Introdueix les produccions per al símbol " + simbol + ":");
            System.out.println("(Prem Enter sense escriure res per acabar)");

            SimbolNoTerminal sn = new SimbolNoTerminal(simbol);
            int comptador = 1;

            while (true) {
                System.out.print("  Producció " + comptador + ": ");
                String produccio = scanner.nextLine();

                if (produccio.isEmpty() && comptador == 1) {
                    // No afegim el símbol i sortim de la fase 1
                    break;
                }

                if (produccio.isEmpty()) {
                    break;
                }

                sn.afegirProduccio(produccio);

                // Detectar referències a nous símbols no terminals
                for (char c : produccio.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        String ref = String.valueOf(c);
                        if (!ref.equals(simbol)) {
                            simbolsReferenciats.add(ref);
                        }
                    }
                }

                comptador++;
            }

            // Si no s’ha afegit cap producció, parem el procés principal
            if (sn.getProduccions().isEmpty()) {
                break;
            }

            afegirSimbolNoTerminal(sn);
            simbolsDefinits.add(simbol);
            index++;
        }

        // Fase 2: forçar definició de símbols no terminals referenciats
        Queue<String> perDefinir = new LinkedList<>(simbolsReferenciats);

        while (!perDefinir.isEmpty()) {
            String simbol = perDefinir.poll();

            if (simbolsDefinits.contains(simbol)) continue;

            System.out.println("Has de definir el símbol no terminal " + simbol + ":");
            System.out.println("(Prem Enter sense escriure res a la producció 1 per indicar paraula buida)");

            SimbolNoTerminal sn = new SimbolNoTerminal(simbol);
            int comptador = 1;

            while (true) {
                System.out.print("  Producció " + comptador + ": ");
                String produccio = scanner.nextLine();

                if (produccio.isEmpty() && comptador == 1) {
                    produccio = "&";
                    sn.afegirProduccio(produccio);
                    break;
                }

                if (produccio.isEmpty()) break;

                sn.afegirProduccio(produccio);

                // Afegir nous símbols referenciats si cal
                for (char c : produccio.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        String ref = String.valueOf(c);
                        if (!simbolsDefinits.contains(ref) && !perDefinir.contains(ref)) {
                            perDefinir.add(ref);
                        }
                    }
                }

                comptador++;
            }

            afegirSimbolNoTerminal(sn);
            simbolsDefinits.add(simbol);
        }
    }
    // Mètode per obtenir els símbols terminals (alfabet)
    public Set<Character> obtenirSimbolsTerminals() {
        Set<Character> terminals = new HashSet<>();

        // Recorrem les produccions de tots els símbols no terminals
        for (SimbolNoTerminal sn : simbols) {
            for (String produccio : sn.getProduccions()) {
                // Afegim els caràcters terminals que no són símbols no terminals ni la paraula buida (&)
                for (char c : produccio.toCharArray()) {
                    if (Character.isLowerCase(c) && c != '&') {  // Excloem la paraula buida (&)
                        terminals.add(c);
                    }
                }
            }
        }
        return terminals;
    }
    // Mètode per mostrar els símbols terminals amb notació de conjunt
    public void mostrarSimbolsTerminals() {
        Set<Character> terminals = obtenirSimbolsTerminals();
        System.out.print("T = {");

        Iterator<Character> iterator = terminals.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next());
            if (iterator.hasNext()) {
                System.out.print(", ");
            }
        }

        System.out.println("}");
    }
    public void eliminarSimbolsInutils() {
        Set<String> fecunds = calcularSimbolsFecunds();
        Set<String> accessibles = calcularSimbolsAccessibles(fecunds);

        Set<String> utils = new HashSet<>(fecunds);
        utils.retainAll(accessibles);

        ArrayList<SimbolNoTerminal> nousSimbols = new ArrayList<>();

        for (SimbolNoTerminal sn : simbols) {
            String nomSimbol = sn.getSimbol();
            if (!utils.contains(nomSimbol) && !nomSimbol.equals("S")) continue;

            SimbolNoTerminal nouSN = new SimbolNoTerminal(nomSimbol);

            for (String produccio : sn.getProduccions()) {
                boolean produccioValida = true;
                for (char c : produccio.toCharArray()) {
                    if (Character.isUpperCase(c) && !utils.contains(String.valueOf(c))) {
                        produccioValida = false;
                        break;
                    }
                }
                if (produccioValida) {
                    nouSN.afegirProduccio(produccio);
                }
            }

            // Afegim el símbol encara que no tingui produccions, si és S
            if (!nouSN.getProduccions().isEmpty() || nomSimbol.equals("S")) {
                nousSimbols.add(nouSN);
            }
        }

        this.simbols = nousSimbols;
    }
    protected Set<String> calcularSimbolsFecunds() {
        Set<String> fecunds = new HashSet<>();
        Set<String> anterior;

        do {
            anterior = new HashSet<>(fecunds);
            for (SimbolNoTerminal sn : simbols) {
                if (fecunds.contains(sn.getSimbol())) continue;

                for (String produccio : sn.getProduccions()) {
                    boolean valida = true;
                    for (char c : produccio.toCharArray()) {
                        if (Character.isUpperCase(c) && !fecunds.contains(String.valueOf(c))) {
                            valida = false;
                            break;
                        }
                    }
                    if (valida) {
                        fecunds.add(sn.getSimbol());
                        break;
                    }
                }
            }
        } while (!fecunds.equals(anterior));

        return fecunds;
    }
    private Set<String> calcularSimbolsAccessibles(Set<String> simbolsFecunds) {
        Set<String> accessibles = new HashSet<>();
        Set<String> anterior;

        accessibles.add("S"); // El símbol inicial és "S" per defecte

        do {
            anterior = new HashSet<>(accessibles);
            for (SimbolNoTerminal sn : simbols) {
                if (!accessibles.contains(sn.getSimbol())) continue;

                for (String produccio : sn.getProduccions()) {
                    for (char c : produccio.toCharArray()) {
                        if ((Character.isUpperCase(c) || Character.isLowerCase(c)) &&
                                simbolsFecunds.contains(String.valueOf(c))) {
                            accessibles.add(String.valueOf(c));
                        }
                    }
                }
            }
        } while (!accessibles.equals(anterior));

        return accessibles;
    }
}