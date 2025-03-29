package com.tpgr3.backend.Marque;

public class Croix extends Marque {
    @Override
    public char afficher() {
        return 'X';
    }

    @Override
    public int getValeur() {
        return CROIX;
    }
}