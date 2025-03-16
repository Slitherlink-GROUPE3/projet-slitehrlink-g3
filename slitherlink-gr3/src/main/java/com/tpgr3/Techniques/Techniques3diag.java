package com.tpgr3.Techniques;

import com.tpgr3.Grille;
import com.tpgr3.Case;
import com.tpgr3.Cellule;
import com.tpgr3.Voisins;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Technique n°4 : détection des 3 en diagonale.
 * Cette technique identifie les paires de cases contenant des 3 qui sont en diagonale l'une par rapport à l'autre.
 * Quand deux cases contenant un 3 sont en diagonale, elles doivent être reliées par une ligne fermée.
 */
public class Techniques3diag implements Techniques {
    
    /**
     * Liste des paires de cases 3 en diagonale trouvées lors de l'analyse.
     * Chaque élément est un tableau de 2 paires de coordonnées: [[x1,y1], [x2,y2]]
     */
    private List<int[][]> paires3Diagonales;
    
    /**
     * Ensemble des paires déjà identifiées pour éviter les doublons.
     * Format des clés: "x1,y1-x2,y2" où les coordonnées sont normalisées.
     */
    private Set<String> pairesVues;
    
    /**
     * Constructeur.
     * Initialise les collections pour le stockage des paires.
     */
    public Techniques3diag() {
        this.paires3Diagonales = new ArrayList<>();
        this.pairesVues = new HashSet<>();
    }
    
    /**
     * Détermine si la technique est applicable sur la grille donnée.
     * Recherche toutes les paires de cases contenant des 3 qui sont en diagonale.
     * 
     * @param grille La grille de jeu à analyser
     * @return true si au moins une paire de cases 3 en diagonale a été trouvée, false sinon
     */
    @Override
    public boolean estApplicable(Grille grille) {
        // Réinitialisation des collections pour une nouvelle analyse
        this.paires3Diagonales.clear();
        this.pairesVues.clear();
        
        // Affichage de la grille pour référence
        //grille.afficher();
        
        // Recherche des paires de cases 3 en diagonale
        rechercherPairesDe3enDiagonale(grille);
        
        // Test visuel des voisins (pour information/debug)
        //test(grille, 1, 1);
        
        // Affichage des résultats et retour de l'applicabilité
        return afficherResultats();
    }
    
    /**
     * Recherche toutes les paires de cases contenant des 3 en diagonale.
     * 
     * @param grille La grille à analyser
     */
    private void rechercherPairesDe3enDiagonale(Grille grille) {
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
                    verifierVoisinagenDiagonal(x, y, voisins);
                }
            }
        }
    }
    
    /**
     * Vérifie les quatre voisins diagonaux d'une case contenant un 3.
     * 
     * @param x Coordonnée x de la case centrale
     * @param y Coordonnée y de la case centrale
     * @param voisins Objet Voisins contenant les informations sur les voisins
     */
    private void verifierVoisinagenDiagonal(int x, int y, Voisins voisins) {
        System.out.println("Case (" + x + "," + y + ") contient un 3");
        
        // Vérification du voisin haut-gauche
        int voisinHG = voisins.getVoisinHautGauche();
        System.out.println("  Voisin haut-gauche: " + voisinHG);
        if(voisinHG == 3) {
            ajouterPaireSansDoublon(x, y, x-1, y-1);
        }
        
        // Vérification du voisin haut-droite
        int voisinHD = voisins.getVoisinHautDroite();
        System.out.println("  Voisin haut-droite: " + voisinHD);
        if(voisinHD == 3) {
            ajouterPaireSansDoublon(x, y, x+1, y-1);
        }
        
        // Vérification du voisin bas-gauche
        int voisinBG = voisins.getVoisinBasGauche();
        System.out.println("  Voisin bas-gauche: " + voisinBG);
        if(voisinBG == 3) {
            ajouterPaireSansDoublon(x, y, x-1, y+1);
        }
        
        // Vérification du voisin bas-droite
        int voisinBD = voisins.getVoisinBasDroite();
        System.out.println("  Voisin bas-droite: " + voisinBD);
        if(voisinBD == 3) {
            ajouterPaireSansDoublon(x, y, x+1, y+1);
        }
    }
    
    /**
     * Ajoute une paire de cases à la liste sans créer de doublon.
     * 
     * @param x1 Coordonnée x de la première case
     * @param y1 Coordonnée y de la première case
     * @param x2 Coordonnée x de la deuxième case
     * @param y2 Coordonnée y de la deuxième case
     */
    private void ajouterPaireSansDoublon(int x1, int y1, int x2, int y2) {
        // Normalisation de la paire (toujours mettre la case avec les coordonnées les plus petites en premier)
        String cle = normaliserCle(x1, y1, x2, y2);
        
        // Vérifier si cette paire existe déjà
        if (!pairesVues.contains(cle)) {
            paires3Diagonales.add(new int[][] {{x1, y1}, {x2, y2}});
            pairesVues.add(cle);
            System.out.println("  → Nouvelle paire ajoutée: " + cle);
        } else {
            System.out.println("  → Paire ignorée (doublon): " + cle);
        }
    }
    
    /**
     * Crée une clé normalisée pour une paire de coordonnées.
     * Garantit que des paires identiques (indépendamment de l'ordre) auront la même clé.
     * 
     * @param x1 Coordonnée x de la première case
     * @param y1 Coordonnée y de la première case
     * @param x2 Coordonnée x de la deuxième case
     * @param y2 Coordonnée y de la deuxième case
     * @return Une chaîne de caractères au format "x1,y1-x2,y2" normalisée
     */
    private String normaliserCle(int x1, int y1, int x2, int y2) {
        if (x1 < x2 || (x1 == x2 && y1 < y2)) {
            return x1 + "," + y1 + "-" + x2 + "," + y2;
        } else {
            return x2 + "," + y2 + "-" + x1 + "," + y1;
        }
    }
    
    /**
     * Affiche les résultats de l'analyse et détermine si la technique est applicable.
     * 
     * @return true si au moins une paire a été trouvée, false sinon
     */
    private boolean afficherResultats() {
        if(!paires3Diagonales.isEmpty()) {
            System.out.println("Il y a " + paires3Diagonales.size() + " paires de cases 3 en diagonales");
            
            // Affiche toutes les paires trouvées
            for(int i = 0; i < paires3Diagonales.size(); i++) {
                int[][] paire = paires3Diagonales.get(i);
                System.out.println("Paire " + (i+1) + ": Case 3 en (" + 
                                  paire[0][0] + ", " + paire[0][1] + ") et (" + 
                                  paire[1][0] + ", " + paire[1][1] + ")");
            }
            
            return true; // La technique est applicable
        } else {
            System.out.println("Il n'y a pas de cases 3 en diagonales");
            return false; // La technique n'est pas applicable
        }
    }

    /**
     * Renvoie la liste des paires de cases 3 en diagonale trouvées.
     * 
     * @return Liste de tableaux de coordonnées [[x1,y1], [x2,y2]]
     */
    public List<int[][]> getPaires3Diagonales() {
        return paires3Diagonales;
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
}