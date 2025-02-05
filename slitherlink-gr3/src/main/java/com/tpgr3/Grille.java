package com.tpgr3;

/**
 * Représente une grille de jeu contenant des cellules de type {@link Case} et {@link Slot}.
 * La grille est initialisée avec une matrice de valeurs et organise les cellules en conséquence.
 */
class Grille {

    /** Matrice contenant les cellules de la grille. */
    private Cellule[][] matrice;
    
    /** Largeur de la grille. */
    private int largeur;
    
    /** Hauteur de la grille. */
    private int hauteur;

    /**
     * Constructeur de la classe Grille.
     *
     * @param largeur La largeur de la grille en nombre de cellules.
     * @param hauteur La hauteur de la grille en nombre de cellules.
     * @param valeurs Tableau 2D contenant les valeurs des cases (uniquement pour les cases avec chiffres).
     */
    public Grille(int largeur, int hauteur, int[][] valeurs) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.matrice = new Cellule[hauteur][largeur];

        initialiserGrille(valeurs);
    }

    /**
     * Initialise la grille en créant des objets {@link Case} et {@link Slot} 
     * en fonction de leur position dans la matrice.
     *
     * @param valeurs Tableau 2D contenant les valeurs des cases numériques.
     */
    private void initialiserGrille(int[][] valeurs) {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (x % 2 == 1 && y % 2 == 1) { // Case contenant un chiffre
                    matrice[y][x] = new Case(x, y, valeurs[y / 2][x / 2]);
                } else if (x % 2 == 0 && y % 2 == 0) { // Slot (emplacement pour des lignes)
                    matrice[y][x] = new Slot(x, y);
                }
            }
        }
    }

    /**
     * Affiche la grille dans la console.
     * Chaque cellule est représentée par son affichage spécifique défini dans {@link Cellule#afficher()}.
     */
    public void afficher() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (matrice[y][x] != null) {
                    System.out.print(String.format("%-2s", matrice[y][x].afficher()) + " ");
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Methode qui permet de poser un baton sur un {@link Slot} de la grille.
     * Et impossible de le faire autre part que sur un {@link Slot}.
     */
    public void actionnerCelule(int x, int y){
        matrice[x][y].actionner();
    }
}
