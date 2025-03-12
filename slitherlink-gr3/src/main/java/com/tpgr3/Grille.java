package com.tpgr3;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une grille de jeu contenant des cellules de type {@link Case} et {@link Slot} {@link CaseVide}.
 * La grille est initialisée avec une matrice de valeurs et organise les cellules en conséquence.
 */
public class Grille {

    /** Matrice contenant les {@link Cellule} de la grille. */
    private Cellule[][] matrice;

    /** Dimensions de la grille. */
    private int largeur;
    private int hauteur;

    /* Sauvagarde des valeurs des cases donnés en parametre*/
    private int[][] valeurs;

    /**
     * Constructeur de la classe Grille.
     *
     * @param valeurs Tableau 2D contenant les valeurs des cases (uniquement pour les cases avec chiffres).
     */
    public Grille(int[][] valeurs) {
        this.hauteur = valeurs.length * 2 + 1;
        this.largeur = valeurs[0].length * 2 + 1;
        this.matrice = new Cellule[hauteur][largeur];
        this.valeurs = valeurs;

        initialiserGrille(valeurs);
    }

    /**
     * Initialise la grille en créant des objets {@link Case} et {@link Slot} 
     * en fonction de leur position dans la matrice.
     * Les {@link Cases} sont initialisées sur x et y impairs, 
     * Les {@link Slots} sur x et y pairs.
     * Le reste c'est des {@link CaseVide}.
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
                }  else if ((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0)) {
                    matrice[y][x] = new Slot(x, y);
                    //System.out.println("Slot créé en (" + x + ", " + y + ")");
                    nbSlot ++;
                }
                /* Ajouter une CaseVide si x et y sont pairs */
                else if (x % 2 == 0 && y % 2 == 0) {
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
     * Methode qui permet de poser une Cellule sur un {@link Slot} de la grille.
     * Comportement different selon le type de Cellules.
     */
    public void actionnerCelule(int x, int y){
        matrice[x][y].actionner();
    }

    /*
     * Retourne la hauteur de la grille
     */
    public int getHauteur() {
        return hauteur;
    }

    /*
     * Retourne la largeur de la grille
     */
    public int getLargeur() {
        return largeur;
    }
   
    /*
     * Retourne la matrice complete
     */
    public Cellule[][] getMatrice() {
        return matrice;
    }

        
    /**
     * Retourne la cellule à la position spécifiée.
     *
     * @param x Indice x de la cellule.
     * @param y Indice y de la cellule.
     * @return La cellule à la position spécifiée.
     */
    public Cellule getCellule(int x, int y) {
        if (estValide(x, y)) {
            return matrice[y][x];
        }
        return null;
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


    /**
     * Réinitialise la grille à son état initial.
     */
    public void reinitialiser() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {

                /*Ajouter une CaseVide si x et y sont pairs*/
                if (x % 2 == 0 && y % 2 == 0) {
                    matrice[y][x] = new CaseVide(x, y);
                }
                /*Ajouter une Case si x et y sont impairs*/
                else if (x % 2 == 1 && y % 2 == 1) {
                    matrice[y][x] = new Case(x, y, 0); // Initialiser avec une valeur par défaut (0)
                }
                /*Ajouter un Slot si x est pair et y est impair ou x est impair et y est pair*/
                else if ((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0)) {
                    matrice[y][x] = new Slot(x, y);
                }
            }
        }
    }
    /**
     * Retourne une liste des cases adjacentes à la cellule spécifiée.
     * Seules les cellules de type {@link Case} sont incluses.
     *
     * @param cellule La cellule dont on cherche les voisins.
     * @return Une liste des cases adjacentes.
     */
    public List<Case> getVoisin(Cellule cellule) {
        List<Case> voisins = new ArrayList<>();

        // Récupérer les coordonnées de la cellule
        int x = cellule.getX();
        int y = cellule.getY();

        // Parcours des 8 cases adjacentes dans un rayon de 2 cases
        for (int dy = -2; dy <= 2; dy += 2) {
            for (int dx = -2; dx <= 2; dx += 2) {
                if (dx == 0 && dy == 0) continue; // Ignorer la cellule actuelle

                int nx = x + dx;
                int ny = y + dy;

                // Vérifier si les coordonnées sont valides
                if (estValide(nx, ny)) {
                    Cellule celluleVoisine = matrice[ny][nx];
                    // Vérifier si la cellule est de type Case
                    if (celluleVoisine instanceof Case) {
                        voisins.add((Case) celluleVoisine);
                    }
                }
            }
        }

        return voisins;
    }

    /* -------- [Methode sur la grille des valeurs ] ------------ */
    /**
    * Vérifie si les indices (i, j) sont valides dans la matrice valeurs.
    *
    * @param i Indice de ligne dans la matrice valeurs.
    * @param j Indice de colonne dans la matrice valeurs.
    * @return true si les indices sont valides, false sinon.
    */
    private boolean estValideBis(int i, int j) {
        return i >= 0 && i < valeurs.length && j >= 0 && j < valeurs[0].length;
    }

    

}