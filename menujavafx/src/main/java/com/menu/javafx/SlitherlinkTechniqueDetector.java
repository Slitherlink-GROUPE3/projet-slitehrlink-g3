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

        private String traduireTechnique(String techniqueName) {
        return switch (techniqueName) {
            case "Tech_0Rule" -> "Règle du 0 (aucune ligne)";
            case "Tech_CornerRule" -> "Règle des coins";
            case "Tech_ThreeRule" -> "Règle du 3";
            case "Tech_3InCorner" -> "3 dans un angle";
            case "Tech_2InCorner" -> "2 dans un angle";
            case "Tech_1OnSide" -> "1 sur un côté";
            case "Tech_0And3" -> "0 et 3 adjacents";
            case "Tech_0And3Diagonal" -> "0 et 3 en diagonale";
            case "Tech_0And2OnSide" -> "0 et 2 sur un côté";
            case "Tech_3And0OnSide" -> "3 et 0 sur un côté";
            case "Tech_Two3Adjacent" -> "Deux 3 adjacents";
            case "Tech_Two3Diagonal" -> "Deux 3 en diagonale";
            case "Tech_1And0InCorner" -> "1 et 0 dans un angle";
            case "Tech_1And2InCorner" -> "1 et 2 dans un angle";
            case "Tech_2And1InCorner" -> "2 et 1 dans un angle";
            case "Tech_Two3InCorner" -> "Deux 3 dans un angle";
            case "Tech_LoopReaching1" -> "Détection de boucle pour 1";
            case "Tech_LoopReaching3" -> "Détection de boucle pour 3";
            case "Tech_2InAngle" -> "2 en diagonal d'un chemin";
            default -> techniqueName;
        };
    }
}