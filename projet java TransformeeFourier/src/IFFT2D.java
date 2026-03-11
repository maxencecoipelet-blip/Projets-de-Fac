public class IFFT2D {
public static complexe[][] IFFT2D(complexe[][] entree) {
    int lignes = entree.length;
    int colonnes = entree[0].length;

    complexe[][] intermediaire = new complexe[lignes][colonnes];
    complexe[][] resultat = new complexe[lignes][colonnes];

    // Étape 1 : IFFT sur chaque ligne
    for (int i = 0; i < lignes; i++) {
        intermediaire[i] = IFFT1D.IFFT1D(entree[i]);
    }

    // Étape 2 : IFFT sur chaque colonne
    for (int j = 0; j < colonnes; j++) {
        complexe[] colonne = new complexe[lignes];
        for (int i = 0; i < lignes; i++) {
            colonne[i] = intermediaire[i][j];
        }

        colonne = IFFT1D.IFFT1D(colonne);

        for (int i = 0; i < lignes; i++) {
            resultat[i][j] = colonne[i];
        }
    }

    return resultat;
}
}