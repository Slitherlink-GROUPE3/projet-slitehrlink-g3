package com.tpgr3.Techniques;

import com.tpgr3.Grille;

public interface Techniques {
    /**
     * Vérifie si une technique est applicable sur la grille.
     * @param grille La grille de jeu.
     * @return true si la technique peut être appliquée, false sinon.
     */
    boolean estApplicable(Grille grille);
    void appliquer(Grille grille);
}