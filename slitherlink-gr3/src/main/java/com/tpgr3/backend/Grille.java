package com.tpgr3.backend;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import com.tpgr3.backend.Slot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tpgr3.backend.Case;
import com.tpgr3.backend.CaseVide;
import com.tpgr3.backend.Cellule;
import com.tpgr3.backend.Coup;
import com.tpgr3.backend.adapter.GrilleAdapter;

import com.tpgr3.backend.Marque.*;
import com.tpgr3.backend.dto.*;




/**
 * Représente une grille de jeu contenant des cellules de type {@link Case}, {@link Slot} et {@link CaseVide}.
 * La grille est initialisée avec une matrice de valeurs et organise les cellules en conséquence.
 */
public class Grille {

    /** Matrice contenant les {@link Cellule} de la grille. */
    private Cellule[][] matrice;

    /** Dimensions de la grille. */
    public int largeur; /*faudra remmetre en privée zebi les getters c'est pas pour rien */ 
    public int hauteur;

    /** Sauvegarde des valeurs des cases fournies en paramètre. */
    public int[][] valeurs;

    //solution
    private List<int[]> solutionCoords; // stockage en interne
    
    private final List<Coup> historique = new ArrayList<>(); // historique des coups
    private int indexHistorique = -1; // index de l'historique

    private List<int[]> solution; // la solution attendue


    /**
     * Constructeur de la classe Grille.
     *
     * @param valeurs Tableau 2D contenant les valeurs des cases (uniquement pour les cases avec chiffres).
     */
    public Grille(int[][] valeurs) {
        this.hauteur = valeurs.length * 2 + 1;  // Calcul de la hauteur de la grille
        this.largeur = valeurs[0].length * 2 + 1;  // Calcul de la largeur de la grille
        this.matrice = new Cellule[hauteur][largeur];  // Initialisation de la matrice
        this.valeurs = valeurs;  // Sauvegarde des valeurs

        initialiserGrille(valeurs);  // Initialisation de la grille
    }

    /**
     * Initialise la grille en créant des objets {@link Case}, {@link Slot} et {@link CaseVide}
     * en fonction de leur position dans la matrice.
     * - Les {@link Case} sont placées aux coordonnées impaires (x et y impairs).
     * - Les {@link Slot} sont placés aux coordonnées mixtes (x pair et y impair, ou x impair et y pair).
     * - Les {@link CaseVide} sont placées aux coordonnées paires (x et y pairs).
     *
     * @param valeurs Tableau 2D contenant les valeurs des cases numériques.
     */
    private void initialiserGrille(int[][] valeurs) {
        int nbSlot = 0;  // Compteur de slots créés
        int nbCase = 0;  // Compteur de cases créées
        int nbVide = 0;  // Compteur de cases vides créées

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {

                // Ajouter une Case si x et y sont impairs
                if (x % 2 == 1 && y % 2 == 1) {
                    matrice[y][x] = new Case(x, y, valeurs[y / 2][x / 2]);
                    nbCase++;
                }
                // Ajouter un Slot si x est pair et y est impair, ou x est impair et y est pair
                else if ((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0)) {
                    matrice[y][x] = new Slot(x, y,this);
                    nbSlot++;
                }
                // Ajouter une CaseVide si x et y sont pairs
                else if (x % 2 == 0 && y % 2 == 0) {
                    matrice[y][x] = new CaseVide(x, y);
                    nbVide++;
                }
            }
        }

