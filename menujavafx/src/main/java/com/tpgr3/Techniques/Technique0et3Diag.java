<<<<<<< HEAD
package com.tpgr3.Techniques;

=======
// Common imports for Technique classes
package com.tpgr3.Techniques;

import java.util.Map;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

>>>>>>> 12cd25160dfc23eed9267d004e81170d606aac35
import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Cellule;
import com.tpgr3.Voisins;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Technique n°6 : détection des 0 et 3 en diagonale.
 * Cette technique identifie les paires de cases contenant un 0 et un 3 qui sont en diagonale l'une par rapport à l'autre.
 */
public class Technique0et3Diag implements Techniques {
    
    /**
     * Liste des paires de cases 0-3 en diagonale trouvées lors de l'analyse.
     * Chaque élément est un tableau de 2 paires de coordonnées: [[x3,y3], [x0,y0]]
     */
    private List<int[][]> paires0et3Diag;
    
    /**
     * Ensemble des paires déjà identifiées pour éviter les doublons.
     * Format des clés: "x3,y3-x0,y0" où les coordonnées sont normalisées.
     */
    private Set<String> pairesVues;
    
    /**
     * Constructeur.
     * Initialise les collections pour le stockage des paires.
     */
    public Technique0et3Diag() {
        this.paires0et3Diag = new ArrayList<>();
        this.pairesVues = new HashSet<>();
    }
    
    /**
     * Détermine si la technique est applicable sur la grille donnée.
     * Recherche toutes les paires de cases contenant un 0 et un 3 en diagonale.
     * 
     * @param grille La grille de jeu à analyser
     * @return true si au moins une paire de cases 0-3 en diagonale a été trouvée, false sinon
     */
    @Override
    public boolean estApplicable(Grille grille) {
        // Réinitialisation des collections pour une nouvelle analyse
        this.paires0et3Diag.clear();
        this.pairesVues.clear();
        
        // Recherche des paires de cases 0-3 en diagonale
        rechercherPairesDe0et3enDiagonale(grille);
        
        // Affichage des résultats et retour de l'applicabilité
        return afficherResultats();
    }
    
    /**
     * Recherche toutes les paires de cases contenant un 0 et 3 en diagonale.
     * 
     * @param grille La grille à analyser
     */
    private void rechercherPairesDe0et3enDiagonale(Grille grille) {
        int[] dimensions = grille.getDimensionsLogiques();
        int largeurRelle = dimensions[0];
        int hauteurRelle = dimensions[1];
        
        System.out.println("Dimensions logiques de la grille : " + largeurRelle + "x" + hauteurRelle);
        
        // Boucle principale vérifiant si des cases sont en diagonales
        for(int y = 1; y <= hauteurRelle; y++) {
            for(int x = 1; x <= largeurRelle; x++) {
                // On vérifie si la case est un 3
                Voisins voisins = new Voisins(grille, x, y);
                voisins.TrouverVoisins();
                if(voisins.c != null && voisins.c.getValeur() == 3) {
                    // Passer la grille comme paramètre supplémentaire
                    verifierVoisinagenDiagonal(x, y, voisins, grille);
                }
            }
        }
    }
    
    /**
     * Vérifie strictement les voisins diagonaux d'une case contenant un 3 pour chercher des 0.
     * 
     * @param x Coordonnée x de la case centrale
     * @param y Coordonnée y de la case centrale
     * @param voisins Objet Voisins contenant les informations sur les voisins
     * @param grille La grille à analyser, passée en paramètre
     */
    private void verifierVoisinagenDiagonal(int x, int y, Voisins voisins, Grille grille) {
        System.out.println("Case (" + x + "," + y + ") contient un 3");
        
        // Vérification stricte du voisin haut-gauche (diagonale)
        int voisinHG = voisins.getVoisinHautGauche();
        System.out.println("  Voisin haut-gauche: " + voisinHG);
        // Vérification que ce n'est pas en fait la case au-dessus ou à gauche
        if(voisinHG == 0 && estVraimentEnDiagonale(x, y, x-1, y-1, grille)) {
            ajouterPaireSansDoublon(x, y, x-1, y-1);
        }
        
        // Vérification stricte du voisin haut-droite (diagonale)
        int voisinHD = voisins.getVoisinHautDroite();
        System.out.println("  Voisin haut-droite: " + voisinHD);
        if(voisinHD == 0 && estVraimentEnDiagonale(x, y, x+1, y-1, grille)) {
            ajouterPaireSansDoublon(x, y, x+1, y-1);
        }
        
        // Vérification stricte du voisin bas-gauche (diagonale)
        int voisinBG = voisins.getVoisinBasGauche();
        System.out.println("  Voisin bas-gauche: " + voisinBG);
        if(voisinBG == 0 && estVraimentEnDiagonale(x, y, x-1, y+1, grille)) {
            ajouterPaireSansDoublon(x, y, x-1, y+1);
        }
        
        // Vérification stricte du voisin bas-droite (diagonale)
        int voisinBD = voisins.getVoisinBasDroite();
        System.out.println("  Voisin bas-droite: " + voisinBD);
        if(voisinBD == 0 && estVraimentEnDiagonale(x, y, x+1, y+1, grille)) {
            ajouterPaireSansDoublon(x, y, x+1, y+1);
        }
    }
    
