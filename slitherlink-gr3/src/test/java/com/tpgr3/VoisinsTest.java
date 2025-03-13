package com.tpgr3;

public class Voisins implements Constantes {
    private Cellule c;
    private Grille grille;
    private int hauteur;
    private int largeur;
    private Cellule[][] CellTab;

    private int voisinHaut;
    private int voisinHautGauche;
    private int voisinHautDroite;
    private int voisinBas;
    private int voisinBasGauche;
    private int voisinBasDroite;
    private int voisinGauche;
    private int voisinDroite;
    // Pour Voisins, nous sauvegardons désormais les coordonnées du centre (Case) dans la grille complète
    private int centerX, centerY;

    public Voisins(Grille grille, int x, int y) {
        this.grille = grille;
        // Correction : la case numérique se trouve aux indices impairs :
        // (x*2+1, y*2+1)
        this.centerX = x * 2 + 1;
        this.centerY = y * 2 + 1;
        this.c = grille.getCellule(centerX, centerY);
        this.hauteur = grille.getHauteur();
        this.largeur = grille.getLargeur();
        this.CellTab = grille.getMatrice();
        this.voisinHaut = VALEUR_NON_INITIALISEE;
        this.voisinHautGauche = VALEUR_NON_INITIALISEE;
        this.voisinHautDroite = VALEUR_NON_INITIALISEE;
        this.voisinBas = VALEUR_NON_INITIALISEE;
        this.voisinBasGauche = VALEUR_NON_INITIALISEE;
        this.voisinBasDroite = VALEUR_NON_INITIALISEE;
        this.voisinGauche = VALEUR_NON_INITIALISEE;
        this.voisinDroite = VALEUR_NON_INITIALISEE;
    }

    public void afficher(){
        System.out.println("Centre (Case) : " + c.getValeur());
    }

    public void TrouverVoisins() {
        System.out.println("Calcul des voisins pour la case au centre (" + centerX + "," + centerY + ")");
        
        // Voisin en haut : (centerX, centerY - 1)
        if(centerY - 1 >= 0 && grille.estValide(centerX, centerY - 1)
           && grille.getCellule(centerX, centerY - 1) instanceof Case) {
            this.voisinHaut = grille.getCellule(centerX, centerY - 1).getValeur();
        } else {
            System.out.println("Pas de voisin en haut");
            this.voisinHaut = HORS_LIMITES;
        }
        
        // Voisin en haut à gauche : (centerX - 1, centerY - 1)
        if(centerX - 1 >= 0 && centerY - 1 >= 0 && grille.estValide(centerX - 1, centerY - 1)
           && grille.getCellule(centerX - 1, centerY - 1) instanceof Case) {
            this.voisinHautGauche = grille.getCellule(centerX - 1, centerY - 1).getValeur();
        } else {
            System.out.println("Pas de voisin en haut à gauche");
            this.voisinHautGauche = HORS_LIMITES;
        }
        
        // Voisin en haut à droite : (centerX + 1, centerY - 1)
        if(centerX + 1 < largeur && centerY - 1 >= 0 && grille.estValide(centerX + 1, centerY - 1)
           && grille.getCellule(centerX + 1, centerY - 1) instanceof Case) {
            this.voisinHautDroite = grille.getCellule(centerX + 1, centerY - 1).getValeur();
        } else {
            System.out.println("Pas de voisin en haut à droite");
            this.voisinHautDroite = HORS_LIMITES;
        }
        
        // Voisin à gauche : (centerX - 1, centerY)
        if(centerX - 1 >= 0 && grille.estValide(centerX - 1, centerY)
           && grille.getCellule(centerX - 1, centerY) instanceof Case) {
            this.voisinGauche = grille.getCellule(centerX - 1, centerY).getValeur();
        } else {
            System.out.println("Pas de voisin à gauche");
            this.voisinGauche = HORS_LIMITES;
        }
        
        // Voisin à droite : (centerX + 1, centerY)
        if(centerX + 1 < largeur && grille.estValide(centerX + 1, centerY)
           && grille.getCellule(centerX + 1, centerY) instanceof Case) {
            this.voisinDroite = grille.getCellule(centerX + 1, centerY).getValeur();
        } else {
            System.out.println("Pas de voisin à droite");
            this.voisinDroite = HORS_LIMITES;
        }
        
        // Voisin en bas : (centerX, centerY + 1)
        if(grille.estValide(centerX, centerY + 1)
           && grille.getCellule(centerX, centerY + 1) instanceof Case) {
            this.voisinBas = grille.getCellule(centerX, centerY + 1).getValeur();
        } else {
            System.out.println("Pas de voisin en bas");
            this.voisinBas = HORS_LIMITES;
        }
        
        // Voisin en bas à gauche : (centerX - 1, centerY + 1)
        if(centerX - 1 >= 0 && grille.estValide(centerX - 1, centerY + 1)
           && grille.getCellule(centerX - 1, centerY + 1) instanceof Case) {
            this.voisinBasGauche = grille.getCellule(centerX - 1, centerY + 1).getValeur();
        } else {
            System.out.println("Pas de voisin en bas à gauche");
            this.voisinBasGauche = HORS_LIMITES;
        }
        
        // Voisin en bas à droite : (centerX + 1, centerY + 1)
        if(centerX + 1 < largeur && grille.estValide(centerX + 1, centerY + 1)
           && grille.getCellule(centerX + 1, centerY + 1) instanceof Case) {
            this.voisinBasDroite = grille.getCellule(centerX + 1, centerY + 1).getValeur();
        } else {
            System.out.println("Pas de voisin en bas à droite");
            this.voisinBasDroite = HORS_LIMITES;
        }
    }

    // Les getters restent inchangés
    public int getVoisinHaut() { return voisinHaut; }
    public int getVoisinHautGauche() { return voisinHautGauche; }
    public int getVoisinHautDroite() { return voisinHautDroite; }
    public int getVoisinBas() { return voisinBas; }
    public int getVoisinBasGauche() { return voisinBasGauche; }
    public int getVoisinBasDroite() { return voisinBasDroite; }
    public int getVoisinGauche() { return voisinGauche; }
    public int getVoisinDroite() { return voisinDroite; }
    
    public void testDetection() {
        TrouverVoisins();
        System.out.println("Voisins trouvés :");
        System.out.println("Haut : " + voisinHaut);
        System.out.println("Haut-Gauche : " + voisinHautGauche);
        System.out.println("Haut-Droite : " + voisinHautDroite);
        System.out.println("Gauche : " + voisinGauche);
        System.out.println("Droite : " + voisinDroite);
        System.out.println("Bas : " + voisinBas);
        System.out.println("Bas-Gauche : " + voisinBasGauche);
        System.out.println("Bas-Droite : " + voisinBasDroite);
    }
}