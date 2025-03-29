package com.tpgr3.backend;

/**
 * Classe permettant de récupérer les voisins d'une case
 * @param grille : la grille de jeu
 * @param x : coordonnée x de la case (coordonnées logiques, commençant à 1)
 * @param y : coordonnée y de la case (coordonnées logiques, commençant à 1)
 */
public class Voisins implements Constantes {
    public Cellule c;
    private Grille grille;
    private int hauteur;
    private int largeur;
    private Cellule[][] cellTab;

    // Coordonnées de la case centrale (x,y sont les indices logiques)
    private int x, y;
    
    // Coordonnées matricielles de la case centrale
    private int centerX, centerY;

    private int voisinHaut;
    private int voisinHautGauche;
    private int voisinHautDroite;
    private int voisinBas;
    private int voisinBasGauche;
    private int voisinBasDroite;
    private int voisinGauche;
    private int voisinDroite;
    
    /**
     * Constructeur
     * @param grille La grille de jeu
     * @param x Coordonnée x logique de la case (commençant à 1)
     * @param y Coordonnée y logique de la case (commençant à 1)
     */
    public Voisins(Grille grille, int x, int y) {
        this.grille = grille;
        this.x = x;
        this.y = y;
        
        // Conversion des coordonnées logiques en indices de la matrice
        // Pour accéder à la case centrale dans la matrice
        this.centerX = (x * 2) - 1;
        this.centerY = (y * 2) - 1;
        
        this.c = grille.getCellule(centerX, centerY);
        this.hauteur = grille.getHauteur();
        this.largeur = grille.getLargeur();
        this.cellTab = grille.getMatrice();
        
        // Initialisation des voisins
        this.voisinHaut = VALEUR_NON_INITIALISEE;
        this.voisinHautGauche = VALEUR_NON_INITIALISEE;
        this.voisinHautDroite = VALEUR_NON_INITIALISEE;
        this.voisinBas = VALEUR_NON_INITIALISEE;
        this.voisinBasGauche = VALEUR_NON_INITIALISEE;
        this.voisinBasDroite = VALEUR_NON_INITIALISEE;
        this.voisinGauche = VALEUR_NON_INITIALISEE;
        this.voisinDroite = VALEUR_NON_INITIALISEE;
    }

    public void afficher() {
        System.out.println("Case centrale: " + c.getValeur());
    }

    /**
     * Trouve les 8 voisins de la case centrale
     */
    public void TrouverVoisins() {
        System.out.println("Calcul des voisins pour la case (" + x + "," + y + ")");
        System.out.println("Position dans la matrice: (" + centerX + "," + centerY + ")");
        
        // Voisin du haut (même colonne, ligne -2)
        if (centerY - 2 >= 0) {
            if (grille.getCellule(centerX, centerY - 2) instanceof Case) {
                this.voisinHaut = grille.getCellule(centerX, centerY - 2).getValeur();
            } else {
                System.out.println("Pas de voisin en haut");
                this.voisinHaut = HORS_LIMITES;
            }
        } else {
            this.voisinHaut = HORS_LIMITES;
        }
        
        // Voisin en haut à gauche (colonne -2, ligne -2)
        if (centerX - 2 >= 0 && centerY - 2 >= 0) {
            if (grille.getCellule(centerX - 2, centerY - 2) instanceof Case) {
                this.voisinHautGauche = grille.getCellule(centerX - 2, centerY - 2).getValeur();
            } else {
                System.out.println("Pas de voisin en haut à gauche");
                this.voisinHautGauche = HORS_LIMITES;
            }
        } else {
            this.voisinHautGauche = HORS_LIMITES;
        }
        
        // Voisin en haut à droite (colonne +2, ligne -2)
        if (centerX + 2 < largeur && centerY - 2 >= 0) {
            if (grille.getCellule(centerX + 2, centerY - 2) instanceof Case) {
                this.voisinHautDroite = grille.getCellule(centerX + 2, centerY - 2).getValeur();
            } else {
                System.out.println("Pas de voisin en haut à droite");
                this.voisinHautDroite = HORS_LIMITES;
            }
        } else {
            this.voisinHautDroite = HORS_LIMITES;
        }
        
        // Voisin à gauche (colonne -2, même ligne)
        if (centerX - 2 >= 0) {
            if (grille.getCellule(centerX - 2, centerY) instanceof Case) {
                this.voisinGauche = grille.getCellule(centerX - 2, centerY).getValeur();
            } else {
                System.out.println("Pas de voisin à gauche");
                this.voisinGauche = HORS_LIMITES;
            }
        } else {
            this.voisinGauche = HORS_LIMITES;
        }
        
        // Voisin à droite (colonne +2, même ligne)
        if (centerX + 2 < largeur) {
            if (grille.getCellule(centerX + 2, centerY) instanceof Case) {
                this.voisinDroite = grille.getCellule(centerX + 2, centerY).getValeur();
            } else {
                System.out.println("Pas de voisin à droite");
                this.voisinDroite = HORS_LIMITES;
            }
        } else {
            this.voisinDroite = HORS_LIMITES;
        }
        
        // Voisin en bas (même colonne, ligne +2)
        if (centerY + 2 < hauteur) {
            if (grille.getCellule(centerX, centerY + 2) instanceof Case) {
                this.voisinBas = grille.getCellule(centerX, centerY + 2).getValeur();
            } else {
                System.out.println("Pas de voisin en bas");
                this.voisinBas = HORS_LIMITES;
            }
        } else {
            this.voisinBas = HORS_LIMITES;
        }
        
        // Voisin en bas à gauche (colonne -2, ligne +2)
        if (centerX - 2 >= 0 && centerY + 2 < hauteur) {
            if (grille.getCellule(centerX - 2, centerY + 2) instanceof Case) {
                this.voisinBasGauche = grille.getCellule(centerX - 2, centerY + 2).getValeur();
            } else {
                System.out.println("Pas de voisin en bas à gauche");
                this.voisinBasGauche = HORS_LIMITES;
            }
        } else {
            this.voisinBasGauche = HORS_LIMITES;
        }
        
        // Voisin en bas à droite (colonne +2, ligne +2)
        if (centerX + 2 < largeur && centerY + 2 < hauteur) {
            if (grille.getCellule(centerX + 2, centerY + 2) instanceof Case) {
                this.voisinBasDroite = grille.getCellule(centerX + 2, centerY + 2).getValeur();
            } else {
                System.out.println("Pas de voisin en bas à droite");
                this.voisinBasDroite = HORS_LIMITES;
            }
        } else {
            this.voisinBasDroite = HORS_LIMITES;
        }
    }

