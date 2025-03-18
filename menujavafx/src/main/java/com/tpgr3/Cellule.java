package com.tpgr3;

// Classe abstraite pour les cellules de la grille
public abstract class Cellule implements Constantes {
    protected int x, y;


    public Cellule(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void actionner(){
        System.out.println("Nous sommes dans la méthode actionner de " + this.getClass().getSimpleName());
    };

    public abstract char afficher();

    public int getValeur() {
        return CASE_NON_INITIALISEE;
    }

    /*getters */
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    
}