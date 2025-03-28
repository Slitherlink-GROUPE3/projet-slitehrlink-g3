package com.menu.javafx;

import com.tpgr3.Techniques.*;
import java.util.Arrays;
import java.util.List;

public class TechniquesPriority {
    // Ordre de priorité des techniques de résolution
    public static final List<Class<? extends Techniques>> PRIORITY_ORDER = Arrays.asList(
        // Règles de base et simples en premier
        Tech_0Rule.class,           // Règle du 0 : aucune ligne autour d'un 0
        Tech_CornerRule.class,       // Règles des coins
        
        // Techniques impliquant des chiffres spécifiques
        Tech_ThreeRule.class,        // Règle du 3
        Tech_0And3Adjacent.class,    // 0 et 3 adjacents
        Tech_0And3Diagonal.class,    // 0 et 3 en diagonale
        
        // Techniques de boucle avancées
        Tech_Two3Adjacent.class,     // Deux 3 adjacents
        //Tech_Two3Diagonal.class,     // Deux 3 en diagonale
        
        // Techniques de détection de loop avancées
        Tech_LoopReaching1.class,    // Détection de loop pour les 1
        Tech_LoopReaching3.class     // Détection de loop pour les 3
    );
}