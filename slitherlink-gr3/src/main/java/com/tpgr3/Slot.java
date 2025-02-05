package com.tpgr3;

// Les slots où l’on peut placer un trait ou une croix
class Slot extends Cellule {
    private char marque; // '-' pour trait, 'X' pour croix, ' ' pour vide

    public Slot(int x, int y) {
        super(x, y);
        this.marque = ' ';
    }

    @Override
    public void actionner() {
        if (marque == '-') {
            marque = 'X';
        } else if (marque == 'X') {
            marque = ' ';
        } else {
            marque = '-';
        }
    }

    @Override
    public char afficher() {
        return marque; // Affiche le symbole du slot
    }
}