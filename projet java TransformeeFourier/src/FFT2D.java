public class FFT2D {

    
    public static complexe[][] FFT2D(complexe[][] entree) {
        int lignes = entree.length;
        int colonnes = entree[0].length;

        
        complexe[][] resultat = new complexe[lignes][colonnes];

        // Étape 1 : FFT 1D sur chaque ligne
        for (int i = 0; i < lignes; i++) {
            resultat[i] = FFT.FFT(entree[i]);
        }

        // Étape 2 : FFT 1D sur chaque colonne
        for (int j = 0; j < colonnes; j++) {
            complexe[] colonne = new complexe[lignes];
            for (int i = 0; i < lignes; i++) {
                colonne[i] = resultat[i][j];
            }

            colonne = FFT.FFT(colonne); // FFT 1D sur la colonne

            for (int i = 0; i < lignes; i++) {
                resultat[i][j] = colonne[i];
            }
        }

        return resultat;
    }

}
