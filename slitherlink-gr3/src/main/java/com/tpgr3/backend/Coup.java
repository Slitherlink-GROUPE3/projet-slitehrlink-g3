package com.tpgr3.backend;

import com.tpgr3.backend.Marque.Marque;

public class Coup {
    private final int x;
    private final int y;
    private final Marque ancienne;
    private final Marque nouvelle;

    public Coup(int x, int y, Marque ancienne, Marque nouvelle) {
        this.x = x;
        this.y = y;
        this.ancienne = ancienne;
        this.nouvelle = nouvelle;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Marque getAncienne() { return ancienne; }
    public Marque getNouvelle() { return nouvelle; }
}
