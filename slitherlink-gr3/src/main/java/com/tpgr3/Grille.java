package com.tpgr3;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une grille de jeu contenant des cellules de type {@link Case}, {@link Slot} et {@link CaseVide}.
 * La grille est initialisée avec une matrice de valeurs et organise les cellules en conséquence.
 */
public class Grille {

    /** Matrice contenant les {@link Cellule} de la grille. */
    private Cellule[][] matrice;

    /** Dimensions de la grille. */
    public int largeur; /*faudra remmetre en privée zebi les getters c'est pas pour rien */ 
    public int hauteur;

    /** Sauvegarde des valeurs des cases fournies en paramètre. */
    public int[][] valeurs;

    /**
     * Constructeur de la classe Grille.
     *
     * @param valeurs Tableau 2D contenant les valeurs des cases (uniquement pour les cases avec chiffres).
     */
    public Grille(int[][] valeurs) {
        this.hauteur = valeurs.length * 2 + 1;  // Calcul de la hauteur de la grille
        this.largeur = valeurs[0].length * 2 + 1;  // Calcul de la largeur de la grille
        this.matrice = new Cellule[hauteur][largeur];  // Initialisation de la matrice
        this.valeurs = valeurs;  // Sauvegarde des valeurs

        initialiserGrille(valeurs);  // Initialisation de la grille
    }

    /**
     * Initialise la grille en créant des objets {@link Case}, {@link Slot} et {@link CaseVide}
     * en fonction de leur position dans la matrice.
     * - Les {@link Case} sont placées aux coordonnées impaires (x et y impairs).
     * - Les {@link Slot} sont placés aux coordonnées mixtes (x pair et y impair, ou x impair et y pair).
     * - Les {@link CaseVide} sont placées aux coordonnées paires (x et y pairs).
     *
     * @param valeurs Tableau 2D contenant les valeurs des cases numériques.
     */
    private void initialiserGrille(int[][] valeurs) {
        int nbSlot = 0;  // Compteur de slots créés
        int nbCase = 0;  // Compteur de cases créées
        int nbVide = 0;  // Compteur de cases vides créées

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {

                // Ajouter une Case si x et y sont impairs
                if (x % 2 == 1 && y % 2 == 1) {
                    matrice[y][x] = new Case(x, y, valeurs[y / 2][x / 2]);
                    nbCase++;
                }
                // Ajouter un Slot si x est pair et y est impair, ou x est impair et y est pair
                else if ((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0)) {
                    matrice[y][x] = new Slot(x, y);
                    nbSlot++;
                }
                // Ajouter une CaseVide si x et y sont pairs
                else if (x % 2 == 0 && y % 2 == 0) {
                    matrice[y][x] = new CaseVide(x, y);
                    nbVide++;
                }
            }
        }

        // Affichage des statistiques d'initialisation
        System.out.println("\n\nGrille initialisée");
        System.out.println("Nombre de cases créées: " + nbCase);
        System.out.println("Nombre de slots créés: " + nbSlot);
        System.out.println("Nombre de cases vides créées: " + nbVide);
    }

    /**
     * Réinitialise la grille à son état initial.
     */
    public void reinitialiser() {
        initialiserGrille(valeurs);
        System.out.println("Grille réinitialisée aux valeurs par défaut");
    }

    /**
     * Affiche la grille dans la console.
     * Chaque cellule est représentée par son affichage spécifique défini dans {@link Cellule#afficher()}.
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
     * Permet d'interagir avec une cellule à la position (x, y).
     * Le comportement dépend du type de cellule.
     *
     * @param x Coordonnée x de la cellule.
     * @param y Coordonnée y de la cellule.
     */
    public void actionnerCelule(int x, int y) {
        if (estValide(x, y)) {
            matrice[y][x].actionner();
        } else {
            System.out.println("Coordonnées invalides.");
        }
    }

    /**
     * Retourne la hauteur de la grille.
     *
     * @return La hauteur de la grille.
     */
    public int getHauteur() {
        return hauteur;
    }

    /**
     * Retourne la largeur de la grille.
     *
     * @return La largeur de la grille.
     */
    public int getLargeur() {
        return largeur;
    }

    /**
     * Retourne la matrice complète de la grille.
     *
     * @return La matrice de cellules.
     */
    public Cellule[][] getMatrice() {
        return matrice;
    }

    /**
     * Retourne la cellule à la position spécifiée.
     *
     * @param x Coordonnée x de la cellule.
     * @param y Coordonnée y de la cellule.
     * @return La cellule à la position (x, y), ou null si les coordonnées sont invalides.
     */
    public Cellule getCellule(int x, int y) {
        if (estValide(x, y)) {
            return matrice[y][x];
        }
        return null;
    }

    /**
     * Retourne les dimensions logiques de la grille.
     * Les dimensions logiques sont calculées en fonction des dimensions réelles de la grille.
     *
     * @return Un tableau contenant la largeur et la hauteur logiques de la grille.
     */
    public int[] getDimensionsLogiques() {
        return new int[] { (largeur - 1) / 2, (hauteur - 1) / 2 };
    }


    /**
     * Vérifie si les coordonnées (x, y) sont valides dans la grille.
     *
     * @param x Coordonnée x à vérifier.
     * @param y Coordonnée y à vérifier.
     * @return true si les coordonnées sont valides, false sinon.
     */
    public boolean estValide(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }

    /**
     * Retourne les Slots adjacents à une Case.
     * @param x Coordonnée x de la Case (doit être impaire)
     * @param y Coordonnée y de la Case (doit être impaire)
     * @return Liste des Slots adjacents.
     */
    public List<Slot> getSlotsAdjacentsCase(int x, int y) {
        List<Slot> slots = new ArrayList<>();
        if (x % 2 != 1 || y % 2 != 1) return slots; // Vérification Case
        
        // Directions : haut, bas, gauche, droite
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (estValide(newX, newY)) {
                Cellule c = getCellule(newX, newY);
                if (c instanceof Slot) slots.add((Slot)c);
            }
        }
        return slots;
    }

    /**
     * Retourne les Cases adjacentes à une Case (2 cases de distance).
     * @param x Coordonnée x de la Case (doit être impaire)
     * @param y Coordonnée y de la Case (doit être impaire)
     * @return Liste des Cases adjacentes.
     */
    public List<Case> getCasesAdjacentes(int x, int y) {
        List<Case> cases = new ArrayList<>();
        if (x % 2 != 1 || y % 2 != 1) return cases;
        
        // Directions : haut, bas, gauche, droite (2 unités)
        int[][] directions = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (estValide(newX, newY)) {
                Cellule c = getCellule(newX, newY);
                if (c instanceof Case) cases.add((Case)c);
            }
        }
        return cases;
    }

    /**
     * Retourne les Cases connectées à un Slot.
     * @param x Coordonnée x du Slot
     * @param y Coordonnée y du Slot
     * @return Liste des Cases connectées.
     */
    public List<Case> getCasesPourSlot(int x, int y) {
        List<Case> cases = new ArrayList<>();
        if (!((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0))) {
            return cases;
        }
        
        // Slot horizontal
        if (x % 2 == 0) {
            if (estValide(x - 1, y)) cases.add((Case)getCellule(x - 1, y));
            if (estValide(x + 1, y)) cases.add((Case)getCellule(x + 1, y));
        } 
        // Slot vertical
        else {
            if (estValide(x, y - 1)) cases.add((Case)getCellule(x, y - 1));
            if (estValide(x, y + 1)) cases.add((Case)getCellule(x, y + 1));
        }
        return cases;
    }
}