        // Affichage des statistiques d'initialisation
        System.out.println("\n\nGrille initialisée");
        System.out.println("Nombre de cases créées: " + nbCase);
        System.out.println("Nombre de slots créés: " + nbSlot);
        System.out.println("Nombre de cases vides créées: " + nbVide);
    }

    /**
     * Réinitialise la grille à son état initial.
     */
    public void reinitialiser() {
        initialiserGrille(valeurs);
        System.out.println("Grille réinitialisée aux valeurs par défaut");
    }

    /**
     * Affiche la grille dans la console.
     * Chaque cellule est représentée par son affichage spécifique défini dans {@link Cellule#afficher()}.
     */
    public void afficher() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (matrice[y][x] != null) {
                    System.out.print(String.format("%-3s", matrice[y][x].afficher()) + " ");
                } else {
                    System.out.print("    ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Permet d'interagir avec une cellule à la position (x, y).
     * Le comportement dépend du type de cellule.
     *
     * @param x Coordonnée x de la cellule.
     * @param y Coordonnée y de la cellule.
     */
    public void actionnerCelule(int x, int y) {
        if (estValide(x, y)) {
            matrice[y][x].actionner();
        } else {
            System.out.println("Coordonnées invalides.");
        }
    }

    /**
     * Retourne la hauteur de la grille.
     *
     * @return La hauteur de la grille.
     */
    public int getHauteur() {
        return hauteur;
    }

    /**
     * Retourne la largeur de la grille.
     *
     * @return La largeur de la grille.
     */
    public int getLargeur() {
        return largeur;
    }

    /**
     * Retourne la matrice complète de la grille.
     *
     * @return La matrice de cellules.
     */
    public Cellule[][] getMatrice() {
        return matrice;
    }

    //Retourne la cellule à la position spécifiée si valide, sinon null.
    public Cellule getCellule(int x, int y) {
        if (estValide(x, y)) {
            return matrice[y][x];
        }
        return null;
    }

    public int getHistoriqueSize() {
        return historique.size();
    }
    
    public int getHistoriqueIndex() {
        return indexHistorique;
    }
    
    public Coup getCoup(int i) {
        return historique.get(i);
    }

    public void setSolution(List<int[]> solution) {
        this.solution = solution;
    }
    
    public List<int[]> getSolution() {
        return solution;
    }

    /**
     * Vérifie si une case est dans un coin logique de la grille.
     * @param x Coordonnée x de la case (doit être impaire)
     * @param y Coordonnée y de la case (doit être impaire)
     */
    public boolean estDansUnCoin(int x, int y) {
        int[] dim = getDimensionsLogiques();
        return (x == 1 && y == 1) || (x == dim[0] * 2 - 1 && y == 1) 
            || (x == 1 && y == dim[1] * 2 - 1) || (x == dim[0] * 2 - 1 && y == dim[1] * 2 - 1);
    }

    /**
     * Retourne les dimensions logiques de la grille (nombre de cases en largeur/hauteur).
     */
    public int[] getDimensionsLogiques() {
        return new int[] { (largeur - 1) / 2, (hauteur - 1) / 2 };
    }


    /**
     * Vérifie si les coordonnées (x, y) sont valides dans la grille.
     *
     * @param x Coordonnée x à vérifier.
     * @param y Coordonnée y à vérifier.
     * @return true si les coordonnées sont valides, false sinon.
     */
    public boolean estValide(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }

    /**
     * Retourne les Slots adjacents à une Case.
     * @param x Coordonnée x de la Case (doit être impaire)
     * @param y Coordonnée y de la Case (doit être impaire)
     * @return Liste des Slots adjacents.
     */
    public List<Slot> getSlotsAdjacentsCase(int x, int y) {
        List<Slot> slots = new ArrayList<>();
        if (x % 2 != 1 || y % 2 != 1) return slots; // Vérification Case
        
        // Directions : haut, bas, gauche, droite
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (estValide(newX, newY)) {
                Cellule c = getCellule(newX, newY);
                if (c instanceof Slot) slots.add((Slot)c);
            }
        }
        return slots;
    }

    /**
     * Retourne les Cases adjacentes à une Case (2 cases de distance).
     * @param x Coordonnée x de la Case (doit être impaire)
     * @param y Coordonnée y de la Case (doit être impaire)
     * @return Liste des Cases adjacentes.
     */
    public List<Case> getCasesAdjacentes(int x, int y) {
        List<Case> cases = new ArrayList<>();
        if (x % 2 != 1 || y % 2 != 1) return cases;
        
        // Directions : haut, bas, gauche, droite (2 unités)
        int[][] directions = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (estValide(newX, newY)) {
                Cellule c = getCellule(newX, newY);
                if (c instanceof Case) cases.add((Case)c);
            }
        }
        return cases;
    }

    /**
     * Retourne les Cases connectées à un Slot.
     * @param x Coordonnée x du Slot
     * @param y Coordonnée y du Slot
     * @return Liste des Cases connectées.
     */
    public List<Case> getCasesPourSlot(int x, int y) {
        List<Case> cases = new ArrayList<>();
        if (!((x % 2 == 0 && y % 2 == 1) || (x % 2 == 1 && y % 2 == 0))) {
            return cases;
        }
        
        // Slot horizontal
        if (x % 2 == 0) {
            if (estValide(x - 1, y)) cases.add((Case)getCellule(x - 1, y));
            if (estValide(x + 1, y)) cases.add((Case)getCellule(x + 1, y));
        } 
        // Slot vertical
        else {
            if (estValide(x, y - 1)) cases.add((Case)getCellule(x, y - 1));
            if (estValide(x, y + 1)) cases.add((Case)getCellule(x, y + 1));
        }
        return cases;
    }

