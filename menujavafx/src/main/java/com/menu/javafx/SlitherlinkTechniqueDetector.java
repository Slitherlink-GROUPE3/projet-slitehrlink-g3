package com.menu.javafx;

import com.tpgr3.Grille;
import com.tpgr3.Techniques.Techniques;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlitherlinkTechniqueDetector {
    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    private Grille slitherGrid;

    /**
     * Constructeur principal.
     * @param gridNumbers Nombres de la grille
     * @param gridLines Lignes de la grille
     * @param slitherlinkGrid Panneau de la grille
     */
    public SlitherlinkTechniqueDetector(int[][] gridNumbers, 
                                        Map<String, Line> gridLines, 
                                        Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
        
        // Créer une grille Slitherlink à partir des nombres
        this.slitherGrid = new Grille(gridNumbers);
    }

    /**
     * Vérifie si une technique spécifique est applicable.
     * @param techniqueClass La classe de la technique à vérifier
     * @return true si la technique est applicable, false sinon
     */
    public boolean estTechniqueApplicable(Class<? extends Techniques> techniqueClass) {
        try {
            // Utiliser le constructeur standard des techniques
            Techniques technique = techniqueClass
                .getConstructor(int[][].class, Map.class, Pane.class)
                .newInstance(gridNumbers, gridLines, slitherlinkGrid);
            
            // Passer la vraie grille Slitherlink
            return technique.estApplicable(slitherGrid);
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de la technique " + 
                               techniqueClass.getSimpleName());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtient la liste de toutes les techniques applicables.
     * @return Liste des techniques qui sont applicables dans la grille actuelle
     */
    public List<Class<? extends Techniques>> getTechniquesApplicables() {
        List<Class<? extends Techniques>> techniquesApplicables = new ArrayList<>();
        
        for (Class<? extends Techniques> techniqueClass : TechniquesPriority.PRIORITY_ORDER) {
            if (estTechniqueApplicable(techniqueClass)) {
                techniquesApplicables.add(techniqueClass);
            }
        }
        
        return techniquesApplicables;
    }
}