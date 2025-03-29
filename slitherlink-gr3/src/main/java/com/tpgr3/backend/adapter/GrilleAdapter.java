package com.tpgr3.backend.adapter;

import com.google.gson.Gson;
import com.tpgr3.backend.*;
import com.tpgr3.backend.Marque.*;
import com.tpgr3.backend.dto.NiveauDTO;
import com.tpgr3.backend.dto.SlotDTO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GrilleAdapter {

    /**
     * Charge une grille complète avec solution depuis un fichier JSON
     * @param cheminFichier Chemin du fichier JSON dans les ressources
     * @return Grille initialisée avec sa solution + coups joués
     */
    public static Grille chargerGrille(String cheminFichier) {
        NiveauDTO dto = loadNiveauDTO(cheminFichier);
        if (dto == null) {
            System.err.println("Erreur: Impossible de charger le niveau depuis " + cheminFichier);
            return null;
        }
        return chargerDepuisSauvegarde(dto);
    }

    /**
     * Crée une nouvelle Grille à partir des valeurs (sans slots marqués).
     * @param dto NiveauDTO contenant la matrice de valeurs
     * @return Une grille vide (pas de coups joués)
     */
    public static Grille fromNiveauSansSlots(NiveauDTO dto) {
        return new Grille(dto.valeurs);
    }

    /**
     * Construit une Grille et applique les coups "joués" + la solution.
     * @param dto NiveauDTO
     * @return Grille complète
     */
    public static Grille chargerDepuisSauvegarde(NiveauDTO dto) {
        // 1. Construire la Grille
        Grille grille = new Grille(dto.valeurs);

        // 2. Appliquer les coups "joués"
        if (dto.joues != null) {
            for (SlotDTO s : dto.joues) {
                Cellule c = grille.getCellule(s.x, s.y);
                if (c instanceof Slot slot) {
                    slot.setMarque(stringToMarque(s.marque));
                }
            }
        }

        // 3. Enregistrer la solution sous forme de liste de coordonnées
        if (dto.solution != null) {
            List<int[]> coordsSolution = new ArrayList<>();
            for (SlotDTO solSlot : dto.solution) {
                coordsSolution.add(new int[]{solSlot.x, solSlot.y});
            }
            grille.setSolution(coordsSolution);
        }

        // 4. Recharger l'historique (pour que retourArriere/Avant fonctionne)
        grille.rechargerHistorique();

        return grille;
    }

    /**
     * Récupère la liste de coordonnées (x,y) de la solution.
     * Utile si on souhaite faire un check par nous-même.
     * @param dto NiveauDTO
     * @return Liste de coordonnées (x,y) attendues
     */
    public static List<int[]> getSolutionCoordonnees(NiveauDTO dto) {
        if (dto.solution == null) {
            return new ArrayList<>();
        }
        return dto.solution.stream()
            .map(s -> new int[]{s.x, s.y})
            .toList();
    }

    /**
     * Convertit une chaîne ("Baton", "Croix", ou autre) en Marque correspondante.
     */
    private static Marque stringToMarque(String m) {
        return switch (m) {
            case "Baton" -> new Baton();
            case "Croix" -> new Croix();
            default -> new Neutre();
        };
    }

    /**
     * Charge un objet NiveauDTO depuis un fichier JSON placé dans les ressources.
     * @param cheminFichier Nom du fichier (par ex: "niveau2.json")
     * @return L'objet NiveauDTO, ou null en cas d'erreur
     */
    public static NiveauDTO loadNiveauDTO(String cheminFichier) {
        try {
            InputStream input = GrilleAdapter.class.getClassLoader().getResourceAsStream(cheminFichier);
            if (input == null) {
                throw new FileNotFoundException("Fichier introuvable : " + cheminFichier);
            }
            Gson gson = new Gson();
            return gson.fromJson(new InputStreamReader(input), NiveauDTO.class);
        } catch (IOException e) {
            System.err.println("Erreur chargement NiveauDTO: " + e.getMessage());
            return null;
        }
    }
}
