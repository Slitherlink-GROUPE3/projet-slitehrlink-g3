package com.tpgr3.backend.Techniques;

import com.tpgr3.backend.Grille;
import java.util.List;

public interface Techniques {
    boolean estApplicable(Grille grille);
    void appliquer(Grille grille);
    
    /**
     * Retourne la liste des coordonnées des slots à marquer sans les modifier.
     * @param grille La grille à analyser
     * @return Liste de coordonnées [x,y] des slots à marquer
     */
    List<int[]> getSlotsToMark(Grille grille);
}