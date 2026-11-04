package ca.cegep.biblio.model;

public enum LivreCategorie {
    ROMAN(0.25, 10),
    SCIENCE(0.50, 20),
    JEUNESSE(0.10, 5);

    private final double frais; 
    private final int max;

    LivreCategorie(double frais, int max) {
        this.frais = frais;
        this.max = max;
    }

    public double calculerFraisRetard(int joursRetard) {
        if (joursRetard < 0) { return 0; }
        // prend la valeur la plus basse, alors dépasse jamais le max
        return Math.min(joursRetard * frais, max);
    }
}