/*=======================================================================================
 * ========================= CHECKING DES CHEMINS =========================================
 * =======================================================================================
*/
    /**
     * Détecte tous les chemins continus formés de Slots marqués "Bâton".
     * Chaque chemin est une suite connectée sans branchement.
     *
     * @return Liste des chemins trouvés.
     */
    public List<Chemin> detecterChemins() {
        List<Chemin> chemins = new ArrayList<>();
        boolean[][] visité = new boolean[hauteur][largeur];

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Cellule cellule = getCellule(x, y);

                if (cellule instanceof Slot slot && slot.getMarque().getValeur() == Constantes.BATON && !visité[y][x]) {
                    Chemin chemin = new Chemin();
                    dfsChemin(x, y, visité, chemin);
                    if (chemin.taille() >= 2) { // On ignore les bâtons isolés
                        chemins.add(chemin);
                    }
                }
            }
        }

        return chemins;
    }

    
    //Metode interne pour la recherche en profondeur (DFS) des chemins. 
    private void dfsChemin(int x, int y, boolean[][] visité, Chemin chemin) {
        visité[y][x] = true;
        chemin.ajouterSlot(x, y);
    
        // Directions : haut, bas, gauche, droite
        int[][] dirs = {{0,-2}, {0,2}, {-2,0}, {2,0}};
    
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
    
            if (!estValide(nx, ny)) continue;
    
            Cellule c = getCellule(nx, ny);
            if (c instanceof Slot s && s.getMarque().getValeur() == Constantes.BATON && !visité[ny][nx]) {
                dfsChemin(nx, ny, visité, chemin);
            }
        }
    } 

    public void verifierSolutionSiPossible() {
        // Si pas de solution enregistrée, on ne fait rien
        if (this.solution == null || this.solution.isEmpty()) {
            System.out.println("Aucune solution enregistrée dans la grille.");
            return;
        }
    
        // Crée un validateur qui travaille sur CETTE grille
        ValidateurDeChemin validateur = new ValidateurDeChemin(this);
    
        // On vérifie la solution complète
        boolean reussi = validateur.check(this.solution);
        if (reussi) {
            System.out.println("Niveau réussi !");
        } else {
            System.out.println("Pas encore résolu...");
        }
    }
    

    /*========================================================================================
    *  ========================= SAUVAGARDE ET JSON =========================================
    * =======================================================================================
    */
    public void sauvegarderGrille(String chemin, int niveau) {
        try {
            NiveauDTO dto = new NiveauDTO();
            dto.niveau = niveau;
            dto.valeurs = this.valeurs;

            // Sauvegarder uniquement les coups joués (slots avec marque ≠ neutre)
            List<SlotDTO> joues = new ArrayList<>();
            for (int y = 0; y < hauteur; y++) {
                for (int x = 0; x < largeur; x++) {
                    Cellule c = getCellule(x, y);
                    if (c instanceof Slot s && !(s.getMarque() instanceof com.tpgr3.backend.Marque.Neutre)) {
                        SlotDTO slotDTO = new SlotDTO();
                        slotDTO.x = x;
                        slotDTO.y = y;
                        slotDTO.marque = s.getMarque().getClass().getSimpleName();
                        joues.add(slotDTO);
                    }
                }
            }

            dto.joues = joues;

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(chemin);
            gson.toJson(dto, writer);
            writer.close();

            System.out.println("💾 Grille sauvegardée avec historique dans " + chemin);
        } catch (IOException e) {
            System.err.println("Erreur de sauvegarde : " + e.getMessage());
        }
    }

    public static Grille chargerGrille(String chemin) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(chemin);
            NiveauDTO dto = gson.fromJson(reader, NiveauDTO.class);
            reader.close();
            return GrilleAdapter.chargerDepuisSauvegarde(dto);
        } catch (IOException e) {
            System.err.println("Erreur de chargement : " + e.getMessage());
            return null;
        }
    }

    public static Grille chargerGrilleVide(String chemin) {
        try {
            InputStream input = Grille.class.getClassLoader().getResourceAsStream(chemin);
            if (input == null) throw new FileNotFoundException("Fichier introuvable dans resources : " + chemin);
            Gson gson = new Gson();
            NiveauDTO dto = gson.fromJson(new InputStreamReader(input), NiveauDTO.class);
            return GrilleAdapter.fromNiveauSansSlots(dto);
        } catch (IOException e) {
            System.err.println("Erreur de chargement (vierge) : " + e.getMessage());
            return null;
        }
    }

    
    // Méthode pour annuler le dernier coup
    public void enregistrerCoup(int x, int y, Marque ancienne, Marque nouvelle) {
        if (!estValide(x, y)) return;
        historique.subList(indexHistorique + 1, historique.size()).clear(); // on écrase les coups futurs
        historique.add(new Coup(x, y, ancienne, nouvelle));
        indexHistorique++;
    }

    // Méthode pour appliquer une marque à une cellule
    public void retourArriere() {
        if (indexHistorique < 0) return;
        Coup coup = historique.get(indexHistorique);
        appliquerMarque(coup.getX(), coup.getY(), coup.getAncienne());
        indexHistorique--;
    }
    // Méthode pour appliquer une marque à une cellule
    public void retourAvant() {
        if (indexHistorique + 1 >= historique.size()) return;
        indexHistorique++;
        Coup coup = historique.get(indexHistorique);
        appliquerMarque(coup.getX(), coup.getY(), coup.getNouvelle());
    }
    // Méthode pour appliquer une marque à une cellule
    private void appliquerMarque(int x, int y, Marque marque) {
        Cellule cell = getCellule(x, y);
        if (cell instanceof Slot slot) {
            slot.setMarque(marque);
        }
    }

    
    /**
     * Recharge l'historique des coups à partir de l'état actuel de la grille.
     * 
     * Cette méthode réinitialise l'historique des actions effectuées sur la grille
     * et le reconstruit en parcourant toutes les cellules. Si une cellule est un
     * Slot et qu'elle contient une marque différente de Neutre, un coup est
     * enregistré dans l'historique pour représenter le changement de Neutre vers
     * la marque actuelle.
     * 
     * Après l'exécution, l'historique contient tous les coups nécessaires pour
     * recréer l'état actuel de la grille à partir d'un état initial neutre.
     */
    public void rechargerHistorique() {
        historique.clear();
        indexHistorique = -1;

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Cellule c = getCellule(x, y);
                if (c instanceof Slot s && !(s.getMarque() instanceof Neutre)) {
                    // Considère qu'on est parti de Neutre → Marque actuelle
                    enregistrerCoup(x, y, new Neutre(), s.getMarque());
                }
            }
        }

        System.out.println("🕘 Historique rechargé depuis l’état actuel (" + historique.size() + " coups)");
    }

        
    
}