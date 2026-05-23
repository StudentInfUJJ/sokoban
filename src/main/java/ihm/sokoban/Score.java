package ihm.sokoban;

public class Score {

    // Numéro + Nom de la partie
    private String NomPartie;

    private int Mouvements;

    private int NbPoussees;

    private int CaissesSurCible;

    private int NbCaisses;

    public Score(String pfNomPartie, int pfMouvements, int pfNbPoussees, int pfCaissesSurCible, int pfNbCaisses) {
        this.NomPartie = pfNomPartie;
        this.Mouvements = pfMouvements;
        this.NbPoussees = pfNbPoussees;
        this.CaissesSurCible = pfCaissesSurCible;
        this.NbCaisses = pfNbCaisses;

    }

    @Override
    public String toString() {
        return "===============================\nNiveau " + this.NomPartie + "\nNombre de mouvements : "
                + this.Mouvements + "\nNombre de poussées : " + this.NbPoussees + "\nNombre de caisses sur cible : "
                + this.CaissesSurCible + "/" + this.NbCaisses;
    }

}
