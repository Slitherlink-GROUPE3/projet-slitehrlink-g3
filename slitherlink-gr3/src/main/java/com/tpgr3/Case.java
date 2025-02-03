package main.java.com.tpgr3;

// Les cases contenant un chiffre
class Case extends Cellule {
    private int valeur;

    public Case(int x, int y, int valeur) {
        super(x, y);
        this.valeur = valeur;
    }

    @Override
    public char afficher() {
        return (char) ('0' + valeur); // Affiche le chiffre
    }
}