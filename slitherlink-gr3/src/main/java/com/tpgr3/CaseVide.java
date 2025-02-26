package com.tpgr3;

public class CaseVide extends Cellule {
    public CaseVide(int x, int y) {
        super(x, y);
    }

    @Override
    public void actionner() {
        System.out.println("Case vide, on ne peut pas actionner");
    }

    @Override
    public char afficher() {
        return ' ';
    }
    
}
