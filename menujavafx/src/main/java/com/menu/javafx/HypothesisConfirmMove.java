package com.menu.javafx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class HypothesisConfirmMove extends Move{

    private SlitherGrid slitherGrid;
    private SlitherGridChecker slitherGridChecker;

    protected HypothesisConfirmMove(Line line, Object action, Color color, SlitherGrid slitherGrid) {
        super(line, action, color);
        this.slitherGrid = slitherGrid;
        this.slitherGridChecker = new SlitherGridChecker(slitherGrid);
    }

    @Override
    public void redoMove() {
        System.out.println(" Redo Hypothesis ");
        this.slitherGridChecker.checkGridAutomatically();
    }

    @Override
    public void undoMove() {
        System.out.println(" Undo Hypothesis ");

        slitherGrid.getSlitherlinkGrid().getChildren()
                .removeIf(node -> node instanceof Line && "hypothesis".equals(((Line) node).getUserData()));

    }

}
