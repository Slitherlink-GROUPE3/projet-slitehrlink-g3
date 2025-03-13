package com.tpgr3;

public class Croix extends Cellule {
    public Croix(int x, int y) {
        super(x, y);
    }

    @Override
    public void actionner() {
        System.out.println("Croix actionnée en (" + x + ", " + y + ")");
    }

    @Override
    public char afficher() {
        return 'X';
    }

    @Override
    public int getValeur() {
        return CROIX;
    }
}