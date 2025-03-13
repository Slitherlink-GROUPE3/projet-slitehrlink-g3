package com.tpgr3.Marque;

public class Neutre extends Marque {
    @Override
    public char afficher() {
        return ' ';
    }

    @Override
    public int getValeur() {
        return NEUTRE;
    }
}