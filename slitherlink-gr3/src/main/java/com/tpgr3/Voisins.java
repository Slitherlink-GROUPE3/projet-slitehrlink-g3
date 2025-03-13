package com.tpgr3;

/**
 * Classe permettant de récupérer les voisins d'une case
 * @param grille : la grille de jeu
 * @param x : coordonnée x de la case   (coordonées matrices)
 * @param y : coordonnée y de la case   (coordonées matrices)
 */
public class Voisins implements Constantes {
    private Cellule c;
    private Grille grille;
    private int hauteur;
    private int largeur;
    private Cellule[][] CellTab;  // Cette variable n'est jamais initialisée correctement

    private int voisinHaut;
    private int voisinHautGauche;
    private int voisinHautDroite;
    private int voisinBas;
    private int voisinBasGauche;
    private int voisinBasDroite;
    private int voisinGauche;
    private int voisinDroite;
    private int x,y;


    
    public Voisins(Grille grille, int x, int y) {
        this.grille = grille;
        this.c = grille.getCellule(x*2, y*2); 
        this.hauteur = grille.getHauteur();
        this.largeur = grille.getLargeur();
        this.CellTab = grille.getMatrice(); // CORRECTION: utilisez this. pour accéder au membre de classe
        this.voisinHaut = VALEUR_NON_INITIALISEE;
        this.voisinHautGauche = VALEUR_NON_INITIALISEE;
        this.voisinHautDroite = VALEUR_NON_INITIALISEE;
        this.voisinBas = VALEUR_NON_INITIALISEE;
        this.voisinBasGauche = VALEUR_NON_INITIALISEE;
        this.voisinBasDroite = VALEUR_NON_INITIALISEE;
        this.voisinGauche = VALEUR_NON_INITIALISEE;
        this.voisinDroite = VALEUR_NON_INITIALISEE;
        this.x = x;
        this.y = y;
    }

    public void afficher(){
        System.out.println("c = " + c.getValeur());
    }

    public void TrouverVoisins(){
        System.out.println("Calcul des voisins");
        if(this.x > 0){
            if(this.y > 0){
                if(x*2-1 < this.largeur && y*2-1 < this.hauteur && grille.getCellule(x*2-1, y*2-1) instanceof Case){
                    this.voisinHautGauche = grille.getCellule(x*2-1, y*2-1).getValeur();
                }
                else{
                    System.out.println("Pas de voisin en haut à gauche");
                    this.voisinHautGauche = HORS_LIMITES;
                }
                if(x*2+1 < this.largeur && y*2-1 < this.hauteur && grille.getCellule(x*2+1, y*2-1) instanceof Case){
                    this.voisinHautDroite = grille.getCellule(x*2+1, y*2-1).getValeur();
                }
                else{
                    System.out.println("Pas de voisin en haut à droite");
                    this.voisinHautDroite = HORS_LIMITES;
                }
                if(x*2 < this.largeur && y*2-2 < this.hauteur && grille.getCellule(x*2, y*2-2) instanceof Case){
                    this.voisinHaut = grille.getCellule(x*2, y*2-2).getValeur();
                }
                else{
                    System.out.println("Pas de voisin en haut");
                    this.voisinHaut = HORS_LIMITES;
                }
                if(x*2-1 < this.largeur && y*2 < this.hauteur && grille.getCellule(x*2-1, y*2) instanceof Case){
                    this.voisinGauche = grille.getCellule(x*2-1, y*2).getValeur();
                }
                else{
                    System.out.println("Pas de voisin à gauche");
                    this.voisinGauche = HORS_LIMITES;
                }
                if(x*2+1 < this.largeur && y*2 < this.hauteur && grille.getCellule(x*2+1, y*2) instanceof Case){
                    this.voisinDroite = grille.getCellule(x*2+1, y*2).getValeur();
                }
                else{
                    System.out.println("Pas de voisin à droite");
                    this.voisinDroite = HORS_LIMITES;
                }
                if(x*2 < this.largeur && y*2+1 < this.hauteur && grille.getCellule(x*2, y*2+1) instanceof Case){
                    this.voisinBas = grille.getCellule(x*2, y*2+1).getValeur();
                }
                else{
                    System.out.println("Pas de voisin en bas");
                    this.voisinBas = HORS_LIMITES;
                }
                if(x*2-1 < this.largeur && y*2+1 < this.hauteur && grille.getCellule(x*2-1, y*2+1) instanceof Case){
                    this.voisinBasGauche = grille.getCellule(x*2-1, y*2+1).getValeur();
                }
                else{
                    System.out.println("Pas de voisin en bas à gauche");
                    this.voisinBasGauche = HORS_LIMITES;
                }
                if(x*2+1 < this.largeur && y*2+1 < this.hauteur && grille.getCellule(x*2+1, y*2+1) instanceof Case){
                    this.voisinBasDroite = grille.getCellule(x*2+1, y*2+1).getValeur();
                }
                else{
                    System.out.println("Pas de voisin en bas à droite");
                    this.voisinBasDroite = HORS_LIMITES;
                }
            }
        }
    }

    // Ajoutez ces méthodes à votre classe Voisins

    public int getVoisinHaut() {
        return voisinHaut;
    }

    public int getVoisinHautGauche() {
        return voisinHautGauche;
    }

    public int getVoisinHautDroite() {
        return voisinHautDroite;
    }

    public int getVoisinBas() {
        return voisinBas;
    }

    public int getVoisinBasGauche() {
        return voisinBasGauche;
    }

    public int getVoisinBasDroite() {
        return voisinBasDroite;
    }

    public int getVoisinGauche() {
        return voisinGauche;
    }

    public int getVoisinDroite() {
        return voisinDroite;
    }

    // Méthode de test pour valider le fonctionnement
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