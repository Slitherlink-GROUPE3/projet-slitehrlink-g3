package com.tpgr3;

import junit.framework.TestCase;

public class CaseVideTest extends TestCase {

    public void testAfficher() {
        CaseVide caseVide = new CaseVide(0, 0);
        assertEquals(' ', caseVide.afficher());
    }

    public void testActionner() {
        CaseVide caseVide = new CaseVide(0, 0);
        caseVide.actionner();
        // Ajoutez des assertions si nécessaire
    }
}