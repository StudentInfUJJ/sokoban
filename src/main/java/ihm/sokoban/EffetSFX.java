package ihm.sokoban;

import java.net.URL;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;

public enum EffetSFX {
    SLIDE("BoisSlide.mp3"),
    BLOQUE("Bloque.mp3"),
    VICTOIRE("Win.mp3"),
    VICTOIRE_FIN("Win_Long.mp3"),
    DEFAITE("Lose.mp3"),
    ALERT("Alert.mp3"),
    CAISSE_SUR_CIBLE("CaisseSurCibleSound.mp3");

    private final String libelle;
    private AudioClip clip;

    EffetSFX(String libelle) {
        this.libelle = libelle;
        try {
            URL resource = getClass().getResource("/ihm/sokoban/audio/" + libelle);
            if (resource != null) {
                this.clip = new AudioClip(resource.toExternalForm());
                // ON stocke les sons pour ne pas les recharger à chaque fois afin de ne pas
                // avoir de latence
            }

        } catch (Exception SFX) {
            Alert erreurSFX = new Alert(AlertType.WARNING);
            erreurSFX.setTitle("Erreur SFX...");
            erreurSFX.setHeaderText(
                    "La musique n'a pas pu chargé : \nCauses potentielles : \n-La musique n'existe pas \n-La musique a été corrompue");
            erreurSFX.initModality(Modality.WINDOW_MODAL);

            ButtonType ok = new ButtonType("Ok");
            erreurSFX.getButtonTypes().setAll(ok);

            erreurSFX.showAndWait();
        }
    }

    public String getLibelle() {
        return this.libelle;
    }

    public void play() {
        if (clip != null) {
            clip.play();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
