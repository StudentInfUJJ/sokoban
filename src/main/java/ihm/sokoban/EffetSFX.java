package ihm.sokoban;

public enum EffetSFX {
    SLIDE("BoisSlide.mp3"),
    BLOQUE("Bloque.mp3"),
    VICTOIRE("Win.mp3"),
    VICTOIRE_FIN("Win_Long.mp3"),
    DEFAITE("Lose.mp3"),
    ALERT("Alert.mp3"),
    MOVE("Move.mp3");

    private final String libelle;

    EffetSFX(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
