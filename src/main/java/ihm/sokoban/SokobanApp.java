package ihm.sokoban;

import java.io.IOException;

import ihm.sokoban.model.Direction;
import ihm.sokoban.model.SokobanException;
import ihm.sokoban.view.SokobanAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Application JavaFX principale pour le jeu Sokoban.
 */
public class SokobanApp extends Application {

    private final KeyCombination ctrlz = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
    private BorderPane rootPane;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        this.rootPane = new BorderPane();

        Scene scene = new Scene(rootPane);

        primaryStage.setTitle("Bientot un jeu de Sokoban");
        primaryStage.setScene(scene);

        loadSokobanJeu(scene);

        primaryStage.show();

    }

    private void loadSokobanJeu(Scene scene) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SokobanApp.class.getResource("view/SokobanJeu.fxml"));

            BorderPane vueJeu = loader.load();

            SokobanAppController ctrl = loader.getController();

            ctrl.setFenetrePrincipale(primaryStage);
            ctrl.mainMusicStart();
            ctrl.setMenu();

            // ICi on gére la capture des touches (combinaison (ctrl+Z) et normaux)

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (ctrlz.match(event)) {
                    try {
                        ctrl.actionUndo();
                    } catch (SokobanException SE) {
                        ctrl.alertUndo();
                    }
                    event.consume();
                } else {
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
                        case N:
                            ctrl.actionNivSuivant();
                            break;
                        case P:
                            ctrl.actionNivPrecedent();
                            break;
                        case R:
                            ctrl.actionRecommencer();
                            break;
                        case X:
                            ctrl.actionQuitter();
                            break;
                        case C:
                            ctrl.actionCharger();
                        case U:
                            ctrl.actionUndo();
                        default:
                            break;
                    }
                    event.consume();
                }
            });

            this.rootPane.setCenter(vueJeu);
            ctrl.mettreAJourAffichage();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main2(String[] args) {
        launch(args);
    }

}