    /**
     * Vérifie que deux cases sont bien en diagonale l'une par rapport à l'autre.
     * Cette vérification permet d'éviter les faux positifs dus à la structure de la grille.
     * 
     * @param x1 Coordonnée x de la première case
     * @param y1 Coordonnée y de la première case
     * @param x2 Coordonnée x de la deuxième case
     * @param y2 Coordonnée y de la deuxième case
     * @param grille La grille de jeu
     * @return true si les cases sont strictement en diagonale, false sinon
     */
    private boolean estVraimentEnDiagonale(int x1, int y1, int x2, int y2, Grille grille) {
        // Vérification mathématique : les cases sont en diagonale si leurs coordonnées
        // diffèrent exactement de 1 dans les deux dimensions
        boolean diagMath = Math.abs(x1 - x2) == 1 && Math.abs(y1 - y2) == 1;
        
        // Vérification supplémentaire : s'assurer qu'aucune case n'existe entre les deux
        boolean pasDeVoisinCommun = true;
        
        // En utilisant les coordonnées matricielles, on pourrait vérifier 
        // qu'aucune case n'existe sur le chemin direct entre les deux cases.
        // Pour notre cas, simplement vérifier la différence de 1 est suffisant.
        
        return diagMath && pasDeVoisinCommun;
    }
    
    /**
     * Ajoute une paire de cases à la liste sans créer de doublon.
     * 
     * @param x3 Coordonnée x de la case avec 3
     * @param y3 Coordonnée y de la case avec 3
     * @param x0 Coordonnée x de la case avec 0
     * @param y0 Coordonnée y de la case avec 0
     */
    private void ajouterPaireSansDoublon(int x3, int y3, int x0, int y0) {
        // La clé est plus simple ici car on distingue le 3 et le 0
        String cle = x3 + "," + y3 + "-" + x0 + "," + y0;
        
        // Vérifier si cette paire existe déjà
        if (!pairesVues.contains(cle)) {
            paires0et3Diag.add(new int[][] {{x3, y3}, {x0, y0}});
            pairesVues.add(cle);
            System.out.println("  → Nouvelle paire ajoutée: " + cle);
        } else {
            System.out.println("  → Paire ignorée (doublon): " + cle);
        }
    }
    
    /**
     * Affiche les résultats de l'analyse et détermine si la technique est applicable.
     * 
     * @return true si au moins une paire a été trouvée, false sinon
     */
    private boolean afficherResultats() {
        if(!paires0et3Diag.isEmpty()) {
            System.out.println("Il y a " + paires0et3Diag.size() + " paires de cases 0-3 en diagonale");
            
            // Affiche toutes les paires trouvées
            for(int i = 0; i < paires0et3Diag.size(); i++) {
                int[][] paire = paires0et3Diag.get(i);
                System.out.println("Paire " + (i+1) + ": Case 3 en (" + 
                                  paire[0][0] + ", " + paire[0][1] + ") et Case 0 en (" + 
                                  paire[1][0] + ", " + paire[1][1] + ")");
            }
            
            return true; // La technique est applicable
        } else {
            System.out.println("Il n'y a pas de cases 0-3 en diagonale");
            return false; // La technique n'est pas applicable
        }
    }

    /**
     * Renvoie la liste des paires de cases 0-3 en diagonale trouvées.
     * 
     * @return Liste de tableaux de coordonnées [[x3,y3], [x0,y0]]
     */
    public List<int[][]> getPaires0et3Diag() {
        return paires0et3Diag;
    }
    
    /**
     * Méthode de test pour visualiser les voisins d'une case spécifique.
     * 
     * @param grille La grille de jeu
     * @param x Coordonnée x de la case à tester
     * @param y Coordonnée y de la case à tester
     */
    public void test(Grille grille, int x, int y) {
        System.out.println("\n=== TEST DES VOISINS DE LA CASE (" + x + "," + y + ") ===");
        Voisins v = new Voisins(grille, x, y);
        v.testDetection();
        System.out.println("===============================================\n");
    }

    //ajout de la méthode appliquer pour eviter les erreur de compilation
    @Override
    public void appliquer(Grille grille) {
        // TODO Auto-generated method stub
    }
}