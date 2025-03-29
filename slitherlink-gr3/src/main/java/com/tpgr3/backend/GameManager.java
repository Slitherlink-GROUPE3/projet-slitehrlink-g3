package com.tpgr3.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tpgr3.backend.dto.GameProgressDTO;
import com.tpgr3.backend.dto.NiveauProgression;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private static final String DOSSIER_SAUVEGARDES = "src/main/resources/sauvegardes/";
    private static final String TEMPLATE = "src/main/resources/game_template.json";

    private static String utilisateur = "default";
    private static GameProgressDTO data;

    public static void setUtilisateur(String nom) {
        utilisateur = nom.toLowerCase().replaceAll("\\s+", "_");
        charger();
    }

    public static String getUtilisateur() {
        return utilisateur;
    }

    public static void charger() {
        try {
            File f = new File(DOSSIER_SAUVEGARDES + utilisateur + ".json");
            if (!f.exists()) {
                copierTemplatePourUtilisateur(f);
            }
            FileReader r = new FileReader(f);
            data = new Gson().fromJson(r, GameProgressDTO.class);
            r.close();
        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement progression : " + e.getMessage());
            data = new GameProgressDTO(); data.progression = new ArrayList<>();
        }
    }

    public static void sauvegarder() {
        try {
            FileWriter w = new FileWriter(DOSSIER_SAUVEGARDES + utilisateur + ".json");
            new GsonBuilder().setPrettyPrinting().create().toJson(data, w);
            w.close();
        } catch (IOException e) {
            System.err.println("❌ Erreur sauvegarde progression : " + e.getMessage());
        }
    }

    public static String getNextNiveauNonTermine() {
        return data.progression.stream()
                .filter(n -> !n.termine)
                .map(n -> n.fichier)
                .findFirst().orElse(null);
    }

    public static List<String> getNiveauxTermines() {
        return data.progression.stream()
                .filter(n -> n.termine)
                .map(n -> n.fichier)
                .toList();
    }

    public static void marquerNiveauTermine(String fichier) {
        for (NiveauProgression n : data.progression) {
            if (n.fichier.equals(fichier)) {
                n.termine = true;
                break;
            }
        }
        sauvegarder();
    }

    private static void copierTemplatePourUtilisateur(File dest) throws IOException {
        File dossier = new File(DOSSIER_SAUVEGARDES);
        if (!dossier.exists()) dossier.mkdirs();
        try (InputStream in = new FileInputStream(TEMPLATE);
             OutputStream out = new FileOutputStream(dest)) {
            in.transferTo(out);
        }
    }
}
