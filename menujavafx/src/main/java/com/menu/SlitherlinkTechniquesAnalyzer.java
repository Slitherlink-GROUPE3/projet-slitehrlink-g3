package com.menu;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;

import java.util.Map;
import java.util.HashMap;

import com.menu.javafx.hintScene;
import com.tpgr3.Grille;
import com.tpgr3.Techniques.Tech_0Rule;
import com.tpgr3.Techniques.Tech_0And3Adjacent;
import com.tpgr3.Techniques.Technique0et3Diag;
import com.tpgr3.Techniques.Techniques3diag;
import com.tpgr3.Techniques.Techniques;

/**
 * Classe qui fait l'interface entre l'interface graphique du jeu Slitherlink
 * et les techniques de résolution existantes
 */
public class SlitherlinkTechniquesAnalyzer {
    
    // Constantes de couleurs du Menu (copiées de GameScene pour la cohérence)
    private static final String MAIN_COLOR = "#3A7D44"; // Vert principal
    private static final String SECONDARY_COLOR = "#F2E8CF"; // Beige clair
    private static final String ACCENT_COLOR = "#BC4749"; // Rouge-brique
    private static final String DARK_COLOR = "#386641"; // Vert foncé
    private static final String LIGHT_COLOR = "#A7C957"; // Vert clair
    
    // Pour éviter de répéter les mêmes suggestions
    private static Map<String, Integer> techniquesSuggested = new HashMap<>();
    private static String lastTechnique = "";
    
    private int[][] gridNumbers;
    private Map<String, Line> gridLines;
    private Pane slitherlinkGrid;
    
    /**
     * Constructeur
     *
     * @param gridNumbers La matrice contenant les valeurs numériques de la grille
     * @param gridLines La map contenant les références aux lignes de la grille
     * @param slitherlinkGrid Le Pane contenant tous les éléments visuels de la grille
     */
    public SlitherlinkTechniquesAnalyzer(int[][] gridNumbers, Map<String, Line> gridLines, Pane slitherlinkGrid) {
        this.gridNumbers = gridNumbers;
        this.gridLines = gridLines;
        this.slitherlinkGrid = slitherlinkGrid;
    }
    
    /**
     * Analyse l'état actuel de la grille et suggère une technique appropriée
     *
     * @param primaryStage Le stage principal de l'application
     */
    public void analyzeAndSuggestTechnique(Stage primaryStage) {
        try {
            System.out.println("Analyse démarrée");
            
            // Convertir la grille du format UI vers le format de traitement
            System.out.println("Conversion de la grille...");
            Grille grille = convertToLogicGrid();
            System.out.println("Grille convertie avec succès");
            
            // Tester chaque technique pour voir si elle est applicable
            System.out.println("Recherche de techniques applicables...");
            String technique = findApplicableTechnique(grille);
            System.out.println("Technique trouvée : " + technique);
            
            // Afficher l'aide correspondante dans une alerte
            System.out.println("Affichage de l'aide...");
            showTechniqueInAlert(primaryStage, technique);
            System.out.println("Aide affichée");
            
            // Mettre à jour la dernière technique suggérée
            lastTechnique = technique;
            // Incrémenter le compteur pour cette technique
            techniquesSuggested.put(technique, techniquesSuggested.getOrDefault(technique, 0) + 1);
        }
        catch (Exception e) {
            System.err.println("Erreur lors de l'analyse : " + e.getMessage());
            e.printStackTrace();
            
            // Afficher une alerte d'erreur 
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur d'analyse");
            alert.setHeaderText("Une erreur s'est produite lors de l'analyse de la grille");
            alert.setContentText("Détails : " + e.getMessage());
            alert.initOwner(primaryStage);
            alert.showAndWait();
        }
    }
    
    /**
     * Convertit la grille de l'interface utilisateur vers le format de traitement
     *
     * @return Une instance de Grille utilisable par les techniques de résolution
     */
    private Grille convertToLogicGrid() {
        try {
            // Debug
            System.out.println("Dimensions de gridNumbers : " + gridNumbers.length + "x" + gridNumbers[0].length);
            System.out.println("Création d'une nouvelle grille...");
            
            // Créer une nouvelle grille avec les nombres
            Grille grille = new Grille(gridNumbers);
            System.out.println("Grille créée avec succès");
            return grille;
        } 
        catch (Exception e) {
            System.err.println("Erreur lors de la création de la grille : " + e.getMessage());
            e.printStackTrace();
            throw e; // Propager l'erreur pour l'afficher à l'utilisateur
        }
    }
    
