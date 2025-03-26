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

    public String getRapportTechniques() {
        StringBuilder rapport = new StringBuilder("Rapport des techniques de résolution :\n\n");
        
        for (Class<? extends Techniques> techniqueClass : TechniquesPriority.PRIORITY_ORDER) {
            try {
                Techniques technique;
                
                // Essayer différents constructeurs
                try {
                    // Premier essai : constructeur avec 3 paramètres
                    technique = techniqueClass
                        .getConstructor(int[][].class, Map.class, Pane.class)
                        .newInstance(gridNumbers, gridLines, slitherlinkGrid);
                } catch (NoSuchMethodException e1) {
                    try {
                        // Deuxième essai : constructeur sans paramètres
                        technique = techniqueClass.getConstructor().newInstance();
                    } catch (NoSuchMethodException e2) {
                        // Si aucun constructeur ne fonctionne
                        rapport.append("• ")
                               .append(traduireTechnique(techniqueClass.getSimpleName()))
                               .append(" : ❌ ERREUR DE CONSTRUCTEUR\n");
                        continue;
                    }
                }
                
                // Vérifier l'applicabilité
                boolean isApplicable = technique.estApplicable(slitherGrid);
                
                // Ajouter une ligne au rapport
                rapport.append("• ")
                       .append(traduireTechnique(techniqueClass.getSimpleName()))
                       .append(" : ")
                       .append(isApplicable ? "🟢 APPLICABLE" : "🔴 NON APPLICABLE")
                       .append("\n");
            } catch (Exception e) {
                rapport.append("• ")
                       .append(traduireTechnique(techniqueClass.getSimpleName()))
                       .append(" : ❌ ERREUR DE VÉRIFICATION\n");
                e.printStackTrace();
            }
        }
        
        return rapport.toString();
    }

    // Méthode pour traduire le nom technique
    private String traduireTechnique(String techniqueName) {
        switch (techniqueName) {
            case "Tech_0Rule": return "Règle du 0 (aucune ligne)";
            case "Tech_CornerRule": return "Règle des coins";
            case "Tech_ThreeRule": return "Règle du 3";
            case "Tech_0And3Adjacent": return "0 et 3 adjacents";
            case "Tech_0And3Diagonal": return "0 et 3 en diagonale";
            case "Tech_Two3Adjacent": return "Deux 3 adjacents";
            case "Tech_Two3Diagonal": return "Deux 3 en diagonale";
            case "Tech_LoopReaching1": return "Détection de boucle pour 1";
            case "Tech_LoopReaching3": return "Détection de boucle pour 3";
            default: return techniqueName;
        }
    }
}