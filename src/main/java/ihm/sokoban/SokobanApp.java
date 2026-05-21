package ihm.sokoban;

import java.io.IOException;
import java.util.Scanner;

import ihm.sokoban.model.Direction;
import ihm.sokoban.model.JeuSokoban;
import ihm.sokoban.model.ResultatMouvement;
import ihm.sokoban.model.SokobanException;
import ihm.sokoban.model.TypeCase;
import ihm.sokoban.util.*;
import ihm.sokoban.view.SokobanAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Application JavaFX principale pour le jeu Sokoban.
 */
public class SokobanApp extends Application {

    private BorderPane rootPane;
    private Stage primaryStage;

    private JeuSokoban jeu;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
            this.jeu = new JeuSokoban(NiveauxTutoriel.getNiveaux(), NiveauxTutoriel.getNiveaux(), 0);

            this.primaryStage = primaryStage;
            this.rootPane = new BorderPane();

            Scene scene = new Scene(rootPane);

            primaryStage.setTitle("Bientot un jeu de Sokoban");
            primaryStage.setScene(scene);

            loadSokobanJeu(scene);

            primaryStage.show();
            

    }

    private void loadSokobanJeu(Scene scene){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SokobanApp.class.getResource("view/SokobanJeu.fxml"));

            BorderPane vueJeu = loader.load();

            SokobanAppController ctrl = loader.getController();

            ctrl.setFenetrePrincipale(primaryStage);
            ctrl.setJeu(this.jeu);
            
            // ICi on gére la capture des touches 
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                switch (event.getCode()) {
                    case UP:
                    case Z:
                        ctrl.executerDeplacement(Direction.HAUT);
                        break;
                    case DOWN:
                    case S:
                        ctrl.executerDeplacement(Direction.BAS);
                        break;
                    case LEFT:
                    case Q:
                        ctrl.executerDeplacement(Direction.GAUCHE);
                        break;
                    case RIGHT:
                    case D:
                        ctrl.executerDeplacement(Direction.DROITE);
                        break;
                    default:
                        break;
                }
                event.consume();
            });

            this.rootPane.setCenter(vueJeu);
            ctrl.mettreAJourAffichage(); 

        } catch (IOException e){
            System.out.println("Ressource FXML non disponible : SokobanJeu.fxml");
            e.printStackTrace();
            System.exit(1);
        }
    }





    public static void main2(String[] args) {
        launch(args);
    }

}
