package com.tpgr3;

import junit.framework.TestCase;

public class CaseTest extends TestCase {

    public void testAfficher() {
        Case case1 = new Case(0, 0, 3);
        assertEquals('3', case1.afficher());
    }

    public void testActionner() {
        Case case1 = new Case(0, 0, 3);
        case1.actionner();
        // Ajoutez des assertions si nécessaire
    }
}