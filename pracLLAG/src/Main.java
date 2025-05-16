public class Main {
    public static void main(String[] args) {
        Gramatica gramatica = new Gramatica();
        System.out.println("Genera la teva gramàtica:");
        gramatica.generarGramatica();
        System.out.println();
        gramatica.mostrarGramatica();
        System.out.println("La gramatica incontextual sense símbols inútils és la següent: ");
        gramatica.eliminarSimbolsInutils();
        gramatica.mostrarProduccions();
    }
}
