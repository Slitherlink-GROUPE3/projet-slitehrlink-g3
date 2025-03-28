package com.menu.javafx;

import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Classe utilitaire pour réutiliser le code de création de croix sans instancier CrossMove
 */
public class CrossMoveHelper {

    /**
     * Crée une croix pour l'application d'un état de sauvegarde
     * @param line La ligne sur laquelle créer la croix
     * @param isHypothesis Si la croix est en mode hypothèse
     * @param slitherGrid La grille sur laquelle ajouter la croix
     */
    public static void createCrossForSave(Line line, boolean isHypothesis, SlitherGrid slitherGrid) {
        // Création des croix selon l'orientation de la ligne
        Line cross1, cross2;
        double padding = 20; // Ajouter un espacement pour la zone cliquable autour de la croix

        if (line.getStartX() == line.getEndX()) { // Ligne verticale
            cross1 = new Line(
                    line.getStartX() - 10, line.getStartY() + 20,
                    line.getEndX() + 10, line.getEndY() - 20);
            cross2 = new Line(
                    line.getStartX() - 10, line.getEndY() - 20,
                    line.getEndX() + 10, line.getStartY() + 20);
        } else { // Ligne horizontale
            cross1 = new Line(
                    line.getStartX() + 20, line.getStartY() - 10,
                    line.getEndX() - 20, line.getEndY() + 10);
            cross2 = new Line(
                    line.getStartX() + 20, line.getEndY() + 10,
                    line.getEndX() - 20, line.getStartY() - 10);
        }
        cross1.setStrokeWidth(3);
        cross1.setUserData(line);

        cross2.setStrokeWidth(3);
        cross2.setUserData(line);

        // Ajouter un effet visuel aux croix
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.3));
        shadow.setRadius(2);
        shadow.setOffsetY(1);
        cross1.setEffect(shadow);
        cross2.setEffect(shadow);

        // Pour les sauvegardes, nous n'ajoutons pas de hitbox car ce n'est pas nécessaire
        // Les croix chargées à partir d'une sauvegarde sont déjà dans l'état final

        // Appliquer la couleur appropriée
        if (isHypothesis) {
            cross1.setStroke(Color.web(SlitherGrid.LIGHT_COLOR));
            cross2.setStroke(Color.web(SlitherGrid.LIGHT_COLOR));
        } else {
            cross1.setStroke(Color.web(SlitherGrid.ACCENT_COLOR));
            cross2.setStroke(Color.web(SlitherGrid.ACCENT_COLOR));
        }

        // Ajouter à la grille
        slitherGrid.getSlitherlinkGrid().getChildren().addAll(cross1, cross2);
    }
}