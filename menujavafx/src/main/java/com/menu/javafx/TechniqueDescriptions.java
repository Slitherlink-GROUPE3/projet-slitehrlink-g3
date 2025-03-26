package com.menu.javafx;

public class TechniqueDescriptions {
    public static String getDescription(String techniqueName) {
        return switch(techniqueName) {
            case "Tech_0Rule" -> 
                "La règle du 0 est une technique fondamentale dans la résolution des puzzles Slitherlink. " +
                "Lorsqu'une case contient un 0, cela signifie qu'aucun segment ne doit être tracé autour de cette case. " +
                "Vous devez donc marquer tous les segments adjacents à cette case avec une croix, " +
                "indiquant qu'ils ne font pas partie de la boucle finale.";
            
            case "Tech_CornerRule" -> 
                "La règle des coins est cruciale pour la résolution des puzzles Slitherlink. " +
                "Dans les coins de la grille, le nombre de segments possibles est limité. " +
                "Cette technique examine les contraintes spécifiques aux cases situées dans les coins, " +
                "en tenant compte du nombre de segments déjà tracés et des possibilités restantes.";
            
            case "Tech_ThreeRule" -> 
                "La règle du 3 est une technique de résolution puissante dans les puzzles Slitherlink. " +
                "Lorsqu'une case contient un 3, cela indique que trois segments doivent obligatoirement " +
                "être tracés autour de cette case. Cette règle permet de déduire avec certitude " +
                "l'emplacement des segments de la boucle dans les cases présentant un 3.";
            
            case "Tech_0And3Adjacent" -> 
                "Cette technique examine les relations entre les cases contenant 0 et 3 qui sont adjacentes. " +
                "Quand un 0 et un 3 sont côte à côte, leurs positions relatives peuvent fournir des " +
                "informations cruciales sur le placement des segments. La proximité de ces cases " +
                "permet de déduire avec précision quels segments doivent être tracés ou marqués.";
            
            case "Tech_0And3Diagonal" -> 
                "Cette technique avancée analyse les relations entre les cases 0 et 3 qui sont en diagonale. " +
                "Lorsque ces cases sont positionnées en diagonale, leurs interactions peuvent révéler " +
                "des indices subtils sur la configuration de la boucle. Elle permet de déduire des " +
                "placements de segments qui ne seraient pas évidents avec d'autres techniques.";
            
            case "Tech_Two3Adjacent" -> 
                "La technique des deux 3 adjacents est une méthode de résolution stratégique. " +
                "Lorsque deux cases contenant un 3 sont côte à côte, leurs contraintes combinées " +
                "peuvent considérablement réduire les possibilités de placement des segments. " +
                "Cette technique permet de déduire avec précision l'emplacement de certains segments.";
            
            case "Tech_Two3Diagonal" -> 
                "Cette technique complexe examine les relations entre deux cases à 3 qui sont en diagonale. " +
                "La position diagonale de ces cases peut créer des contraintes uniques sur la configuration " +
                "de la boucle. Elle permet de déduire des placements de segments qui nécessitent une " +
                "analyse géométrique plus approfondie.";
            
            case "Tech_LoopReaching1" -> 
                "La technique de détection de boucle pour les cases à 1 se concentre sur les contraintes " +
                "spécifiques autour des cases contenant un 1. Comme un seul segment doit être tracé, " +
                "cette technique permet d'identifier avec précision les possibilités de placement " +
                "du segment unique autour de la case.";
            
            case "Tech_LoopReaching3" -> 
                "La technique de détection de boucle pour les cases à 3 est une méthode avancée. " +
                "Elle analyse en profondeur les contraintes autour des cases à 3, en utilisant " +
                "les informations sur les segments existants pour déduire le placement précis " +
                "des segments manquants pour compléter la configuration.";
            
            default -> 
                "Technique de résolution spécifique pour les puzzles Slitherlink. " +
                "Cette technique offre des stratégies pour déduire le placement des segments " +
                "en fonction des contraintes numériques de la grille.";
        };
    }
}
