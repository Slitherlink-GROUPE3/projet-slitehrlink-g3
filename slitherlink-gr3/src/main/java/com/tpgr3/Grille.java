package com.tpgr3;

/**
 * Représente une grille de jeu contenant des cellules de type {@link Case} et {@link Slot}.
 * La grille est initialisée avec une matrice de valeurs et organise les cellules en conséquence.
 */
class Grille {

    /** Matrice contenant les {@link Case} de la grille. */
    private Cellule[][] matrice;

    /** Dimensions de la grille. */
    private int largeur;
    private int hauteur;

    /**
     * Constructeur de la classe Grille.
     *
     * @param valeurs Tableau 2D contenant les valeurs des cases (uniquement pour les cases avec chiffres).
     */
    public Grille(int[][] valeurs) {
        this.hauteur = valeurs.length * 2 + 1;
        this.largeur = valeurs[0].length * 2 + 1;
        this.matrice = new Cellule[hauteur][largeur];

        initialiserGrille(valeurs);
    }

    /**
     * Initialise la grille en créant des objets {@link Case} et {@link Slot} 
     * en fonction de leur position dans la matrice.
     * Les {@link Cases} sont initialisées sur x et y impairs, 
     * Les {@link Slots} sur x et y pairs.
     *
     * @param valeurs Tableau 2D contenant les valeurs des cases numériques.
     */
    private void initialiserGrille(int[][] valeurs) {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                matrice[y][x] = new CaseVide(x, y);
                if (x % 2 == 1 && y % 2 == 1) { // Case contenant un chiffre
                    matrice[y][x] = new Case(x, y, valeurs[y / 2][x / 2]);
                    System.out.println("Case créée en (" + x + ", " + y + ")");
                } else if (x % 2 == 0 && y % 2 == 1) { // Slot (emplacement pour des batons et croix)
                    matrice[y][x] = new Slot(x, y);
                    System.out.println("Slot créé en (" + x + ", " + y + ")");
                }
                else if (x % 2 == 1 && y % 2 == 0) { // Slot (emplacement pour des batons et croix)
                    matrice[y][x] = new Slot(x, y);
                    System.out.println("Slot créé en (" + x + ", " + y + ")");
                }
            }
        }
    }

    /**
     * Affiche la grille dans la console.
     * Chaque cellule est représentée par son affichage spécifique défini dans {@link Cellule#afficher()}.
     * Les cellules sont affichées dans une grille carrée avec des caractères de même taille.
     */
    public void afficher() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (matrice[y][x] != null) {
                    System.out.print(String.format("%-3s", matrice[y][x].afficher()) + " ");
                } else {
                    System.out.print("    ");
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
        matrice[x][y].actionner(); //comportement différent selon le type de cellule
    }

    public int getHauteur() {
        return hauteur;
    }
    public int getLargeur() {
        return largeur;
    }
}