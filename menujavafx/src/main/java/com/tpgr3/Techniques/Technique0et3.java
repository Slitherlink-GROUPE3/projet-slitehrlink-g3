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
 * Technique n°5 : détection des 0 et 3 adjacents verticalement.
 * Cette technique identifie les paires de cases contenant un 0 et un 3 qui sont l'un au-dessus de l'autre.
 * Quand un 0 et un 3 sont alignés verticalement, des règles spécifiques s'appliquent pour les segments.
 */
public class Technique0et3 implements Techniques {
    
    /**
     * Liste des paires de cases 0-3 adjacentes verticalement trouvées lors de l'analyse.
     * Chaque élément est un tableau de 2 paires de coordonnées: [[x1,y1], [x2,y2]]
     * où la première paire est la case avec 3 et la seconde est la case avec 0.
     */
    private List<int[][]> paires0et3;
    
    /**
     * Ensemble des paires déjà identifiées pour éviter les doublons.
     * Format des clés: "x1,y1-x2,y2" où les coordonnées sont normalisées.
     */
    private Set<String> pairesVues;
    
    /**
     * Contrôle l'affichage des messages de debug.
     * 0 = silencieux (par défaut), 1 = affichage des messages
     */
    private int verbose;
    
    /**
     * Constructeur.
     * Initialise les collections pour le stockage des paires.
     * Mode verbeux désactivé par défaut.
     */
    public Technique0et3() {
        this.paires0et3 = new ArrayList<>();
        this.pairesVues = new HashSet<>();
        this.verbose = 0;
    }
    
    /**
     * Constructeur avec option de verbosité.
     *
     * @param verbose 0 pour mode silencieux, 1 pour mode verbeux
     */
    public Technique0et3(int verbose) {
        this.paires0et3 = new ArrayList<>();
        this.pairesVues = new HashSet<>();
        this.verbose = verbose;
    }
    
    /**
     * Définit le mode verbeux.
     *
     * @param verbose 0 pour mode silencieux, 1 pour mode verbeux
     */
    public void setVerbose(int verbose) {
        this.verbose = verbose;
    }
    
    /**
     * Détermine si la technique est applicable sur la grille donnée.
     * Recherche toutes les paires de cases contenant un 0 et un 3 qui sont adjacentes verticalement.
     * 
     * @param grille La grille de jeu à analyser
     * @return true si au moins une paire de cases 0-3 adjacentes a été trouvée, false sinon
     */
    @Override
    public boolean estApplicable(Grille grille) {
        // Réinitialisation des collections pour une nouvelle analyse
        this.paires0et3.clear();
        this.pairesVues.clear();
        
        // Affichage de la grille pour référence (seulement en mode verbeux)
        if (verbose == 1) {
            grille.afficher();
        }
        
        // Recherche des paires de cases 0-3 adjacentes
        rechercherPairesDe0et3(grille);
        
        // Test visuel des voisins (pour information/debug)
        if (verbose == 1) {
            test(grille, 1, 1);
        }
        
        // Affichage des résultats et retour de l'applicabilité
        return afficherResultats();
    }
    
    /**
     * Recherche toutes les paires de cases contenant un 0 et un 3 qui sont adjacentes verticalement.
     * 
     * @param grille La grille à analyser
     */
    private void rechercherPairesDe0et3(Grille grille) {
        int[] dimensions = grille.getDimensionsLogiques();
        int largeurRelle = dimensions[0];
        int hauteurRelle = dimensions[1];
        
        if (verbose == 1) {
            System.out.println("Dimensions logiques de la grille : " + largeurRelle + "x" + hauteurRelle);
        }
        
        // Boucle principale vérifiant si des cases sont adjacentes verticalement
        for(int y = 1; y <= hauteurRelle; y++) {
            for(int x = 1; x <= largeurRelle; x++) {
                // On récupère les voisins de la case courante
                Voisins voisins = new Voisins(grille, x, y);
                voisins.TrouverVoisins();
                
                if(voisins.c != null) {
                    // Vérifier si c'est un 3 avec un 0 en haut ou en bas
                    if(voisins.c.getValeur() == 3) {
                        verifierVoisinage0Pour3(x, y, voisins);
                    }
                    // Vérifier si c'est un 0 avec un 3 en haut ou en bas
                    else if(voisins.c.getValeur() == 0) {
                        verifierVoisinage3Pour0(x, y, voisins);
                    }
                }
            }
        }
    }
    
    /**
     * Vérifie si une case contenant un 3 a un voisin contenant un 0 au-dessus ou en-dessous.
     * 
     * @param x Coordonnée x de la case avec 3
     * @param y Coordonnée y de la case avec 3
     * @param voisins Objet Voisins contenant les informations sur les voisins
     */
    private void verifierVoisinage0Pour3(int x, int y, Voisins voisins) {
        if (verbose == 1) {
            System.out.println("Case (" + x + "," + y + ") contient un 3");
        }
        
        // Vérification du voisin du haut
        int voisinHaut = voisins.getVoisinHaut();
        if (verbose == 1) {
            System.out.println("  Voisin haut: " + voisinHaut);
        }
        if(voisinHaut == 0) {
            ajouterPaireSansDoublon(x, y, x, y-1, true);
        }
        
        // Vérification du voisin du bas
        int voisinBas = voisins.getVoisinBas();
        if (verbose == 1) {
            System.out.println("  Voisin bas: " + voisinBas);
        }
        if(voisinBas == 0) {
            ajouterPaireSansDoublon(x, y, x, y+1, true);
        }
    }
    
