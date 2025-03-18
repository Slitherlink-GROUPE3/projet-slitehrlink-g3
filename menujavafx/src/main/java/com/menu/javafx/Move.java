package com.menu.javafx;

import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

public abstract class Move {

    private Line line;
    private Object action;
    private Color color;

    protected Move(Line line, Object action, Color color){
        this.line = line;
        this.action = action;
        this.color = color;
    }

    public abstract void undoMove();
    public abstract void redoMove();

    public Line line() {
        return line;
    }
    public Object action() { return action; }
    public Color color() {
         return color;
    }

    public void setLine( Line line ){ this.line = line; }
    public void setAction( Object action ){ this.action = action; }
    public void setColor( Color color ) { this.color = color; }
}