<<<<<<< HEAD
package com.tpgr3.Techniques;

=======
// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

>>>>>>> 12cd25160dfc23eed9267d004e81170d606aac35
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