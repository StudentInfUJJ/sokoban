package ihm.sokoban.view;

import java.net.URL;
import java.nio.file.Path;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import ihm.sokoban.EffetSFX;
import ihm.sokoban.model.Direction;
import ihm.sokoban.model.JeuSokoban;
import ihm.sokoban.model.ResultatMouvement;
import ihm.sokoban.model.SokobanException;
import ihm.sokoban.model.TypeCase;
import ihm.sokoban.util.LoaderNiveauxXSB;
import ihm.sokoban.util.LoaderNiveauxXSB.Banque;
import ihm.sokoban.util.NiveauxSokoban;
import ihm.sokoban.util.NiveauxTutoriel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;

public class SokobanAppController implements Initializable {

    // === Fenêtre principale et Modèle ===
    private Stage fenetrePrincipale;
    // Initialisation du jeu (par défaut commence par Tutoriel)
    public static JeuSokoban jeu = new JeuSokoban(NiveauxTutoriel.getNiveaux(), NiveauxTutoriel.getNoms(), 0);

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
    private MediaPlayer mediaMainMusic;
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

    /**
     * Fait le lien avec la fenêtre de l'application
     * 
     * @param fenetrePrincipale
     */

    public void setFenetrePrincipale(Stage fenetrePrincipale) {
        this.fenetrePrincipale = fenetrePrincipale;
        this.fenetrePrincipale.setOnCloseRequest(event -> {
            actionQuitter();
            event.consume();
        });

    }

    /**
     * Charge les niveaux normaux de sokoban
     */
    @FXML
    public void setJeuSokoban() {
        jeu = new JeuSokoban(0);
        this.nomsNiveaux = NiveauxSokoban.getNoms();
        this.choixNiveaux.getItems().clear();
        setMenu();
        setSprites();

        mettreAJourAffichage();
    }

    /**
     * Charge les niveaux Tutoriels
     */
    @FXML
    public void setJeuTutos() {
        jeu = new JeuSokoban(NiveauxTutoriel.getNiveaux(), NiveauxTutoriel.getNoms(), 0);
        this.nomsNiveaux = NiveauxTutoriel.getNoms();
        this.choixNiveaux.getItems().clear();
        setMenu();
        setSprites();
        parDefautJoueur();
        mettreAJourAffichage();
    }

    /**
     * Initialise toutes les images de tous les mouvements dans des HashMap (clé =
     * mouvement, valeur = image)
     */
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

