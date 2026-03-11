public class IDFT2D {
public static complexe[][] IDFT2D(complexe[][] entree) {
    int lignes = entree.length;
    int colonnes = entree[0].length;

    complexe[][] intermediaire = new complexe[lignes][colonnes];
    complexe[][] resultat = new complexe[lignes][colonnes];

    // Étape 1 : IDFT 1D sur chaque ligne
    for (int i = 0; i < lignes; i++) {
        intermediaire[i] = IDFT1D.IDFT1D(entree[i]);
    }

    // Étape 2 : IDFT 1D sur chaque colonne
    for (int j = 0; j < colonnes; j++) {
        complexe[] colonne = new complexe[lignes];
        for (int i = 0; i < lignes; i++) {
            colonne[i] = intermediaire[i][j];
        }

        complexe[] colonneTransformee = IDFT1D.IDFT1D(colonne);

        for (int i = 0; i < lignes; i++) {
            resultat[i][j] = colonneTransformee[i];
        }
    }

    return resultat;
}
}