public class Main {
    public static void main(String[] args) {
        Gramatica gramatica = new Gramatica();
        System.out.println("Genera la teva gramàtica:");
        gramatica.generarGramatica();
        System.out.println();
        gramatica.obtenirSimbolsTerminals();
        System.out.println("La gramatica incontextual generada és la següent: ");
        gramatica.mostrarSimbolsTerminals();
        System.out.println("Les produccions P són:");
        gramatica.mostrarProduccions();
        System.out.println("La gramatica incontextual sense símbols inútils és la següent: ");
        gramatica.eliminarSimbolsInutils();
        gramatica.mostrarProduccions();
    }
}
