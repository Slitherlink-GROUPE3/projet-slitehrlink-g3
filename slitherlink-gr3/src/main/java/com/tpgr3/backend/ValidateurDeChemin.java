package com.tpgr3.backend;

import com.tpgr3.backend.Marque.*;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateurDeChemin {

    private final Grille grille;

    public ValidateurDeChemin(Grille grille) {
        this.grille = grille;
    }

    public boolean check(List<int[]> solution) {
        Set<String> solutionSet = coordsToSet(solution);

        for (Chemin chemin : grille.detecterChemins()) {
            Set<String> cheminSet = coordsToSet(chemin.getSlots());
            if (cheminSet.equals(solutionSet)) {
                System.out.println("✅ Chemin EXACT trouvé.");
                return true;
            }
        }

        System.out.println("❌ Aucun chemin correspondant.");
        return false;
    }

    public boolean checkPartiel(List<int[]> solution) {
        Set<String> solutionSet = coordsToSet(solution);

        for (Chemin chemin : grille.detecterChemins()) {
            Set<String> cheminSet = coordsToSet(chemin.getSlots());
            if (solutionSet.containsAll(cheminSet)) {
                System.out.println("🟡 Chemin partiel détecté.");
                return true;
            }
        }

        System.out.println("❌ Aucun chemin partiel conforme.");
        return false;
    }

    public int[] checkPartielStrict(List<int[]> solution) {
        Set<String> solutionSet = coordsToSet(solution);

        for (Chemin chemin : grille.detecterChemins()) {
            for (int[] slot : chemin.getSlots()) {
                String key = slot[0] + "," + slot[1];
                if (!solutionSet.contains(key)) {
                    System.out.printf("❌ Divergence au slot (%d,%d)%n", slot[0], slot[1]);
                    return slot;
                }
            }
        }

        System.out.println("✅ Aucun point de divergence détecté.");
        return null;
    }

    public void supprimerDepuis(int x, int y) {
        Set<String> visités = new HashSet<>();
        dfsSuppression(x, y, visités);
    }

    private void dfsSuppression(int x, int y, Set<String> visités) {
        if (!grille.estValide(x, y)) return;

        String key = x + "," + y;
        if (visités.contains(key)) return;
        visités.add(key);

        Cellule cellule = grille.getCellule(x, y);
        if (!(cellule instanceof Slot slot)) return;

        if (!(slot.getMarque() instanceof Baton)) return;

        // Remet en Neutre
        slot.setMarque(new Neutre());

        int[][] dirs = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        for (int[] d : dirs) {
            dfsSuppression(x + d[0], y + d[1], visités);
        }
    }

    private Set<String> coordsToSet(List<int[]> liste) {
        return liste.stream().map(c -> c[0] + "," + c[1]).collect(Collectors.toSet());
    }
}
