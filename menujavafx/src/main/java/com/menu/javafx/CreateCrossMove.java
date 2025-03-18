package com.menu.javafx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CreateCrossMove extends CrossMove{
    public CreateCrossMove(Line line, Object action, Color color, SlitherGrid slitherGrid) {
        super(line, action, color, slitherGrid);
        this.createCross(line, !slitherGrid.isHypothesisInactive());
    }

    @Override
    public void undoMove(){
        this.getSlitherGrid().getSlitherlinkGrid().getChildren()
                .removeIf(node -> node instanceof Line && node.getUserData() == this.line());
    }


    @Override
    public void redoMove() {
        createCross(this.line(), false);
    }

}
