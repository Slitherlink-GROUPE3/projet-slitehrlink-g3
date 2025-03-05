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
        int nbSlot = 0;
        int nbCase = 0 ;
        int nbVide = 0 ;

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {

                /*Ajouter une Case si x et y sont impairs*/
                if (x % 2 == 1 && y % 2 == 1) {
                    matrice[y][x] = new Case(x, y, valeurs[y / 2][x / 2]) ;
                    nbCase ++;
                    //System.out.println("Case créée en (" + x + ", " + y + ")");
                
                /*Ajouter un Slot si x est pair et y est impair ou x est impair et y est pair*/
                }  if ((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0)) {
                    matrice[y][x] = new Slot(x, y);
                    //System.out.println("Slot créé en (" + x + ", " + y + ")");
                    nbSlot ++;
                }
                /* Ajouter une CaseVide si x et y sont pairs */
                if (x % 2 == 0 && y % 2 == 0) {
                    matrice[y][x] = new CaseVide(x, y);
                    //System.out.println("Case vide créé en (" + x + ", " + y + ")");
                    nbVide++;
                }
            }
        }
        System.out.println("Grille initialisée");
        System.out.println("Nombre de cases créées: " + nbCase);
        System.out.println("Nombre de slots créés: " + nbSlot);
        System.out.println("Nombre de cases créées: " + nbVide);
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
        matrice[x][y].actionner();
    }

    public int getHauteur() {
        return hauteur;
    }

    public int getLargeur() {
        return largeur;
    }


    /**
     * Vérifie si les indices (x, y) sont valides dans la grille.
     *
     * @param x Indice x à vérifier.
     * @param y Indice y à vérifier.
     * @return true si les indices sont valides, false sinon.
     */
    public boolean estValide(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }
}