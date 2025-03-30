package com.menu.javafx;

import com.tpgr3.Techniques.*;
import java.util.Arrays;
import java.util.List;

public class TechniquesPriority {
    // Ordre de priorité des techniques de résolution
    public static final List<Class<? extends Techniques>> PRIORITY_ORDER = Arrays.asList(
        // Règles de base et simples en premier

        Tech_0Rule.class,             // Règle du 0 : aucune ligne autour d'un 0
        Tech_3InCorner.class,         // 3 dans un angle
        Tech_2InCorner.class,         // 2 dans un angle
        Tech_0And3.class,             // 0 et 3 adjacents
        Tech_0And3Diagonal.class,     // 0 et 3 en diagonale
        Tech_0And2OnSide.class,       // 0 et 2 sur un côté
        Tech_3And0OnSide.class,       // 3 et 0 sur un côté
        Tech_Two3Adjacent.class,      // Deux 3 adjacents
        Tech_Two3Diagonal.class,      // Deux 3 en diagonale
        Tech_1And0InCorner.class,     // 1 et 0 dans un angle
        Tech_1And2InCorner.class,     // 1 et 2 dans un angle
        Tech_2And1InCorner.class,     // 2 et 1 dans un angle
        Tech_Two3InCorner.class,     // Deux 3 dans un angle
        Tech_2InAngle.class         // 2 dans un angle

        //Tech_1OnSide.class,           // 1 sur un côté de la grille
        //Tech_LoopReaching1.class,     // Détection de boucle pour les 1
        //Tech_LoopReaching3.class      // Détection de boucle pour les 3
    );
}