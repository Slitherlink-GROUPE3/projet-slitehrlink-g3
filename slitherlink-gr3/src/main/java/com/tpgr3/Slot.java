package com.tpgr3;

// Les slots où l’on peut placer un trait ou une croix
class Slot extends Cellule {
    private char marque; // '-' pour trait, 'X' pour croix, ' ' pour vide

    public Slot(int x, int y) {
        super(x, y);
        this.marque = ' ';
    }

    /*
     * Actionne le slot en changeant sa marque
     */
    @Override
    public void actionner() {
        debug();// appel à la methode de debug
        System.out.println("Nous sommes dans la méthode actionner() de " + this.getClass().getName());
        if (marque == '-') {
            marque = 'X';
        } else if (marque == 'X') {
            marque = ' ';
        } else {
            marque = '-';
        }
    }

    /*
     * Methode de débug pour afficher les coordonnées du slot et clear la console 
     */
    public void debug() {
        // clear la console
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("Slot (" + x + ", " + y + ")");

    }

    @Override
    public char afficher() {
        return marque; // Affiche le symbole du slot
    }
}