    /**
     * Trouve une technique applicable sur l'état actuel de la grille
     *
     * @param grille La grille de jeu au format logique
     * @return Le nom de la technique applicable, ou une technique par défaut si aucune n'est applicable
     */
    private String findApplicableTechnique(Grille grille) {
        // Tableau des techniques à tester dans l'ordre de priorité
        String[] techniquesToTest = {
            "technique_0",
            "technique_03_adjacent",
            "technique_03_diag",
            "technique_3_diag",
            "technique_general"
        };
        
        // Vérifier d'abord les situations spéciales
        
        // Vérifier si des cases 0 sont connectées à des segments (ce qui serait une erreur)
        boolean has0Connected = false;
        for (int i = 0; i < gridNumbers.length; i++) {
            for (int j = 0; j < gridNumbers[i].length; j++) {
                if (gridNumbers[i][j] == 0) {
                    // Vérifier s'il y a des segments autour de ce 0
                    if (countAdjacentActiveLines(i, j) > 0) {
                        has0Connected = true;
                        break;
                    }
                }
            }
            if (has0Connected) break;
        }
        
        if (has0Connected) {
            return "technique_0"; // Priorité: règle des 0 violée
        }
        
        // 1. Règle pour les 0 non complétés (avec des croix manquantes)
        for (int i = 0; i < gridNumbers.length; i++) {
            for (int j = 0; j < gridNumbers[i].length; j++) {
                if (gridNumbers[i][j] == 0) {
                    // Vérifier si tous les côtés ont des croix
                    if (countAdjacentCrosses(i, j) < 4) {
                        return "technique_0";
                    }
                }
            }
        }
        
        // Si une technique a été suggérée plus de 2 fois ou était la dernière suggérée, 
        // essayer d'en trouver une autre
        String foundTechnique = null;
        String alternateTechnique = null;
        
        // 2. Tester les autres techniques dans l'ordre
        for (String technique : techniquesToTest) {
            // Si c'est la dernière technique suggérée, la noter mais continuer à chercher
            if (technique.equals(lastTechnique)) {
                continue;
            }
            
            boolean applicable = false;
            
            switch (technique) {
                case "technique_0":
                    Tech_0Rule tech0 = new Tech_0Rule();
                    applicable = tech0.estApplicable(grille);
                    break;
                    
                case "technique_03_adjacent":
                    // Vérifier si 0 et 3 sont adjacents
                    Tech_0And3Adjacent tech03Adjacent = new Tech_0And3Adjacent();
                    applicable = tech03Adjacent.estApplicable(grille);
                    break;
                    
                case "technique_03_diag":
                    // Vérifier si 0 et 3 sont en diagonale
                    Technique0et3Diag tech03Diag = new Technique0et3Diag();
                    applicable = tech03Diag.estApplicable(grille);
                    break;
                    
                case "technique_3_diag":
                    // Vérifier si deux 3 sont en diagonale
                    Techniques3diag tech3Diag = new Techniques3diag();
                    applicable = tech3Diag.estApplicable(grille);
                    break;
                    
                case "technique_general":
                    // Toujours applicable
                    applicable = true;
                    break;
            }
            
            // Si la technique est applicable et n'a pas été suggérée trop souvent
            if (applicable) {
                int count = techniquesSuggested.getOrDefault(technique, 0);
                
                if (count < 2) {
                    // Cette technique n'a pas été trop utilisée, on la prend
                    foundTechnique = technique;
                    break;
                } else if (alternateTechnique == null) {
                    // Garder cette technique en réserve si on n'en trouve pas d'autre
                    alternateTechnique = technique;
                }
            }
        }
        
        // Si on a trouvé une technique, l'utiliser
        if (foundTechnique != null) {
            return foundTechnique;
        }
        
        // Utiliser la technique alternative si disponible, sinon la technique générale
        return alternateTechnique != null ? alternateTechnique : "technique_general";
    }
    
