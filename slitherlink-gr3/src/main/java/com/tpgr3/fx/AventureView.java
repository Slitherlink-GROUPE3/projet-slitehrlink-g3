package com.tpgr3.fx;

import com.tpgr3.backend.GameManager;
import com.tpgr3.backend.Grille;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class AventureView {

    public static Parent getView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label label = new Label("Entrez votre pseudo :");
        TextField champPseudo = new TextField();
        Button valider = new Button("Valider");

        Label messageErreur = new Label();
        messageErreur.setStyle("-fx-text-fill: red;");

        valider.setOnAction(e -> {
            String pseudo = champPseudo.getText().trim();
            messageErreur.setText(""); // clear ancien message

            if (!pseudo.isEmpty()) {
                GameManager.setUtilisateur(pseudo);
                String fichier = GameManager.getNextNiveauNonTermine();
                if (fichier != null) {
                    Grille grille = Grille.chargerGrilleVide(fichier);
                    if (grille != null) {
                        Scene gameScene = GameView.getView(grille, fichier);
                        Main.changerScene(gameScene);
                    } else {
                        messageErreur.setText("❌ Échec de chargement du niveau : " + fichier);
                    }
                } else {
                    messageErreur.setText("Aucun niveau disponible pour ce joueur.");
                }
            } else {
                messageErreur.setText("Veuillez entrer un pseudo.");
            }
        });

        root.getChildren().addAll(label, champPseudo, valider, messageErreur);
        return root;
    }
}