    // Getters pour récupérer les valeurs des voisins
    public int getVoisinHaut() { return voisinHaut; }
    public int getVoisinHautGauche() { return voisinHautGauche; }
    public int getVoisinHautDroite() { return voisinHautDroite; }
    public int getVoisinBas() { return voisinBas; }
    public int getVoisinBasGauche() { return voisinBasGauche; }
    public int getVoisinBasDroite() { return voisinBasDroite; }
    public int getVoisinGauche() { return voisinGauche; }
    public int getVoisinDroite() { return voisinDroite; }

    /**
     * Méthode de test visuel pour vérifier la détection des voisins
     */
    public void testDetection() {
        TrouverVoisins();
        
        System.out.println("\n========== DÉTECTION DES VOISINS ==========");
        System.out.println("Case logique: (" + x + "," + y + ")");
        System.out.println("Position dans la matrice: (" + centerX + "," + centerY + ")");
        System.out.println("Valeur de la case centrale: " + (c != null ? c.getValeur() : "NULL"));
        System.out.println("Dimensions de la grille: " + largeur + "x" + hauteur);
        
        // Tableau visuel des voisins
        System.out.println("\n+-------+-------+-------+");
        System.out.println("| " + formatValue(voisinHautGauche) + " | " + formatValue(voisinHaut) + " | " + formatValue(voisinHautDroite) + " |");
        System.out.println("+-------+-------+-------+");
        System.out.println("| " + formatValue(voisinGauche) + " | " + formatValue(c.getValeur()) + " | " + formatValue(voisinDroite) + " |");
        System.out.println("+-------+-------+-------+");
        System.out.println("| " + formatValue(voisinBasGauche) + " | " + formatValue(voisinBas) + " | " + formatValue(voisinBasDroite) + " |");
        System.out.println("+-------+-------+-------+\n");
        
        // Vérification des problèmes potentiels
        checkVoisinsCorrects();
    }
    
    // Méthode pour formater l'affichage des valeurs
    private String formatValue(int value) {
        if (value == HORS_LIMITES) {
            return "HORS";
        } else if (value == VALEUR_NON_INITIALISEE) {
            return "N/I";
        } else {
            return String.format("%3d", value);
        }
    }
    
    // Méthode qui vérifie si les voisins sont correctement détectés
    private void checkVoisinsCorrects() {
        System.out.println("=== DIAGNOSTICS ===");
        boolean probleme = false;
        
        // Vérification que les coordonnées de base sont valides
        if (x < 1 || y < 1) {
            System.out.println("⚠️ ERREUR: Les coordonnées logiques doivent être ≥ 1");
            probleme = true;
        }
        
        if (centerX < 0 || centerY < 0 || centerX >= largeur || centerY >= hauteur) {
            System.out.println("⚠️ ERREUR: Les coordonnées matricielles sont hors limites");
            probleme = true;
        }
        
        // Vérification de la présence d'une case centrale
        if (c == null || !(c instanceof Case)) {
            System.out.println("⚠️ ERREUR: La case centrale n'est pas une Case valide");
            probleme = true;
        }
        
        // On vérifie si les voisins théoriquement présents ont été détectés
        if (centerY - 2 >= 0 && voisinHaut == VALEUR_NON_INITIALISEE) {
            System.out.println("⚠️ ERREUR: Le voisin du haut n'est pas détecté mais devrait l'être");
            probleme = true;
        }
        
        if (centerX - 2 >= 0 && voisinGauche == VALEUR_NON_INITIALISEE) {
            System.out.println("⚠️ ERREUR: Le voisin de gauche n'est pas détecté mais devrait l'être");
            probleme = true;
        }
        
        if (centerX + 2 < largeur && voisinDroite == VALEUR_NON_INITIALISEE) {
            System.out.println("⚠️ ERREUR: Le voisin de droite n'est pas détecté mais devrait l'être");
            probleme = true;
        }
        
        if (centerY + 2 < hauteur && voisinBas == VALEUR_NON_INITIALISEE) {
            System.out.println("⚠️ ERREUR: Le voisin du bas n'est pas détecté mais devrait l'être");
            probleme = true;
        }
        
        if (!probleme) {
            System.out.println("✅ Tout fonctionne correctement!");
        } else {
            System.out.println("\n🔍 CONSEIL: Vérifiez les calculs de conversion des coordonnées");
            System.out.println("  - Pour une case logique (x,y), les indices matriciels sont ((x*2)-1, (y*2)-1)");
            System.out.println("  - L'écart entre les cases est de 2 dans la matrice");
        }
    }
}