package com.tpgr3.fx;

import com.tpgr3.backend.Grille;
import com.tpgr3.backend.Slot;
import com.tpgr3.backend.ValidateurDeChemin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tpgr3.backend.Cellule;
import com.tpgr3.backend.Marque.Croix;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;

import com.tpgr3.backend.Techniques.*;
import com.tpgr3.backend.adapter.GrilleAdapter;


public class GameView {

    private static final double CANVAS_TAILLE = 700;
    
    // Liste des slots en surbrillance
    private static final Set<String> slotsEnSurbrillance = new HashSet<>();

    public static Scene getView(Grille grille, String fichier) {
        BorderPane root = new BorderPane();

        // --- Canvas (Grille)
        Canvas canvas = new Canvas(CANVAS_TAILLE, CANVAS_TAILLE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        dessinerGrille(gc, grille);
        root.setCenter(canvas);

        // --- Gestion clic sur Slot
        canvas.setOnMouseClicked(e -> {
            int x = (int) (e.getX() / (CANVAS_TAILLE / grille.getLargeur()));
            int y = (int) (e.getY() / (CANVAS_TAILLE / grille.getHauteur()));
            Cellule cell = grille.getCellule(x, y);

            if (cell instanceof Slot slot) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    slot.actionner();
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    slot.setMarque(new Croix());
                    // Note: on pourrait également enregistrer un coup manuellement
                    // si on voulait un historique spécifique. Ici, la "croix" est imposée,
                    // donc on ne cycle pas via .actionner().
                }
                dessinerGrille(gc, grille);
            }
        });

        // --- VBox boutons
        VBox boutons = new VBox(10);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        boutons.setPrefWidth(250);

        Button btnRetourAvant = new Button("⏩ Retour Avant");
        btnRetourAvant.setOnAction(e -> {
            grille.retourAvant();
            dessinerGrille(gc, grille);
        });

        Button btnRetourArriere = new Button("⏪ Retour Arrière");
        btnRetourArriere.setOnAction(e -> {
            grille.retourArriere();
            dessinerGrille(gc, grille);
        });

        Button btnCheck = new Button("✅ Vérifier");
        btnCheck.setOnAction(e -> {
            // 1) Charger la solution
            // suppose qu'on a la variable 'fichier' (ex: "niveaux/grille4x4.json")
            var dto = GrilleAdapter.loadNiveauDTO(fichier);
            if (dto == null || dto.solution == null) {
                System.out.println("Impossible de charger la solution depuis le JSON.");
                return;
            }
            List<int[]> solutionCoords = GrilleAdapter.getSolutionCoordonnees(dto);

            // 2) Faire la vérification partielle stricte
            ValidateurDeChemin validateur = new ValidateurDeChemin(grille);
            int[] divergence = validateur.checkPartielStrict(solutionCoords);
            
            if (divergence == null) {
                // Aucune divergence => tout est correct ou partiellement correct
                System.out.println("✅ Aucune divergence détectée !");
            } else {
                // Il y a un slot hors solution => on supprime à partir de ce slot
                System.out.printf("❌ Divergence détectée au slot (%d,%d). Suppression...\n", divergence[0], divergence[1]);
                validateur.supprimerDepuis(divergence[0], divergence[1]);

                // Redessiner la grille pour voir les changements
                dessinerGrille(gc, grille);
            }
        });

        Button btnChargerSolution = new Button("📥 Charger la solution");
        btnChargerSolution.setOnAction(e -> {
            chargerSolutionDepuisJson(fichier, grille);
            dessinerGrille(gc, grille); // Mise à jour visuelle
        });



        Button btnHypothese = new Button("❓ Hypothèse");
        btnHypothese.setOnAction(e -> {
            System.out.println("Pas encore implémenté : hypothèse (brancher checkPartiel ?).");
        });

        Button btnAide = new Button("🆘 Aide");
        btnAide.setOnAction(e -> {
            // Lancer la détection des techniques applicables
            afficherAide(grille);
        });


