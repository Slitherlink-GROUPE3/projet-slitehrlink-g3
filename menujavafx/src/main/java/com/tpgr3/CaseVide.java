package com.tpgr3;

public class CaseVide extends Cellule {
    public CaseVide(int x, int y) {
        super(x, y);
    }

    @Override
    public void actionner() {
        super.actionner();
        System.out.println("Case vide, on ne peut pas actionner");
    }

    @Override
    public char afficher() {
        return ' ';
    }

    @Override
    public int getValeur() {
        return CASE_VIDE;
    }
   
}
