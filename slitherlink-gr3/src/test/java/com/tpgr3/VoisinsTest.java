package com.tpgr3;

import junit.framework.TestCase;

/**
 * Tests unitaires pour la classe Voisins
 */
public class VoisinsTest extends TestCase implements Constantes {
    
    private Grille grille;
    
    @Override
    protected void setUp() {
        // Configuration d'une grille de test 3x3
        int[][] valeurs = {
            {3, 2, 3},
            {1, 3, 2},
            {2, 1, 0}
        };
        grille = new Grille(valeurs);
    }
    
    public void testConstructeur() {
        // Vérifie que l'objet Voisins est bien créé sans erreur
        Voisins voisins = new Voisins(grille, 1, 1);
        assertNotNull(voisins);
    }
    
    public void testInitialisation() {
        // Vérifie que les attributs sont correctement initialisés
        Voisins voisins = new Voisins(grille, 1, 1);
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinHaut());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinHautGauche());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinHautDroite());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinBas());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinBasGauche());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinBasDroite());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinGauche());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinDroite());
    }
    
    public void testVoisinsCentre() {
        // Test pour une cellule au centre (position 1,1)
        Voisins voisins = new Voisins(grille, 1, 1);
        voisins.TrouverVoisins();
        
        // Vérifie que tous les voisins sont détectés (les valeurs ne sont plus non initialisées)
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinHaut());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinHautGauche());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinHautDroite());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinBas());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinBasGauche());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinBasDroite());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinGauche());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinDroite());
    }
    
    public void testVoisinsHautGauche() {
        // Test pour une cellule en haut à gauche (position 0,0)
        Voisins voisins = new Voisins(grille, 0, 0);
        voisins.TrouverVoisins();
        
        // Vérifie les voisins hors limites
        assertEquals(HORS_LIMITES, voisins.getVoisinHaut());
        assertEquals(HORS_LIMITES, voisins.getVoisinHautGauche());
        assertEquals(HORS_LIMITES, voisins.getVoisinGauche());
        
        // Vérifie les voisins qui devraient exister
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinDroite());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinBas());
    }
    
    public void testVoisinsBasDroite() {
        // Test pour une cellule en bas à droite (position 2,2)
        Voisins voisins = new Voisins(grille, 2, 2);
        voisins.TrouverVoisins();
        
        // Vérifie les voisins hors limites
        assertEquals(HORS_LIMITES, voisins.getVoisinBasDroite());
        assertEquals(HORS_LIMITES, voisins.getVoisinDroite());
        assertEquals(HORS_LIMITES, voisins.getVoisinBas());
        
        // Vérifie les voisins qui devraient exister
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinHaut());
        assertTrue(VALEUR_NON_INITIALISEE != voisins.getVoisinGauche());
    }
    
    public void testCoordonnéesInvalides() {
        // Test avec des coordonnées en dehors de la grille
        Voisins voisins = new Voisins(grille, 10, 10);
        voisins.TrouverVoisins();
        
        // Tous les voisins devraient rester à VALEUR_NON_INITIALISEE
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinHaut());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinHautGauche());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinHautDroite());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinBas());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinBasGauche());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinBasDroite());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinGauche());
        assertEquals(VALEUR_NON_INITIALISEE, voisins.getVoisinDroite());
    }
    
    public void testCoherenceVoisinsDiagonaux() {
        // Vérifie que les voisins diagonaux sont cohérents avec les voisins directs
        Voisins voisins = new Voisins(grille, 1, 1);
        voisins.TrouverVoisins();
        
        // Si le voisin du haut et le voisin de gauche sont HORS_LIMITES,
        // alors le voisin en haut à gauche devrait aussi être HORS_LIMITES
        if (voisins.getVoisinHaut() == HORS_LIMITES && voisins.getVoisinGauche() == HORS_LIMITES) {
            assertEquals(HORS_LIMITES, voisins.getVoisinHautGauche());
        }
        
        // Même chose pour les autres diagonales
        if (voisins.getVoisinHaut() == HORS_LIMITES && voisins.getVoisinDroite() == HORS_LIMITES) {
            assertEquals(HORS_LIMITES, voisins.getVoisinHautDroite());
        }
        
        if (voisins.getVoisinBas() == HORS_LIMITES && voisins.getVoisinGauche() == HORS_LIMITES) {
            assertEquals(HORS_LIMITES, voisins.getVoisinBasGauche());
        }
        
        if (voisins.getVoisinBas() == HORS_LIMITES && voisins.getVoisinDroite() == HORS_LIMITES) {
            assertEquals(HORS_LIMITES, voisins.getVoisinBasDroite());
        }
    }
    
    public void testAffichage() {
        // Ce test vérifie simplement que la méthode testDetection() s'exécute sans erreur
        Voisins voisins = new Voisins(grille, 1, 1);
        voisins.TrouverVoisins();
        voisins.testDetection();
        // Si aucune exception n'est levée, le test est considéré comme réussi
        assertTrue(true);
    }
}