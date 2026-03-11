public class FFT {

    
    public static complexe[] FFT(complexe[] entree) {
        int N = entree.length;

       
        if (N == 1) return new complexe[] { entree[0] };

        
        if (N % 2 != 0) {
            System.out.println("La taille du tableau doit être une puissance de 2, hors ici N est impair");
        }

        if ((N & (N - 1)) != 0) { 
            throw new IllegalArgumentException("N doit être une puissance de 2 (2, 4, 8, 16, ...).");
        }


        // Séparer les indices pairs et impairs
        complexe[] pairs = new complexe[N / 2];
        complexe[] impairs = new complexe[N / 2];

        for (int i = 0; i < N / 2; i++) {
            pairs[i] = entree[2 * i];
            impairs[i] = entree[2 * i + 1];
        }

        // Appels récursifs
        complexe[] transformeePairs = FFT(pairs);
        complexe[] transformeeImpairs = FFT(impairs);

        // Combinaison des résultats
        complexe[] resultat = new complexe[N];
        for (int k = 0; k < N / 2; k++) {
            double angle = -2 * Math.PI * k / N;
            complexe facteur = new complexe(Math.cos(angle), Math.sin(angle));
            complexe t = facteur.mul(transformeeImpairs[k]);

            resultat[k] = transformeePairs[k].ajouter(t);
            resultat[k + N / 2] = transformeePairs[k].soustraire(t);
        }

        return resultat;
    }
}
