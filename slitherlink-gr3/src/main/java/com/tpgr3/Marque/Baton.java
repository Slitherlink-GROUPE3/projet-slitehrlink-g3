package com.tpgr3.Marque;

public class Baton extends Marque {
    @Override
    public char afficher() {
        return '-';
    }

    @Override
    public int getValeur() {
        return BATON;
    }
}