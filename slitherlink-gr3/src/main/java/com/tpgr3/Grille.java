package main.java.com.tpgr3;

class Grille {
    private Cellule[][] matrice;
    private int largeur, hauteur;

    public Grille(int largeur, int hauteur, int[][] valeurs) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.matrice = new Cellule[hauteur][largeur];

        initialiserGrille(valeurs);
    }

    private void initialiserGrille(int[][] valeurs) {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (x % 2 == 1 && y % 2 == 1) { // Case avec chiffre
                    matrice[y][x] = new Case(x, y, valeurs[y / 2][x / 2]);
                } else if (x % 2 == 0 || y % 2 == 0) { // Slot
                    matrice[y][x] = new Slot(x, y);
                }
            }
        }
    }

    public void afficher() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                System.out.print(matrice[y][x] != null ? matrice[y][x].afficher() + " " : "  ");
            }
            System.out.println();
        }
    }
}
