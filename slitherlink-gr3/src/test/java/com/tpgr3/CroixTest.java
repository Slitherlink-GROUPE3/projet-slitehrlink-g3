package com.tpgr3;

import junit.framework.TestCase;

public class CroixTest extends TestCase {

    public void testAfficher() {
        Croix croix = new Croix(0, 0);
        assertEquals('X', croix.afficher());
    }

    public void testActionner() {
        Croix croix = new Croix(0, 0);
        croix.actionner();
        // Ajoutez des assertions si nécessaire
    }
}