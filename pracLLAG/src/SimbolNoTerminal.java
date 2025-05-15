import java.util.ArrayList;
public class SimbolNoTerminal {
    private String simbol;
    private ArrayList<String> produccions;

    // Constructor
    public SimbolNoTerminal(String simbol) {
        this.simbol = simbol;
        this.produccions = new ArrayList<>();
    }

    // Mètode per afegir una producció
    public void afegirProduccio(String produccio) {
        produccions.add(produccio);
    }

    // Mètode per mostrar la informació del símbol
    @Override
    public String toString() {
        return simbol + " → " + String.join(" | ", produccions);
    }

    // Getter per obtenir el símbol
    public String getSimbol() {
        return simbol;
    }

    // Getter per obtenir les produccions
    public ArrayList<String> getProduccions() {
        return produccions;
    }
}
