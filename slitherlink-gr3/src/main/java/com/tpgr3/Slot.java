package com.tpgr3;
import com.tpgr3.Marque.Baton;
import com.tpgr3.Marque.Croix;
import com.tpgr3.Marque.Marque;
import com.tpgr3.Marque.Neutre;

public class Slot extends Cellule {
    private Marque marque;

    public Slot(int x, int y) {
        super(x, y);
        this.marque = new Neutre();
    }

    /*
     * Actionne le slot en changeant sa marque
     */
    @Override
    public void actionner() {
        super.actionner();
        System.out.println("contenu : '" + marque.afficher() + "'");

        // debug();// appel à la methode de debug
        if (marque instanceof Baton) {
            marque = new Croix();
        } else if (marque instanceof Croix) {
            marque = new Neutre();
        } else {
            marque = new Baton();
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
        return marque.afficher(); // Affiche le symbole du slot
    }


    @Override
    public int getValeur() {
        return marque.getValeur();
    }

    public Marque getMarque() {
        return marque;
    }

    public void setMarque(Marque marque) {
        this.marque = marque;
    }

}