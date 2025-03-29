package com.tpgr3.backend;

import java.util.*;
import java.util.stream.Collectors;

public class Chemin {
    private List<int[]> slots;  // Liste ordonnée de positions (x, y)
    private Set<String> visited; // Pour vérification rapide

    public Chemin() {
        this.slots = new ArrayList<>();
        this.visited = new HashSet<>();
    }

    public void ajouterSlot(int x, int y) {
        slots.add(new int[]{x, y});
        visited.add(x + "," + y);
    }

    public List<int[]> getSlots() {
        return slots;
    }

    public boolean contient(int x, int y) {
        return visited.contains(x + "," + y);
    }

    public int taille() {
        return slots.size();
    }

    public boolean estBoucle() {
        if (slots.size() < 4) return false;
        int[] premier = slots.get(0);
        int[] dernier = slots.get(slots.size() - 1);
        return premier[0] == dernier[0] && premier[1] == dernier[1];
    }

    public boolean estIdentiqueA(Chemin autre) {
        if (autre.taille() != this.taille()) return false;

        Set<String> coords1 = this.slots.stream().map(c -> c[0] + "," + c[1]).collect(Collectors.toSet());
        Set<String> coords2 = autre.slots.stream().map(c -> c[0] + "," + c[1]).collect(Collectors.toSet());
        return coords1.equals(coords2);
    }

    public void afficher() {
        System.out.println("Chemin (taille = " + taille() + ") :");
        for (int[] coord : slots) {
            System.out.print("→ (" + coord[0] + "," + coord[1] + ") ");
        }
        System.out.println(estBoucle() ? " [BOUCLE]" : " [NON BOUCLE]");
    }
}
