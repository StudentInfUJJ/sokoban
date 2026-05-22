package ihm.sokoban.view;

import java.net.URL;
import java.nio.file.Path;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import ihm.sokoban.model.Direction;
import ihm.sokoban.model.JeuSokoban;
import ihm.sokoban.model.ResultatMouvement;
import ihm.sokoban.model.SokobanException;
import ihm.sokoban.model.TypeCase;
import ihm.sokoban.util.LoaderNiveauxXSB;
import ihm.sokoban.util.LoaderNiveauxXSB.Banque;
import ihm.sokoban.util.NiveauxSokoban;
import ihm.sokoban.util.NiveauxTutoriel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;

import java.util.HashMap;

public class SokobanAppController implements Initializable {

    // === Fenêtre principale et Modèle ===
    private Stage fenetrePrincipale;
    // Initialisation du jeu (par défaut commence par Tutoriel)
    private JeuSokoban jeu = new JeuSokoban(NiveauxTutoriel.getNiveaux(), NiveauxTutoriel.getNoms(), 0);

    // === Composants FXML (Interface Graphique) ===
    @FXML
    private GridPane zoneJeu;

    @FXML
    private MenuItem setAutoProgression;

    @FXML
    private Button recommencer;

    @FXML
    private Button nivSuivant;

    @FXML
    private Button nivPrecedent;

    @FXML
    private Label mouvements;

    @FXML
    private Label poussees;

    @FXML
    private Label niveau;

    @FXML
    private Label blocsOk;

    @FXML
    private Menu choixNiveaux;

