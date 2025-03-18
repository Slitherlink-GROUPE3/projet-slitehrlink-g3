package com.menu.javafx;

import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class RemoveCrossMove extends CrossMove{

    public RemoveCrossMove(Line line, Object action, Color color, SlitherGrid slitherGrid) {
        super(line, action, color, slitherGrid);
        this.toggleCross(line);
    }

    @Override
    public void undoMove() {
        this.createCross(this.line(), false);
    }

    @Override
    public void redoMove() {
        this.getSlitherGrid().getSlitherlinkGrid().getChildren()
                .removeIf(node -> node instanceof Line && node.getUserData() == this.line());
    }
}