    /**
     * Cette fonction s'occupe de l'affichage du jeu. Utilisé à chaque fois qu'il
     * y'a un changement dans le jeu. Exemple : j'appuie sur une fléche de
     * mouvement, le moteur du jeu JeuSokoban change en arrière plan et cette
     * fonction permet d'afficher ses changements
     */
    public void mettreAJourAffichage() {
        this.imagJoueur.setFitHeight(40);
        this.imagJoueur.setFitWidth(40);

        scoreboard();
        this.fenetrePrincipale.setTitle(nomsNiveaux[jeu.getNiveauCourant()]);

        if (jeu == null)
            return; // Sécurité

        if (jeu.estDernierNiveau()) {
            this.nivSuivant.setDisable(true);
        } else {
            this.nivSuivant.setDisable(false);
        }
        if (jeu.getNiveauCourant() == 0) {
            this.nivPrecedent.setDisable(true);
        } else {
            this.nivPrecedent.setDisable(false);
        }

        this.zoneJeu.getChildren().clear();
        this.zoneJeu.getRowConstraints().clear();
        this.zoneJeu.getColumnConstraints().clear();
        TypeCase[][] tabJeu = jeu.getGrille(); // A chaque fois on met à jour le jeu
        int nbLigne = jeu.getNbLignes();
        int nbColonnes = jeu.getNbColonnes(); // On récupère le vrai nombre de colonnes

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

    /**
     * Met la position par défaut du joueur à chaque fois que le niveau change
     */
    private void parDefautJoueur() {
        this.imagJoueur = new ImageView(new Image(getClass().getResourceAsStream("/ihm/sokoban/images/JoueurBas.png")));
    }

    /**
     * Determine le statut du jeu et en fonction determine l'action à faire : aller
     * au niveau 0(cas du dernier niveau ), aller au niveau suivant (cas normal) et
     * recommencer (en cas d'échec)
     * 
     */
    private void statuJeu() {
        if (jeu.isNiveauTermine() && !jeu.estDernierNiveau()) {
            joueSFX(EffetSFX.VICTOIRE);
            finNivSimple();

        } else if (jeu.isNiveauTermine() && jeu.estDernierNiveau()) {
            joueSFX(EffetSFX.VICTOIRE_FIN);
            finNivDernier();

        } else if (jeu.isPerdu()) {
            joueSFX(EffetSFX.DEFAITE);
            loseNiv();

        }

    }

    /**
     * Affiche le scoreboard du jeu : nombre de mouvement, nombre de poussées,
     * niveau actuel, caisses restantes
     */
    private void scoreboard() {
        nbMouvements = new SimpleIntegerProperty(jeu.getNbMouvements());
        nbPoussees = new SimpleIntegerProperty(jeu.getNbPoussees());
        this.mouvements.textProperty().bind(nbMouvements.asString());
        this.poussees.textProperty().bind(nbPoussees.asString());

        this.niveau
                .setText((String.valueOf(jeu.getNiveauCourant() + 1)) + " / " + jeu.getNbNiveaux());
        this.blocsOk.setText(String.valueOf(jeu.getNbCaissesSurCible()) + " / " + jeu.getNbCaisses());
    }

    /**
     * Actionné quand le niveau est le dernier du jeu
     * Affiche un pop up demandant l'affirmation de quitter ou de continuer
     */
    private void finNivDernier() {
        Alert finNivDernier = new Alert(AlertType.CONFIRMATION);
        ButtonType recommencer, recommencerTotal;
        recommencer = null;
        recommencerTotal = null;
        Optional<ButtonType> reponse = null;
        recommencer = new ButtonType("recommencer ce niveau");
        recommencerTotal = new ButtonType("Aller à niveau 0");
        finNivDernier.setTitle("Dernier niveau terminé...");
        finNivDernier.setHeaderText("Félicitations vous venez de terminer le dernier niveau !!!");
        finNivDernier.initOwner(this.fenetrePrincipale);
        finNivDernier.initModality(Modality.WINDOW_MODAL);

        if (!autoProgression) {

            finNivDernier.getButtonTypes().setAll(recommencer, recommencerTotal);

            mainMusicStop();

            reponse = finNivDernier.showAndWait();

            if (reponse.orElse(null) == recommencerTotal) {
                stopSFX(EffetSFX.VICTOIRE_FIN);
                mainMusicStop();
                mainMusicStart();
                jeu.chargerNiveauParIndex(0);
                mettreAJourAffichage();
            }

            if (reponse.orElse(null) == recommencer) {
                stopSFX(EffetSFX.VICTOIRE_FIN);
                actionRecommencer();
                mainMusicStop();
                mainMusicStart();

            }

        } else {
            jeu.chargerNiveauParIndex(0);
            mettreAJourAffichage();
            mediaMainMusic.stop();
            mediaMainMusic.play();
        }

    }

    /**
     * Actionné quand le niveau n'est pas le dernier niveau
     * Affiche un pop up qui demande si le joueur veut contineur à jouer ou
     * recommencer le niveau
     */
    private void finNivSimple() {

        // Pas dernier niveau sans autoProgression (normal)
        Alert finNivSimple = new Alert(AlertType.CONFIRMATION);
        ButtonType recommencer = new ButtonType("Recommencer");
        ButtonType niveauSuivant = new ButtonType("Niveau suivant");
        Optional<ButtonType> reponse = null;
        finNivSimple.setTitle("Niveau terminé...");
        finNivSimple.setHeaderText("Niveau terminé !\nPasser au prochain niveau ?");
        finNivSimple.initOwner(this.fenetrePrincipale);
        finNivSimple.initModality(Modality.WINDOW_MODAL);

        finNivSimple.getButtonTypes().setAll(niveauSuivant, recommencer);

        if (!autoProgression) {
            finNivSimple.getButtonTypes().setAll(recommencer, niveauSuivant);

            reponse = finNivSimple.showAndWait();

            if (reponse.orElse(null) == niveauSuivant) {
                actionNivSuivant();
            }
            if (reponse.orElse(null) == recommencer) {
                actionRecommencer();
            }
        } else {
            actionNivSuivant();
        }

    }

    /**
     * Actionné quand le joueur perd le niveau
     * Affiche un pop up qui demande si le joueur veut recommencer le niveau
     */
    private void loseNiv() {
        Alert loseNiv = new Alert(AlertType.INFORMATION);

        loseNiv.setTitle("Game Over");
        loseNiv.setHeaderText("GAME OVER");
        loseNiv.initOwner(this.fenetrePrincipale);
        loseNiv.initModality(Modality.WINDOW_MODAL);
        ButtonType recommencer = new ButtonType("Recommencer");
        ButtonType annuler = new ButtonType("Annuler");

        loseNiv.getButtonTypes().setAll(recommencer, annuler);

        mainMusicStop();
        Optional<ButtonType> reponse = loseNiv.showAndWait();
        mainMusicStart();

        if (reponse.orElse(null) == recommencer) {
            actionRecommencer();
        }

    }

    /**
     * En fonction de la saisie clavier on déduit la direction et en fonction
     * de cette dernière on deplace le joueur et obtient le résultat du mouvement
     * 
     * @param direction
     */
    public void executerDeplacement(Direction direction) {
        if (jeu.peutJouer()) {
            int caisseAvant = jeu.getNbCaissesSurCible();
            ResultatMouvement r = jeu.deplacer(direction); // actionne le mouvement
            directionJoueur = direction;
            int caisseApres = jeu.getNbCaissesSurCible();
            switch (r) {
                case DEPLACE:
                    majJoueur(direction, r);
                    break;// cas normal
                case POUSSE:
                    majJoueur(direction, r);
                    if (!jeu.isNiveauTermine() && caisseApres > caisseAvant) { // Quand le nombre de caisse sur cible
                                                                               // augmente on joue le son
                                                                               // CaisseSurCibleSound.mp3
                        joueSFX(EffetSFX.CAISSE_SUR_CIBLE);
                    } else {
                        joueSFX(EffetSFX.SLIDE);
                    }
                    break; // Cas pousser
                case BLOQUE:
                    majJoueur(direction, r);
                    joueSFX(EffetSFX.BLOQUE);
                    break; // Mouvement impossible
                case NIVEAU_TERMINE:
                    break;
                default:
                    break;
            }
            mettreAJourAffichage();
        }
    }

    /**
     * Affiche un pop up qui propose de passer au niveau suivant si celui ci n'est
     * pas le dernier
     * 
     */
    @FXML
    public void actionNivSuivant() {
        if (!jeu.estDernierNiveau()) {
            try {
                jeu.niveauSuivant();
                parDefautJoueur();// Sinon le joueur reste dans la position du jeu précedent
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

    /**
     * Affiche un pop up qui propose de passer au niveau précedent si celui ci n'est
     * pas le premier
     */
    @FXML
    public void actionNivPrecedent() {
        if (jeu.getNiveauCourant() != 0) {
            try {
                jeu.niveauPrecedent();
                parDefautJoueur();
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

    /**
     * Fait recommencer le niveau courant
     */
    @FXML
    public void actionRecommencer() {
        jeu.reset();
        mainMusicStop();
        mainMusicStart();
        parDefautJoueur();
        mettreAJourAffichage();
    }

    /**
     * Affiche un pop up qui demande confirmation avant de fermer
     */
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

    /**
     * En fonction de la direction et du résultat du mouvement on change le sprite
     * du joueur
     * 
     * @param direction
     * @param resMouv
     */
    private void majJoueur(Direction direction, ResultatMouvement resMouv) {
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

    /**
     * Permet d'annuler une action
     */
    public void actionUndo() {
        try {
            jeu.annuler();
            mettreAJourAffichage();
        } catch (SokobanException SE) {
            alertUndo();
        }

    }

    /**
     * permet de charger des niveaux externes à partir d'un chemin de répertoire
     * Soulève des exceptions en cas d'erreur
     */
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

    /**
     * Actionné par actionUndo qui affiche un pop up qui indique qu'il ne reste plus
     * aucun mouvement à annuler
     * 
     */
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

    /**
     * Affiche un pop up de crédits
     */
    @FXML
    private void actionAPropos() {
        Alert APropos = new Alert(AlertType.INFORMATION);
        APropos.setTitle("Crédits");
        APropos.setHeaderText("Fait par : Erhan BAYRAKCEKEN\nDernière mise à jour le : 22/05/2026");
        APropos.initOwner(fenetrePrincipale);
        APropos.initModality(Modality.WINDOW_MODAL);

        ButtonType ok = new ButtonType("Ok");
        APropos.getButtonTypes().setAll(ok);

        APropos.showAndWait();
    }

    /**
     * Bascule le mode auto progression et change le texte du label entre ON/OFF
     */
    @FXML
    private void actionAutoProgOnOff() {
        autoProgression = !autoProgression;
        if (autoProgression) {
            setAutoProgression.setText("Auto Progression (ON)");
        } else {
            setAutoProgression.setText("Auto Progression (OFF)");
        }
    }

    /**
     * Affiche un pop up d'aide qui affiche au joueur une explicatino sur le jeu et
     * les touches du jeu
     */
    @FXML
    private void actionAide() {
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

    /**
     * Met à jour les niveaux du menu déroulant
     */
    public void setMenu() {
        for (int i = 0; i < this.nomsNiveaux.length; i++) {
            final int indexNiveau = i; // On stocke l'index qui correspond au niveau du jeu
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
            this.choixNiveaux.getItems().add(tempItem); // On ajoute les niveaux dans le menu déroulant qui contiennent
                                                        // chacun un listener
        }
    }

    /**
     * Fait commencer l'effet souhaité, aidé par l'IA pour l'optimisation
     * 
     * @param effet
     */
    public void joueSFX(EffetSFX effet) {
        try {
            effet.play();

        } catch (Exception SFX) {
            Alert erreurSFX = new Alert(AlertType.WARNING);
            erreurSFX.setTitle("Erreur SFX...");
            erreurSFX.setHeaderText(SFX.getMessage());
            erreurSFX.initOwner(fenetrePrincipale);
            erreurSFX.initModality(Modality.WINDOW_MODAL);

            ButtonType ok = new ButtonType("Ok");
            erreurSFX.getButtonTypes().setAll(ok);

            erreurSFX.showAndWait();
        }

    }

    /**
     * Arrête l'effet souhaité
     */
    public void stopSFX(EffetSFX effet) {
        try {
            effet.stop();
        } catch (Exception SFX) {
            Alert erreurSFX = new Alert(AlertType.WARNING);
            erreurSFX.setTitle("Erreur SFX...");
            erreurSFX.setHeaderText(SFX.getMessage());
            erreurSFX.initOwner(fenetrePrincipale);
            erreurSFX.initModality(Modality.WINDOW_MODAL);

            ButtonType ok = new ButtonType("Ok");
            erreurSFX.getButtonTypes().setAll(ok);

            erreurSFX.showAndWait();
        }
    }

    /**
     * Fait commencer la musique principale
     */
    public void mainMusicStart() {
        try {
            URL resource = getClass().getResource("/ihm/sokoban/audio/MusiqueGenIA.mp3");

            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                mediaMainMusic = new MediaPlayer(media);
                mediaMainMusic.setVolume(0.4);
                mediaMainMusic.setCycleCount(MediaPlayer.INDEFINITE);
                mediaMainMusic.play();
            } else {
                Alert erreurMusique = new Alert(AlertType.WARNING);
                erreurMusique.setTitle("Erreur SFX...");
                erreurMusique.setHeaderText(
                        "La musique n'a pas pu charger : \nCauses potentielles : \n-La musique n'existe pas \n-La musique a été corrompue");
                erreurMusique.initOwner(fenetrePrincipale);
                erreurMusique.initModality(Modality.WINDOW_MODAL);

                ButtonType ok = new ButtonType("Jouer quand même");
                erreurMusique.getButtonTypes().setAll(ok);

                erreurMusique.showAndWait();
            }
        } catch (Exception e) {
            Alert erreurMusique = new Alert(AlertType.WARNING);
            erreurMusique.setTitle("Erreur SFX...");
            erreurMusique.setHeaderText(e.getMessage());
            erreurMusique.initOwner(fenetrePrincipale);
            erreurMusique.initModality(Modality.WINDOW_MODAL);

            ButtonType ok = new ButtonType("Ok");
            erreurMusique.getButtonTypes().setAll(ok);

            erreurMusique.showAndWait();
        }
    }

    /**
     * Arrête la musique principale
     */
    public void mainMusicStop() {
        mediaMainMusic.stop();
    }
}