        Button btnRecharger = new Button("🔄 Recharger");
        btnRecharger.setOnAction(e -> {
            grille.rechargerHistorique(); 
            // => reconstruit l’historique depuis l’état actuel
            dessinerGrille(gc, grille);
        });

        Button btnReset = new Button("♻️ Reset");
        btnReset.setOnAction(e -> {
            grille.reinitialiser(); 
            // => efface toute la grille (retour état initial)
            dessinerGrille(gc, grille);
        });

        Button btnAppliquerTech = new Button("DEBUG : Appliquer Technique");
        btnAppliquerTech.setOnAction(e -> {
            appliquerPremiereTechniqueApplicable(grille, gc);
        });

        boutons.getChildren().addAll(
            btnRetourAvant,
            btnRetourArriere,
            btnCheck,
            btnAide,
            btnAppliquerTech,
            btnHypothese,
            btnRecharger,
            btnReset,
            btnChargerSolution
        );
        root.setRight(boutons);

        return new Scene(root, 1000, 700);
    }

    private static void dessinerGrille(GraphicsContext gc, Grille grille) {
        double largeurCanvas = gc.getCanvas().getWidth();
        double hauteurCanvas = gc.getCanvas().getHeight();
    
        int cols = grille.getLargeur();
        int rows = grille.getHauteur();
    
        double tailleCase = Math.min(largeurCanvas / cols, hauteurCanvas / rows);
    
        // Fond de grille (simplifié sans bordures)
        gc.setFill(Color.web("#f0f0f5"));
        gc.fillRect(0, 0, largeurCanvas, hauteurCanvas);
        
        // Parcourir toutes les cellules
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                double px = x * tailleCase;
                double py = y * tailleCase;
    
                Cellule cell = grille.getCellule(x, y);
    
                // Surbrillance avec un nouveau style: contour en pointillé coloré
                if (slotsEnSurbrillance.contains(x + "," + y)) {
                    gc.save();
                    
                    // Couleur vive pour le contour
                    gc.setStroke(Color.DEEPPINK);
                    gc.setLineWidth(3.5);
                    
                    // Motif en pointillé (tirets)
                    gc.setLineDashes(5, 3);
                    
                    // Dessiner un contour autour du slot
                    double margin = tailleCase * 0.15;
                    gc.strokeRect(px + margin, py + margin, 
                                 tailleCase - 2*margin, tailleCase - 2*margin);
                    
                    gc.restore();
                }
    
                // Case chiffrée (sans fond blanc)
                if (cell instanceof com.tpgr3.backend.Case c && c.getValeur() >= 0) {
                    gc.save();
                    
                    // Police plus grande et plus lisible
                    gc.setFont(javafx.scene.text.Font.font("Comic Sans MS", javafx.scene.text.FontWeight.BOLD, 20));
                    
                    // Couleur du texte adaptée à la valeur
                    if (c.getValeur() == 0) {
                        gc.setFill(Color.BLUE);
                    } else if (c.getValeur() == 3) {
                        gc.setFill(Color.RED);
                    } else {
                        gc.setFill(Color.BLACK);
                    }
                    
                    // Centrage amélioré du texte
                    gc.fillText("" + c.getValeur(), 
                               px + tailleCase/2 - 6, 
                               py + tailleCase/2 + 8);
                    
                    gc.restore();
                }
    
                // Point : CaseVide
                if (cell instanceof com.tpgr3.backend.CaseVide) {
                    double r = 10;  // Point plus gros
                    
                    // Dessiner un cercle avec effet 3D
                    gc.setFill(Color.web("#333333"));
                    gc.fillOval(px + tailleCase/2 - r/2, py + tailleCase/2 - r/2, r, r);
                    
                    // Petit reflet pour l'effet 3D
                    double rReflet = r * 0.4;
                    gc.setFill(Color.web("#999999"));
                    gc.fillOval(px + tailleCase/2 - r/2 + r*0.2, py + tailleCase/2 - r/2 + r*0.2, 
                               rReflet, rReflet);
                }
    
                // Slot (Bâton / Croix)
                if (cell instanceof Slot s) {
                    // Calcul des 2 "CaseVide" connectées
                    int vx1, vy1, vx2, vy2;
                    if (x % 2 == 0) {
                        vx1 = x;   vy1 = y - 1;
                        vx2 = x;   vy2 = y + 1;
                    } else {
                        vx1 = x - 1; vy1 = y;
                        vx2 = x + 1; vy2 = y;
                    }
    
                    double[] c1 = centerOfCaseVide(vx1, vy1, tailleCase);
                    double[] c2 = centerOfCaseVide(vx2, vy2, tailleCase);
    
                    if (s.getMarque() instanceof com.tpgr3.backend.Marque.Baton) {
                        gc.save();
                        
                        // Bâton plus épais et arrondi pour aspect crayon/feutre
                        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                        gc.setStroke(Color.web("#222222"));
                        gc.setLineWidth(8);
                        gc.strokeLine(c1[0], c1[1], c2[0], c2[1]);
                        
                        // Effet de brillance au centre du bâton
                        double midX = (c1[0] + c2[0]) / 2;
                        double midY = (c1[1] + c2[1]) / 2;
                        
                        // Déterminer si la ligne est verticale ou horizontale
                        boolean isVertical = Math.abs(c1[0] - c2[0]) < Math.abs(c1[1] - c2[1]);
                        double highlightLength = tailleCase * 0.25;
                        
                        gc.setLineWidth(3);
                        gc.setStroke(Color.web("#666666"));
                        
                        if (isVertical) {
                            gc.strokeLine(midX, midY - highlightLength/2, midX, midY + highlightLength/2);
                        } else {
                            gc.strokeLine(midX - highlightLength/2, midY, midX + highlightLength/2, midY);
                        }
                        
                        gc.restore();
                    } else if (s.getMarque() instanceof com.tpgr3.backend.Marque.Croix) {
                        gc.save();
                        
                        // Croix plus expressive avec effet crayon/feutre
                        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                        gc.setStroke(Color.web("#e74c3c"));  // Rouge plus vif
                        gc.setLineWidth(6);
                        
                        double margin = tailleCase * 0.2;
                        gc.strokeLine(px + margin, py + margin, 
                                     px + tailleCase - margin, py + tailleCase - margin);
                        gc.strokeLine(px + margin, py + tailleCase - margin, 
                                     px + tailleCase - margin, py + margin);
                        
                        gc.restore();
                    }
                }
            }
        }
    }
    
    
   /**
     * Crée et retourne la liste des techniques disponibles.
     * Cette méthode est utilisée par afficherAide et appliquerPremiereTechniqueApplicable.
     * @return La liste des techniques disponibles sans doublons.
     */
    private static List<Techniques> creerListeTechniques() {
        List<Techniques> techniques = new ArrayList<>();
        
        // Ajouter toutes les techniques disponibles
        techniques.add(new Tech_0Rule());
        //techniques.add(new Tech_1Et0Coin());
        //techniques.add(new Tech_1Et2Coin());
        //techniques.add(new Tech_CornerRule());
        //techniques.add(new Tech_3Et0Cote());
        //techniques.add(new Tech_0And3Adjacent());
        //techniques.add(new Tech_0And3Diagonal());
        //techniques.add(new Tech_LoopReaching1());
        //techniques.add(new Tech_LoopReaching3());
        //techniques.add(new Tech_Two3Adjacent());
        //techniques.add(new Tech_Two3Diagonal());
        //techniques.add(new Tech_1Cote());
        // Ajouter d'autres techniques au besoin...
        
        // Supprimer les doublons
        techniques.removeIf(technique -> {
            for (int i = 0; i < techniques.size(); i++) {
                if (technique.getClass().equals(techniques.get(i).getClass()) && technique != techniques.get(i)) {
                    return true;
                }
            }
            return false;
        });
        
        return techniques;
    }
    
    private static void afficherAide(Grille grille) {
        List<Techniques> techniques = creerListeTechniques();
        
        StringBuilder sb = new StringBuilder();
        sb.append("Analyse des techniques disponibles :\n\n");

        for (var technique : techniques) {
            String nom = technique.getClass().getSimpleName();
            boolean applicable = technique.estApplicable(grille);
            sb.append("• ").append(nom)
              .append(" : ").append(applicable ? "APPLICABLE ✅" : "non applicable ❌")
              .append("\n");
        }

        // Affiche un popup
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aide - Techniques");
        alert.setHeaderText("Techniques disponibles sur cette grille");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    /**
     * Met en surbrillance les slots spécifiés pendant 3 secondes.
     * @param coords Liste des coordonnées [x,y] des slots à mettre en surbrillance
     * @param gc GraphicsContext pour dessiner
     * @param grille Grille de jeu
     */
    private static void highlightSlotsFor3Seconds(List<int[]> coords, GraphicsContext gc, Grille grille) {
        // Effacer la surbrillance précédente
        slotsEnSurbrillance.clear();
        
        // Ajouter les nouvelles coordonnées à mettre en surbrillance
        for (int[] c : coords) {
            slotsEnSurbrillance.add(c[0] + "," + c[1]);
        }
        
        // Redessiner la grille avec la surbrillance
        dessinerGrille(gc, grille);

        // Après 3 secondes, retirer la surbrillance
        PauseTransition pause = new PauseTransition(Duration.seconds(10));
        pause.setOnFinished(ev -> {
            slotsEnSurbrillance.clear();
            dessinerGrille(gc, grille);
        });
        pause.play();
    }

    /**
     * Applique la première technique applicable sur la grille en mode surbrillance.
     */
    private static void appliquerPremiereTechniqueApplicable(Grille grille, GraphicsContext gc) {
        // Utiliser la même liste de techniques que pour l'aide
        List<Techniques> techniques = creerListeTechniques();
    
        // Parcourir la liste et afficher en surbrillance la première applicable
        for (var technique : techniques) {
            if (technique.estApplicable(grille)) {
                System.out.println("🔍 " + technique.getClass().getSimpleName() + " est applicable => affichage en surbrillance!");
                
                // Récupérer les slots à marquer sans les modifier
                List<int[]> slotsToMark = technique.getSlotsToMark(grille);
                
                // Mettre ces slots en surbrillance pendant 3 secondes
                highlightSlotsFor3Seconds(slotsToMark, gc, grille);
                return;
            }
        }
    
        // Si aucune technique n'est applicable
        System.out.println("Aucune technique n'est applicable pour le moment.");
    }

    // methode privée pour calculer le centre d'une case vide
    private static double[] centerOfCaseVide(int vx, int vy, double tailleCase) {
        double px = vx * tailleCase + tailleCase / 2; 
        double py = vy * tailleCase + tailleCase / 2;
        return new double[] { px, py };
    }


    //DEBUG : Charger la solution depuis le JSON
    private static void chargerSolutionDepuisJson(String fichier, Grille grille) {
        // 1) Charger le NiveauDTO
        var dto = com.tpgr3.backend.adapter.GrilleAdapter.loadNiveauDTO(fichier);
        if (dto == null || dto.solution == null) {
            System.out.println("Impossible de charger la solution (fichier ou champ solution manquant).");
            return;
        }
    
        // 2) Appliquer la solution à la grille
        for (var slotDTO : dto.solution) {
            // On considère que la solution contient "marque":"Baton"
            var cell = grille.getCellule(slotDTO.x, slotDTO.y);
            if (cell instanceof com.tpgr3.backend.Slot s) {
                s.setMarque(new com.tpgr3.backend.Marque.Baton());
            }
        }
    
        // 3) Optionnel : recharger l’historique pour garder la cohérence
        grille.rechargerHistorique();
    
        System.out.println("Solution appliquée : tous les slots de la solution sont en 'Baton'.");
    }
    
}
