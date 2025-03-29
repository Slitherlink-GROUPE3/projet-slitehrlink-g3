package com.tpgr3.backend;

import com.tpgr3.backend.Marque.*;

public class Slot extends Cellule {

    private Marque marque;
    private Grille grille;

    /**
     * Constructeur d’un Slot.
     * @param x Position X dans la grille
     * @param y Position Y dans la grille
     * @param grille Référence à l'objet Grille pour enregistrer les coups et vérifier la solution
     */
    public Slot(int x, int y, Grille grille) {
        super(x, y);
        this.marque = new Neutre();
        this.grille = grille;
    }

    /**
     * Change successivement l'état du slot (Neutre → Baton → Croix → Neutre),
     * enregistre cette modification dans l’historique,
     * puis appelle la vérification automatique de la solution.
     */
    @Override
    public void actionner() {
        // État avant changement
        Marque avant = marque;

        // Cycle baton/croix/neutre
        if (marque instanceof Baton) {
            marque = new Croix();
        } else if (marque instanceof Croix) {
            marque = new Neutre();
        } else {
            marque = new Baton();
        }

        // Historiser ce changement
        grille.enregistrerCoup(x, y, avant, marque);

        // Vérification automatique (check) après chaque coup
        // ==> Tu dois avoir une méthode dans Grille, ex: grille.verifierSolutionSiPossible();
        grille.verifierSolutionSiPossible();
    }

    /**
     * Affiche le symbole de la marque courante du slot.
     */
    @Override
    public char afficher() {
        return marque.afficher();
    }

    /**
     * Retourne un code entier associé à la marque (BATON/CROIX/NEUTRE).
     */
    @Override
    public int getValeur() {
        return marque.getValeur();
    }

    /**
     * Retourne la marque courante (Baton, Croix, Neutre).
     */
    public Marque getMarque() {
        return marque;
    }

    /**
     * Affecte une nouvelle marque au slot (utile pour charger des coups depuis un JSON).
     */
    public void setMarque(Marque marque) {
        this.marque = marque;
    }

    /*
     * Méthodes de debug si besoin
     */
    public void debug() {
        clearConsole();
        System.out.println("Slot (" + x + ", " + y + ")");
    }

    public void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
