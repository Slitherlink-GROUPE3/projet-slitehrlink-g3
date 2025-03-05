package com.tpgr3;

// Les slots où l’on peut placer un trait ou une croix
class Slot extends Cellule {
    private char marque; // '-' pour trait, 'X' pour croix, ' ' pour vide

    public Slot(int x, int y) {
        super(x, y);
        this.marque =' ';
    }

    /*
     * Actionne le slot en changeant sa marque
     */
    @Override
    public void actionner() {
        super.actionner();
        // debug();// appel à la methode de debug
        if (marque == '-') {
            marque = 'X';
        } else if (marque == 'X') {
            marque = ' ';
        } else {
            marque = '-';
        }
    }


    public void clearConsole(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /*
     * Methode de débug pour afficher les coordonnées du slot et clear la console 
     */
    public void debug() {
        clearConsole();
        System.out.println("Slot (" + x + ", " + y + ")");
    }

    @Override
    public char afficher() {
        return marque; // Affiche le symbole du slot
    }
}