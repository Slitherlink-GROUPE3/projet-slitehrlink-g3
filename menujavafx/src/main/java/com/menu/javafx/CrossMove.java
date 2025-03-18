package com.menu.javafx;

import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public abstract class CrossMove extends Move {

    private SlitherGrid slitherGrid;

    public CrossMove(Line line, Object action, Color color, SlitherGrid slitherlinkGrid) {
        super(line, action, color);
        this.slitherGrid = slitherlinkGrid;
    }

    protected void createCross(Line line, boolean isHypothesis) {
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

        // Ajouter une grande hitbox autour de la croix pour la rendre plus facile à
        // cliquer
        Rectangle hitbox = new Rectangle(
                Math.min(cross1.getStartX(), cross2.getStartX()) - padding,
                Math.min(cross1.getStartY(), cross2.getStartY()) - padding,
                Math.abs(cross1.getEndX() - cross1.getStartX()) + 2 * padding,
                Math.abs(cross1.getEndY() - cross1.getStartY()) + 2 * padding);
        hitbox.setFill(Color.TRANSPARENT);
        hitbox.setUserData(line); // Lier la hitbox à la ligne de la croix
        hitbox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                toggleCross(line); // Si on clique sur la hitbox, on supprime la croix

                // Si ce n'est pas en mode hypothèse, enregistre le mouvement dans l'historique
                if (!slitherGrid.isHypothesisInactive()) {
                    // Supprime les mouvements futurs si on était revenu en arrière
                    slitherGrid.addMove(this);
                }
            }
        });

        if (!slitherGrid.isHypothesisInactive()) {
            cross1.setStroke(Color.web(SlitherGrid.LIGHT_COLOR));
            cross2.setStroke(Color.web(SlitherGrid.LIGHT_COLOR));
            cross1.setUserData("hypothesis");
            cross2.setUserData("hypothesis");
        } else {
            cross1.setStroke(Color.web(SlitherGrid.ACCENT_COLOR));
            cross2.setStroke(Color.web(SlitherGrid.ACCENT_COLOR));
        }


        slitherGrid.getSlitherlinkGrid().getChildren().addAll(cross1, cross2, hitbox);

    }

    protected void toggleCross(Line line) {
        boolean hasCross = slitherGrid.getSlitherlinkGrid().getChildren().stream()
                .anyMatch(node -> node instanceof Line && node.getUserData() == line);

        if (hasCross) {
            // Supprimer la croix si elle existe
            slitherGrid.getSlitherlinkGrid().getChildren().removeIf(node -> node instanceof Line && node.getUserData() == line);
            slitherGrid.getSlitherlinkGrid().getChildren().removeIf(node -> node instanceof Rectangle && node.getUserData() == line);
        } else {
            createCross(line, !slitherGrid.isHypothesisInactive());
        }
    }

    protected SlitherGrid getSlitherGrid() {
        return slitherGrid;
    }

}
