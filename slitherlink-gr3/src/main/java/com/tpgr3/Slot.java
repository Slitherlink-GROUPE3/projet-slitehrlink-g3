package main.java.com.tpgr3;

// Les slots où l’on peut placer un trait ou une croix
class Slot extends Cellule {
    private char marque; // '-' pour trait, 'X' pour croix, ' ' pour vide

    public Slot(int x, int y) {
        super(x, y);
        this.marque = ' ';
    }

    public void setMarque(char marque) {
        if (marque == '-' || marque == 'X' || marque == ' ') {
            this.marque = marque;
        }
    }

    @Override
    public char afficher() {
        return marque; // Affiche le symbole du slot
    }
}