    // === État interne et Propriétés du jeu ===
    private Boolean autoProgression = false;
    private IntegerProperty nbMouvements;
    private IntegerProperty nbPoussees;
    private String[] nomsNiveaux = NiveauxTutoriel.getNoms();
    // === Sprites et Images ===
    // Joueur
    private ImageView imagJoueur = new ImageView(
            new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurBas.png")));

    // Boite
    private Image imgCaisse = new Image(getClass().getResourceAsStream("/ihm/sokoban/images/Caisse.png"));

    // Boite sur cible
    private Image imgCaisseSurCible = new Image(
            getClass().getResourceAsStream("/ihm/sokoban/images/CaisseSurCible.png"));

    // Mur
    private Image imgMur = new Image(getClass().getResourceAsStream("/ihm/sokoban/images/MurFinal.png"));

    private Image imgSol = new Image(getClass().getResourceAsStream("/ihm/sokoban/images/SolFinal.png"));

    // Cible
    private Image imgCible = new Image(getClass().getResourceAsStream("/ihm/sokoban/images/Cible.png")); // TO DO

    // Joueur sur Cible
    private Image joueurSurCibleHaut = new Image(
            getClass().getResourceAsStream("/ihm/sokoban/images/JoueurSurCibleHaut.png"));
    private Image joueurSurCibleBas = new Image(
            getClass().getResourceAsStream("/ihm/sokoban/images/JoueurSurCibleBas.png"));
    private Image joueurSurCibleGauche = new Image(
            getClass().getResourceAsStream("/ihm/sokoban/images/JoueurSurCibleGauche.png"));
    private Image joueurSurCibleDroite = new Image(
            getClass().getResourceAsStream("/ihm/sokoban/images/JoueurSurCibleDroite.png"));

    // Vide
    private Image imgVide = new Image(
            getClass().getResourceAsStream("/ihm/sokoban/images/Vide.png")); // TO DO

    // Joueur en fonction de direction et mouvement

    private HashMap<Direction, Image> deplacement = new HashMap<>();
    private HashMap<Direction, Image> poussee = new HashMap<>();

    // Direction joueur

    Direction directionJoueur;

    public void setFenetrePrincipale(Stage fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        this.fenetrePrincipale.setOnCloseRequest(event -> {
            actionQuitter();
            event.consume();
        });
    }

    @FXML
    public void setJeuSokoban() {
        this.jeu = new JeuSokoban(0);
        this.nomsNiveaux = NiveauxSokoban.getNoms();
        this.choixNiveaux.getItems().clear();
        setMenu();
        setSprites();

        mettreAJourAffichage();
    }

    @FXML
    public void setJeuTutos() {
        this.jeu = new JeuSokoban(NiveauxTutoriel.getNiveaux(), NiveauxTutoriel.getNoms(), 0);
        this.nomsNiveaux = NiveauxTutoriel.getNoms();
        this.choixNiveaux.getItems().clear();
        setMenu();
        setSprites();
        this.imagJoueur = new ImageView(new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurBas.png")));
        mettreAJourAffichage();
    }

    public void setSprites() {

        this.deplacement.put(Direction.BAS,
                new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurBas.png")));
        this.deplacement.put(Direction.HAUT,
                new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurHaut.png")));
        this.deplacement.put(Direction.GAUCHE,
                new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurGauche.png")));
        this.deplacement.put(Direction.DROITE,
                new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurDroite.png")));
        this.poussee.put(Direction.BAS,
                new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurBasPousse.png")));
        this.poussee.put(Direction.HAUT,
                new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurHautPousse.png")));
        this.poussee.put(Direction.GAUCHE,
                new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurGauchePousse.png")));
        this.poussee.put(Direction.DROITE,
                new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurDroitePousse.png")));

    }

    public void mettreAJourAffichage() {
        this.imagJoueur.setFitHeight(40);
        this.imagJoueur.setFitWidth(40);

        scoreboard();
        this.fenetrePrincipale.setTitle(nomsNiveaux[jeu.getNiveauCourant()]);

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

        this.zoneJeu.getChildren().clear();
        this.zoneJeu.getRowConstraints().clear();
        this.zoneJeu.getColumnConstraints().clear();
        TypeCase[][] tabJeu = this.jeu.getGrille(); // A chaque fois on met à jour le jeu
        int nbLigne = this.jeu.getNbLignes();
        int nbColonnes = this.jeu.getNbColonnes(); // On récupère le vrai nombre de colonnes

        for (int i = 0; i < nbLigne; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                switch (tabJeu[i][j]) {
                    case JOUEUR:
                        this.zoneJeu.add(imagJoueur, j, i);
                        break;
                    case CAISSE:
                        ImageView tempCaisse = new ImageView(imgCaisse);
                        tempCaisse.setFitWidth(40);
                        tempCaisse.setFitHeight(40);
                        this.zoneJeu.add(tempCaisse, j, i);
                        break;
                    case CIBLE:
                        ImageView tempCible = new ImageView(imgCible);
                        tempCible.setFitWidth(40);
                        tempCible.setFitHeight(40);
                        this.zoneJeu.add(tempCible, j, i);
                        break;
                    case MUR:
                        ImageView tempMur = new ImageView(imgMur);
                        tempMur.setFitWidth(40);
                        tempMur.setFitHeight(40);
                        this.zoneJeu.add(tempMur, j, i);
                        break;
                    case CAISSE_SUR_CIBLE:
                        ImageView tempCaisseSurCible = new ImageView(imgCaisseSurCible);
                        tempCaisseSurCible.setFitWidth(40);
                        tempCaisseSurCible.setFitHeight(40);
                        this.zoneJeu.add(tempCaisseSurCible, j, i);
                        break;
                    case JOUEUR_SUR_CIBLE:
                        if (this.directionJoueur == Direction.BAS) {
                            ImageView tempJoueurSurCible = new ImageView(joueurSurCibleBas);
                            tempJoueurSurCible.setFitWidth(40);
                            tempJoueurSurCible.setFitHeight(40);
                            this.zoneJeu.add(tempJoueurSurCible, j, i);
                        }
                        if (this.directionJoueur == Direction.HAUT) {
                            ImageView tempJoueurSurCible = new ImageView(joueurSurCibleHaut);
                            tempJoueurSurCible.setFitWidth(40);
                            tempJoueurSurCible.setFitHeight(40);
                            this.zoneJeu.add(tempJoueurSurCible, j, i);
                        }
                        if (this.directionJoueur == Direction.GAUCHE) {
                            ImageView tempJoueurSurCible = new ImageView(joueurSurCibleGauche);
                            tempJoueurSurCible.setFitWidth(40);
                            tempJoueurSurCible.setFitHeight(40);
                            this.zoneJeu.add(tempJoueurSurCible, j, i);
                        }
                        if (this.directionJoueur == Direction.DROITE) {
                            ImageView tempJoueurSurCible = new ImageView(joueurSurCibleDroite);
                            tempJoueurSurCible.setFitWidth(40);
                            tempJoueurSurCible.setFitHeight(40);
                            this.zoneJeu.add(tempJoueurSurCible, j, i);
                        }
                        break;
                    case SOL:
                        ImageView tempSol = new ImageView(imgSol);
                        tempSol.setFitHeight(40);
                        tempSol.setFitWidth(40);
                        this.zoneJeu.add(tempSol, j, i);
                        break;
                    case VIDE:
                        ImageView tempVide = new ImageView(imgVide);
                        tempVide.setFitHeight(40);
                        tempVide.setFitWidth(40);
                        this.zoneJeu.add(tempVide, j, i);
                        break;
                }

            }
        }
        statuJeu();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setSprites();
    }

    private void statuJeu() {
        if (jeu.isNiveauTermine()) {
            finNiv();

        } else if (jeu.isPerdu()) {
            loseNiv();
        }
    }

    private void scoreboard() {
        nbMouvements = new SimpleIntegerProperty(jeu.getNbMouvements());
        nbPoussees = new SimpleIntegerProperty(jeu.getNbPoussees());
        this.mouvements.textProperty().bind(nbMouvements.asString());
        this.poussees.textProperty().bind(nbPoussees.asString());

        this.niveau
                .setText((String.valueOf(this.jeu.getNiveauCourant() + 1)) + " / " + this.jeu.getNbNiveaux());
        this.blocsOk.setText(String.valueOf(jeu.getNbCaissesSurCible()) + " / " + jeu.getNbCaisses());
    }

    private void finNiv() {
        Alert finNiv = new Alert(AlertType.INFORMATION);
        ButtonType wow, nivSuivant, recommencerTotal;
        wow = null;
        nivSuivant = null;
        recommencerTotal = null;
        Optional<ButtonType> reponse = null;
        if (jeu.estDernierNiveau() && !autoProgression) {
            finNiv.setTitle("Dernier niveau terminé...");
            finNiv.setHeaderText("Félicitations vous venez de terminer le dernier niveau !!!");
            finNiv.initOwner(this.fenetrePrincipale);
            finNiv.initModality(Modality.WINDOW_MODAL);
            wow = new ButtonType("Trop fort");
            recommencerTotal = new ButtonType("Aller à niveau 0");

            finNiv.getButtonTypes().setAll(wow, recommencerTotal, ButtonType.NO);

        } else if (jeu.estDernierNiveau() && autoProgression) {
            jeu.chargerNiveauParIndex(0);
            mettreAJourAffichage();
        }

        else if (!jeu.estDernierNiveau() && !autoProgression) {
            finNiv.setTitle("Niveau terminé...");
            finNiv.setHeaderText("Niveau terminé !\nPasser au prochain niveau ?");
            finNiv.initOwner(this.fenetrePrincipale);
            finNiv.initModality(Modality.WINDOW_MODAL);
            nivSuivant = new ButtonType("Niveau suivant");

            finNiv.getButtonTypes().setAll(nivSuivant, ButtonType.NO);

        } else if (!jeu.estDernierNiveau() && autoProgression) {
            actionNivSuivant();
        }

        if (!autoProgression) {
            reponse = finNiv.showAndWait();
        }

        if (reponse != null && reponse.orElse(null) == nivSuivant) {
            actionNivSuivant();
        }

        if (reponse != null && reponse.orElse(null) == recommencerTotal) {
            actionRecommencer();
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
            actionRecommencer();
        }

    }

    public void executerDeplacement(Direction direction) {
        if (jeu.peutJouer()) {
            ResultatMouvement r = jeu.deplacer(direction); // actionne le mouvement
            directionJoueur = direction;
            switch (r) {
                case DEPLACE:
                    majJoueur(direction, r);
                    break;// cas normal
                case POUSSE:
                    majJoueur(direction, r);
                    break; // Cas pousser
                case BLOQUE:
                    majJoueur(direction, r);
                    break; // Mouvement impossible
                case NIVEAU_TERMINE:
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
            try {
                this.jeu.niveauSuivant();
                mettreAJourAffichage();
            } catch (SokobanException SE) {
                Alert erreur = new Alert(AlertType.ERROR);
                erreur.setTitle("Erreur de niveau");
                erreur.setHeaderText("Impossible de charger le niveau suivant");
                erreur.setContentText(SE.getMessage());
                erreur.initOwner(fenetrePrincipale);
                actionNivPrecedent();
                erreur.showAndWait();
            }
        }
    }

    @FXML
    public void actionNivPrecedent() {
        if (this.jeu.getNiveauCourant() != 0) {
            try {
                this.jeu.niveauPrecedent();
                mettreAJourAffichage();
            } catch (SokobanException SE) {
                Alert erreur = new Alert(AlertType.ERROR);
                erreur.setTitle("Erreur de niveau");
                erreur.setHeaderText("Impossible de charger le niveau précédent");
                erreur.setContentText(SE.getMessage());
                erreur.initOwner(fenetrePrincipale);
                erreur.showAndWait();
            }
        }
    }

    @FXML
    public void actionRecommencer() {
        this.jeu.reset();
        mettreAJourAffichage();
    }

    @FXML
    public void actionQuitter() {

        Alert close = new Alert(AlertType.WARNING);
        close.setTitle("Fermeture...");
        close.setHeaderText("Voulez vous fermer ? ");
        close.initOwner(fenetrePrincipale);
        close.initModality(Modality.WINDOW_MODAL);

        ButtonType plustard = new ButtonType("Plus tard...");
        close.getButtonTypes().setAll(plustard, ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> reponse = close.showAndWait();

        if (reponse.orElse(null) == ButtonType.YES) {
            this.fenetrePrincipale.close();
        }
    }

    public void majJoueur(Direction direction, ResultatMouvement resMouv) {
        Image nouvelleImage = null;

        if (resMouv == ResultatMouvement.DEPLACE) {
            nouvelleImage = deplacement.get(direction);
        } else if (resMouv == ResultatMouvement.POUSSE || resMouv == ResultatMouvement.BLOQUE) {
            nouvelleImage = poussee.get(direction);
        }
        if (nouvelleImage != null) {
            imagJoueur.setImage(nouvelleImage);
        }
    }

    public void actionUndo() {
        try {
            jeu.annuler();
            mettreAJourAffichage();
        } catch (SokobanException SE) {
            alertUndo();
        }

    }

    @FXML
    public void actionCharger() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Sélectionnez le dossier contenant les niveaux (.xsb)");

        // Ouvre la fenêtre de dialogue pour sélectionner un dossier
        File dossierSelectionne = directoryChooser.showDialog(this.fenetrePrincipale);

        if (dossierSelectionne != null) {
            try {
                Path dossierCible = dossierSelectionne.toPath();

                Banque banqNiv = LoaderNiveauxXSB.chargerDepuisDossier(dossierCible);

                jeu.setBanqueNiveaux(banqNiv.niveaux, banqNiv.noms);
                nomsNiveaux = banqNiv.noms;
                this.choixNiveaux.getItems().clear();
                setMenu();
                mettreAJourAffichage();
                if (this.fenetrePrincipale != null) {
                    this.fenetrePrincipale.sizeToScene();
                }

            } catch (SokobanException SE) {
                Alert erreur = new Alert(AlertType.ERROR);
                erreur.setTitle("Erreur de chargement");
                erreur.setHeaderText("Impossible de charger les niveaux");
                erreur.setContentText(SE.getMessage());
                erreur.initOwner(fenetrePrincipale);
                erreur.showAndWait();
            }

        }
    }

    public void alertUndo() {
        Alert undo = new Alert(AlertType.WARNING);
        undo.setTitle("Plus rien à annuler...");
        undo.setHeaderText("Plus rien à annuler");
        undo.initOwner(fenetrePrincipale);
        undo.initModality(Modality.WINDOW_MODAL);

        ButtonType ok = new ButtonType("je me calme et je respire");
        undo.getButtonTypes().setAll(ok);

        undo.showAndWait();
    }

    @FXML
    public void actionAPropos() {
        Alert APropos = new Alert(AlertType.INFORMATION);
        APropos.setTitle("Crédits");
        APropos.setHeaderText("Fait par : Erhan BAYRAKCEKEN\nDernière mise à jour le : 22/05/2026");
        APropos.initOwner(fenetrePrincipale);
        APropos.initModality(Modality.WINDOW_MODAL);

        ButtonType ok = new ButtonType("Ok");
        APropos.getButtonTypes().setAll(ok);

        APropos.showAndWait();
    }

    @FXML
    private void actionAutoProgOnOff() {
        autoProgression = !autoProgression;
        if (autoProgression) {
            setAutoProgression.setText("Auto Progression (ON)");
        } else {
            setAutoProgression.setText("Auto Progression (OFF)");
        }
    }

    @FXML
    public void actionAide() {
        Alert aide = new Alert(AlertType.INFORMATION);
        aide.setTitle("Aide - Comment jouer ?");
        aide.setHeaderText("Règles du jeu Sokoban");
        aide.setContentText("Le but du jeu est de pousser toutes les caisses sur les cibles bleues.\n" +
                "Attention : vous ne pouvez pousser qu'une seule caisse à la fois et vous ne pouvez pas les tirer !\n\n"
                +
                "Commandes au clavier :\n" +
                "• Flèches directionnelles ou Z, Q, S, D : Déplacer le joueur\n" +
                "• Ctrl + Z : Annuler le dernier mouvement\n\n" +
                "L'Auto Progression (dans le menu) permet de passer automatiquement au niveau suivant une fois terminé (sans confirmation).");
        aide.initOwner(fenetrePrincipale);
        aide.initModality(Modality.WINDOW_MODAL);

        ButtonType ok = new ButtonType("Compris !");
        aide.getButtonTypes().setAll(ok);

        aide.showAndWait();
    }

    public void setMenu() {
        for (int i = 0; i < this.nomsNiveaux.length; i++) {
            final int indexNiveau = i;
            MenuItem tempItem = new MenuItem("Niveau " + i + " : " + this.nomsNiveaux[i]);
            tempItem.setOnAction(event -> {
                try {
                    jeu.chargerNiveauParIndex(indexNiveau);
                    mettreAJourAffichage();
                } catch (SokobanException SE) {
                    Alert erreur = new Alert(AlertType.ERROR);
                    erreur.setTitle("Erreur de chargement");
                    erreur.setHeaderText("Impossible de charger le niveaux");
                    erreur.setContentText(SE.getMessage());
                    erreur.initOwner(fenetrePrincipale);
                    erreur.showAndWait();
                }
            });
            this.choixNiveaux.getItems().add(tempItem);
        }
    }
}
