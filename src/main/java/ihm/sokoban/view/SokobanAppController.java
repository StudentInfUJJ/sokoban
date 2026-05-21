package ihm.sokoban.view;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import ihm.sokoban.model.Direction;
import ihm.sokoban.model.JeuSokoban;
import ihm.sokoban.model.ResultatMouvement;
import ihm.sokoban.model.TypeCase;
import ihm.sokoban.util.NiveauxTutoriel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SokobanAppController implements Initializable {

    private Stage fenetrePrincipale;

    private JeuSokoban jeu;

    @FXML
    private GridPane zoneJeu;

    @FXML
    private Button nivSuivant;

    @FXML
    private Button nivPrecedent;

    @FXML
    private Button menu;

    @FXML
    private Label mouvements;

    @FXML
    private Label niveau;

    @FXML
    private Label blocsOk;

    public void setFenetrePrincipale(Stage fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
    }

    public void setJeu(JeuSokoban jeu) {
        this.jeu = jeu;
    }

    public void mettreAJourAffichage() {
        scoreboard();
        this.fenetrePrincipale.setTitle(NiveauxTutoriel.getNomNiveau(jeu.getNiveauCourant()));

        if (this.jeu == null)
            return; // Sécurité

        if (this.jeu.estDernierNiveau()) {
            this.nivSuivant.setDisable(true);
        } else {
            this.nivSuivant.setDisable(false);
        }
        if (this.jeu.getNiveauCourant() == 0) {
            this.nivPrecedent.setDisable(true);
        } else {
            this.nivPrecedent.setDisable(false);
        }

        this.zoneJeu.getRowConstraints().clear();
        this.zoneJeu.getColumnConstraints().clear();
        TypeCase[][] tabJeu = this.jeu.getGrille(); // A chaque fois on met à jour le jeu
        int nbLigne = this.jeu.getNbLignes();
        int nbColonnes = this.jeu.getNbColonnes(); // On récupère le vrai nombre de colonnes
        System.out.println(jeu.getNbMouvements());

        for (int i = 0; i < nbLigne; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                switch (tabJeu[i][j]) {
                    case JOUEUR:
                        this.zoneJeu.add(new Rectangle(40, 40, Color.BEIGE), j, i);
                        break;
                    case CAISSE:
                        this.zoneJeu.add(new Rectangle(40, 40, Color.MAROON), j, i);
                        break;
                    case CIBLE:
                        this.zoneJeu.add(new Rectangle(40, 40, Color.GREEN), j, i);
                        break;
                    case MUR:
                        this.zoneJeu.add(new Rectangle(40, 40, Color.BLACK), j, i);
                        break;
                    case CAISSE_SUR_CIBLE:
                        this.zoneJeu.add(new Rectangle(40, 40, Color.BLUE), j, i);
                        break;
                    case JOUEUR_SUR_CIBLE:
                        this.zoneJeu.add(new Rectangle(40, 40, Color.RED), j, i);
                        break;
                    case SOL:
                        this.zoneJeu.add(new Rectangle(40, 40, Color.WHITE), j, i);
                        break;
                    case VIDE:
                        // On ne dessine rien, ou un bloc transparent
                        break;
                }

            }
        }
        statuJeu();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void statuJeu() {
        if (jeu.isNiveauTermine()) {
            finNiv();

        } else if (jeu.isPerdu()) {
            loseNiv();
        }
    }

    private void scoreboard() {
        this.mouvements.setText(String.valueOf(this.jeu.getNbMouvements()));
        this.niveau
                .setText((String.valueOf(this.jeu.getNiveauCourant() + 1)) + " / " + this.jeu.getNbNiveaux());
        this.blocsOk.setText(String.valueOf(jeu.getNbCaissesSurCible()));
    }

    private void finNiv() {
        Alert finNiv = new Alert(AlertType.INFORMATION);
        ButtonType wow, nivSuivant, recommencerTotal;
        wow = null;
        nivSuivant = null;
        recommencerTotal = null;
        if (jeu.estDernierNiveau()) {
            finNiv.setTitle("Dernier niveau terminé...");
            finNiv.setHeaderText("Félicitations vous venez de terminer le dernier niveau !!!");
            finNiv.initOwner(this.fenetrePrincipale);
            finNiv.initModality(Modality.WINDOW_MODAL);
            wow = new ButtonType("Trop fort");
            recommencerTotal = new ButtonType("Aller à niveau 0");

            finNiv.getButtonTypes().setAll(wow, recommencerTotal, ButtonType.NO);

        } else if (!jeu.estDernierNiveau()) {
            finNiv.setTitle("Niveau terminé...");
            finNiv.setHeaderText("Niveau terminé !\nPasser au prochain niveau ?");
            finNiv.initOwner(this.fenetrePrincipale);
            finNiv.initModality(Modality.WINDOW_MODAL);
            nivSuivant = new ButtonType("Niveau suivant");

            finNiv.getButtonTypes().setAll(nivSuivant, ButtonType.NO);

        }

        Optional<ButtonType> reponse = finNiv.showAndWait();

        if (reponse.orElse(null) == nivSuivant) {
            jeu.niveauSuivant();
            mettreAJourAffichage();
        }

        if (reponse.orElse(null) == recommencerTotal) {
            jeu.chargerNiveauParIndex(0);
            mettreAJourAffichage();
        }

    }

    private void loseNiv() {
        Alert loseNiv = new Alert(AlertType.INFORMATION);

        loseNiv.setTitle("Game Over");
        loseNiv.setHeaderText("GAME OVER");
        loseNiv.initOwner(this.fenetrePrincipale);
        loseNiv.initModality(Modality.WINDOW_MODAL);
        ButtonType recommencer = new ButtonType("Recommencer");
        ButtonType annuler = new ButtonType("Annuler");

        loseNiv.getButtonTypes().setAll(recommencer, annuler);

        Optional<ButtonType> reponse = loseNiv.showAndWait();

        if (reponse.orElse(null) == recommencer) {
            jeu.reset();
            mettreAJourAffichage();
        }

    }

    public void executerDeplacement(Direction direction) {
        if (jeu.peutJouer()) {
            ResultatMouvement r = jeu.deplacer(direction); // actionne le mouvement
            switch (r) {
                case DEPLACE:
                    System.out.println("Le joueur regarde" + direction);
                    break;// cas normal
                case POUSSE:
                    System.err.println("Le joueur pousse " + direction);
                    break; // Cas pousser
                case BLOQUE:
                    System.out.println("Le joueur est bloqué par un obstacle de " + direction);
                    break; // Mouvement impossible
                case NIVEAU_TERMINE:
                    System.out.println("Le joueur danse");
                    break;
                default:
                    break;
            }
            mettreAJourAffichage();
        }
    }

    @FXML
    public void actionNivSuivant() {
        if (!this.jeu.estDernierNiveau()) {
            this.jeu.niveauSuivant();
            mettreAJourAffichage();
        }

    }

    @FXML
    public void actionNivPrecedent() {
        if (this.jeu.getNiveauCourant() != 0) {
            this.jeu.niveauPrecedent();
            mettreAJourAffichage();
        }
    }
}
