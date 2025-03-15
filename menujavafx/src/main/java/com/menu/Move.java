package com.menu;

import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

public class Move {
    private final Line line;
    private final Object action;
    private final Color color;

    public Move(Line line, Object action, Color color) {
        this.line = line;
        this.action = action;
        this.color = color;
    }

    public Line getLine() {
        return line;
    }

    public Object getAction() {
        return action;
    }

    public Color getColor(){ return this.color; }
}