package com.menu.javafx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class LineMove extends Move{

    private SlitherGrid slitherGrid;
    private SlitherGridChecker slitherGridChecker;

    public LineMove(Line line, Object action, Color color, SlitherGrid slitherGrid) {
        super(line, action, color);
        this.slitherGrid = slitherGrid;
        this.slitherGridChecker = new SlitherGridChecker(slitherGrid);
    }

    @Override
    public void undoMove(){
        if (this.line() != null) {
            this.line().setStroke(Color.TRANSPARENT);
        }
    }

    @Override
    public void redoMove() {
        if (this.line() != null) {
            this.line().setStroke(Color.web(SlitherGrid.DARK_COLOR));

            if (!slitherGrid.isHypothesisInactive()) {
                slitherGridChecker.checkGridAutomatically();
            }
        }
    }
}