    /**
     * Affiche une alerte avec la technique suggérée.
     * 
     * @param primaryStage Le stage principal de l'application
     * @param techniqueName Le nom de la technique à afficher
     */
    private void showTechniqueInAlert(Stage primaryStage, String techniqueName) {
        String title = "";
        String text = "";
        
        switch (techniqueName) {
            case "technique_0":
                title = "Règle du 0";
                text = "Une case avec un 0 ne doit avoir aucun segment adjacent. Vous pouvez placer des croix sur tous les côtés d'un 0.";
                break;
                
            case "technique_03_adjacent":
                title = "0 et 3 adjacents";
                text = "Lorsqu'un 0 et un 3 sont adjacents, le 3 ne peut pas utiliser le segment entre lui et le 0. Les trois autres côtés du 3 doivent être des segments.";
                break;
                
            case "technique_03_diag":
                title = "0 et 3 en diagonale";
                text = "Lorsqu'un 0 et un 3 sont en diagonale, ils créent une configuration particulière. Le 3 doit avoir ses segments placés de manière à éviter le 0.";
                break;
                
            case "technique_3_diag":
                title = "3 en diagonale";
                text = "Lorsque deux cases contenant un 3 sont en diagonale l'une par rapport à l'autre, elles doivent partager une partie de leur boucle.";
                break;
                
            default:
                title = "Astuces générales";
                text = "- Commencez par les cases 0 et 3, qui sont les plus contraignantes.\n" +
                       "- Rappelez-vous que la solution est toujours une boucle unique sans branches.\n" +
                       "- Quand deux segments se rencontrent, il ne peut pas y avoir un troisième segment (cela créerait une branche).";
                break;
        }
        
        // Créer et configurer l'alerte
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Aide - " + title);
        alert.setHeaderText(title);
        alert.setContentText(text);
        
        // Faire en sorte que l'alerte reste liée à la fenêtre principale
        alert.initOwner(primaryStage);
        
        try {
            // Personnaliser le style de l'alerte
            DialogPane dialogPane = alert.getDialogPane();
            if (getClass().getResource("/style.css") != null) {
                dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            }
            dialogPane.setStyle("-fx-background-color: " + SECONDARY_COLOR + ";");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'application du style : " + e.getMessage());
            // Continuer sans style personnalisé
        }
        
        // Ajouter un bouton OK personnalisé
        ButtonType okButton = new ButtonType("OK");
        alert.getButtonTypes().setAll(okButton);
        
        // Afficher l'alerte
        alert.showAndWait();
    }
    
    /**
     * Compte le nombre de segments actifs autour d'une cellule.
     * 
     * @param row Ligne de la cellule
     * @param col Colonne de la cellule
     * @return Le nombre de segments actifs
     */
    private int countAdjacentActiveLines(int row, int col) {
        int count = 0;
        
        // Vérifier la ligne du haut
        String topLineKey = "H_" + row + "_" + col;
        Line topLine = gridLines.get(topLineKey);
        if (topLine != null && topLine.getStroke() != Color.TRANSPARENT) {
            count++;
        }
        
        // Vérifier la ligne du bas
        String bottomLineKey = "H_" + (row + 1) + "_" + col;
        Line bottomLine = gridLines.get(bottomLineKey);
        if (bottomLine != null && bottomLine.getStroke() != Color.TRANSPARENT) {
            count++;
        }
        
        // Vérifier la ligne de gauche
        String leftLineKey = "V_" + row + "_" + col;
        Line leftLine = gridLines.get(leftLineKey);
        if (leftLine != null && leftLine.getStroke() != Color.TRANSPARENT) {
            count++;
        }
        
        // Vérifier la ligne de droite
        String rightLineKey = "V_" + row + "_" + (col + 1);
        Line rightLine = gridLines.get(rightLineKey);
        if (rightLine != null && rightLine.getStroke() != Color.TRANSPARENT) {
            count++;
        }
        
        return count;
    }
    
    /**
     * Compte le nombre de croix autour d'une cellule.
     * 
     * @param row Ligne de la cellule
     * @param col Colonne de la cellule
     * @return Le nombre de croix
     */
    private int countAdjacentCrosses(int row, int col) {
        int count = 0;
        
        // Vérifier la ligne du haut
        String topLineKey = "H_" + row + "_" + col;
        Line topLine = gridLines.get(topLineKey);
        if (topLine != null && hasCross(topLine)) {
            count++;
        }
        
        // Vérifier la ligne du bas
        String bottomLineKey = "H_" + (row + 1) + "_" + col;
        Line bottomLine = gridLines.get(bottomLineKey);
        if (bottomLine != null && hasCross(bottomLine)) {
            count++;
        }
        
        // Vérifier la ligne de gauche
        String leftLineKey = "V_" + row + "_" + col;
        Line leftLine = gridLines.get(leftLineKey);
        if (leftLine != null && hasCross(leftLine)) {
            count++;
        }
        
        // Vérifier la ligne de droite
        String rightLineKey = "V_" + row + "_" + (col + 1);
        Line rightLine = gridLines.get(rightLineKey);
        if (rightLine != null && hasCross(rightLine)) {
            count++;
        }
        
        return count;
    }
    
    /**
     * Vérifie si une ligne a une croix.
     * 
     * @param line La ligne à vérifier
     * @return true si la ligne a une croix, false sinon
     */
    private boolean hasCross(Line line) {
        return slitherlinkGrid.getChildren().stream()
            .anyMatch(node -> node instanceof Line && node.getUserData() == line);
    }
}