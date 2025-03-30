package com.menu.javafx;

public class TechniqueDescriptions {
    public static String getDescription(String techniqueName) {
        return switch(techniqueName) {
            case "Tech_0Rule" -> 
                "Règle du 0 : Aucun segment ne doit entourer une case contenant un 0. " +
                "Marquez tous les segments adjacents avec une croix.";
            
            case "Tech_CornerRule" -> 
                "Règle des coins : Dans les coins de la grille, le nombre de segments est limité " +
                "selon la valeur de la case (0, 1, 2 ou 3).";
            
            case "Tech_ThreeRule" -> 
                "Règle du 3 : Exactement trois segments doivent entourer une case contenant un 3.";
            
            case "Tech_3InCorner" -> 
                "3 dans un angle : Lorsqu'un 3 est dans un coin, il faut tracer les deux segments " +
                "dans l'angle plus un troisième.";
            
            case "Tech_2InCorner" -> 
                "2 dans un angle : Lorsqu'un 2 est dans un coin, placez deux bâtons à l'opposé de l'angle.";
            
            case "Tech_1OnSide" -> 
                "1 sur un côté : Si un bâton est déjà placé sur un bord à côté d'un 1, " +
                "le segment opposé doit être marqué d'une croix.";
            
            case "Tech_0And3" -> 
                "0 et 3 adjacents : Quand un 0 et un 3 sont côte à côte, placez une croix sur le segment " +
                "entre eux et trois bâtons autour du 3.";
            
            case "Tech_0And3Diagonal" -> 
                "0 et 3 en diagonale : Placez des croix autour du 0 et placez des bâtons sur deux " +
                "côtés spécifiques du 3.";
            
            case "Tech_0And2OnSide" -> 
                "0 et 2 sur un côté : Placez un bâton à l'opposé du bord et un autre à l'opposé du 0, " +
                "puis ajoutez deux bâtons aux extrémités.";
            
            case "Tech_3And0OnSide" -> 
                "3 et 0 sur un côté : Placez deux bâtons - un contre le côté de la grille et " +
                "un du côté opposé au 0.";
            
            case "Tech_Two3Adjacent" -> 
                "Deux 3 adjacents : Placez les segments extérieurs des deux cases 3 et " +
                "laissez le segment commun vide.";
            
            case "Tech_Two3Diagonal" -> 
                "Deux 3 en diagonale : Placez quatre bâtons aux positions spécifiques pour éviter " +
                "de créer des angles entre les deux 3.";
            
            case "Tech_1And0InCorner" -> 
                "1 et 0 dans un angle : Avec un 1 dans l'angle à côté d'un 0, placez un bâton unique " +
                "dans la direction appropriée et deux autres bâtons en continuité.";
            
            case "Tech_1And2InCorner" -> 
                "1 et 2 dans un angle : Avec un 1 dans l'angle à côté d'un 2, placez des croix " +
                "dans l'angle car il sera impossible de respecter la condition du 1.";
            
            case "Tech_2And1InCorner" -> 
                "2 et 1 dans un angle : Utilisez la règle du 2 dans l'angle pour placer deux bâtons, " +
                "puis placez un bâton supplémentaire à côté du 1.";
            
            case "Tech_Two3InCorner" -> 
                "Deux 3 dans un angle : Combinez les techniques des 3 côte à côte et du 3 dans l'angle " +
                "pour déduire des placements obligatoires.";
            
            case "Tech_LoopReaching1" -> 
                "Détection de boucle pour 1 : Lorsqu'un bâton est déjà placé à côté d'un 1, " +
                "les autres segments autour doivent être marqués d'une croix.";
            
            case "Tech_LoopReaching3" -> 
                "Détection de boucle pour 3 : Analyse les contraintes autour des cases à 3 " +
                "pour déterminer les segments manquants.";
            
            default -> 
                "Technique de résolution qui utilise les contraintes des chiffres pour " +
                "déduire le placement correct des segments.";
        };
    }
}