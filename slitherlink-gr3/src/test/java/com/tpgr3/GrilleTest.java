package com.tpgr3;

import junit.framework.TestCase;

public class GrilleTest extends TestCase {

    public void testInitialisationGrille() {
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 3}
        };
        Grille grille = new Grille(valeurs);
        assertEquals(7, grille.getHauteur());
        assertEquals(7, grille.getLargeur());    
    }

    public void testEstValide() {
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 3}
        };
        Grille grille = new Grille(valeurs);
        assertTrue(grille.estValide(0, 0));
        assertTrue(grille.estValide(6, 6));
        assertFalse(grille.estValide(7, 7));
        assertFalse(grille.estValide(-1, -1));
    }

    public void testActionnerCelule() {
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 3}
        };
        Grille grille = new Grille(valeurs);
        grille.actionnerCelule(0, 1);
        assertEquals('-', grille.getCellule(0, 1).afficher());
    }

    public void testGetCellule() {
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 3}
        };
        Grille grille = new Grille(valeurs);
        assertNotNull(grille.getCellule(0, 0));
        assertNull(grille.getCellule(7, 7));
    }
}