    /**
     * Vérifie si une case contenant un 0 a un voisin contenant un 3 au-dessus ou en-dessous.
     * 
     * @param x Coordonnée x de la case avec 0
     * @param y Coordonnée y de la case avec 0
     * @param voisins Objet Voisins contenant les informations sur les voisins
     */
    private void verifierVoisinage3Pour0(int x, int y, Voisins voisins) {
        if (verbose == 1) {
            System.out.println("Case (" + x + "," + y + ") contient un 0");
        }
        
        // Vérification du voisin du haut
        int voisinHaut = voisins.getVoisinHaut();
        if (verbose == 1) {
            System.out.println("  Voisin haut: " + voisinHaut);
        }
        if(voisinHaut == 3) {
            ajouterPaireSansDoublon(x, y-1, x, y, false);
        }
        
        // Vérification du voisin du bas
        int voisinBas = voisins.getVoisinBas();
        if (verbose == 1) {
            System.out.println("  Voisin bas: " + voisinBas);
        }
        if(voisinBas == 3) {
            ajouterPaireSansDoublon(x, y+1, x, y, false);
        }
    }
    
    /**
     * Ajoute une paire de cases à la liste sans créer de doublon.
     * 
     * @param x1 Coordonnée x de la case avec 3
     * @param y1 Coordonnée y de la case avec 3
     * @param x2 Coordonnée x de la case avec 0
     * @param y2 Coordonnée y de la case avec 0
     * @param estCase3Premier indique si le premier jeu de coordonnées correspond à la case 3
     */
    private void ajouterPaireSansDoublon(int x1, int y1, int x2, int y2, boolean estCase3Premier) {
        // Normalisation de la paire
        String cle = normaliserCle(x1, y1, x2, y2);
        
        // Vérifier si cette paire existe déjà
        if (!pairesVues.contains(cle)) {
            if (estCase3Premier) {
                paires0et3.add(new int[][] {{x1, y1}, {x2, y2}});
            } else {
                paires0et3.add(new int[][] {{x1, y1}, {x2, y2}});
            }
            pairesVues.add(cle);
            if (verbose == 1) {
                System.out.println("  → Nouvelle paire ajoutée: " + cle);
            }
        } else if (verbose == 1) {
            System.out.println("  → Paire ignorée (doublon): " + cle);
        }
    }
    
    /**
     * Crée une clé normalisée pour une paire de coordonnées.
     * Pour les paires 0-3, nous utilisons toujours la forme "x3,y3-x0,y0".
     * 
     * @param x1 Coordonnée x de la première case
     * @param y1 Coordonnée y de la première case
     * @param x2 Coordonnée x de la deuxième case
     * @param y2 Coordonnée y de la deuxième case
     * @return Une chaîne de caractères au format "x1,y1-x2,y2" normalisée
     */
    private String normaliserCle(int x1, int y1, int x2, int y2) {
        return Math.min(x1, x2) + "," + Math.min(y1, y2) + "-" + Math.max(x1, x2) + "," + Math.max(y1, y2);
    }
    
    /**
     * Affiche les résultats de l'analyse et détermine si la technique est applicable.
     * Les résultats principaux sont toujours affichés, indépendamment du mode verbeux.
     * 
     * @return true si au moins une paire a été trouvée, false sinon
     */
    private boolean afficherResultats() {
        if(!paires0et3.isEmpty()) {
            System.out.println("Il y a " + paires0et3.size() + " paires de cases 0-3 adjacentes verticalement");
            
            // Affiche toutes les paires trouvées
            for(int i = 0; i < paires0et3.size(); i++) {
                int[][] paire = paires0et3.get(i);
                System.out.println("Paire " + (i+1) + ": Case 3 en (" + 
                                  paire[0][0] + ", " + paire[0][1] + ") et Case 0 en (" + 
                                  paire[1][0] + ", " + paire[1][1] + ")");
            }
            
            // Message final toujours affiché
            System.out.println("La technique des cases 0-3 adjacentes verticalement est applicable: true");
            return true;
        } else {
            // Message final toujours affiché
            System.out.println("La technique des cases 0-3 adjacentes verticalement n'est pas applicable: false");
            return false;
        }
    }

    /**
     * Renvoie la liste des paires de cases 0-3 adjacentes verticalement trouvées.
     * 
     * @return Liste de tableaux de coordonnées [[x3,y3], [x0,y0]] où la première paire est le 3 et la seconde le 0
     */
    public List<int[][]> getPaires0et3() {
        return paires0et3;
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