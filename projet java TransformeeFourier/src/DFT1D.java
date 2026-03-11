public class DFT1D {

    
    public static complexe[] DFT1D(complexe[] entree) {
        int N = entree.length;
        complexe[] sortie = new complexe[N];

        for (int k = 0; k < N; k++) {
            double sommeRe = 0;
            double sommeIm = 0;

            for (int n = 0; n < N; n++) {
                double angle = -2 * Math.PI * k * n / N;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);

                // (a + ib) * (cos + i sin) = (a cos - b sin) + i(a sin + b cos)
                double re = entree[n].re * cos - entree[n].im * sin;
                double im = entree[n].re * sin + entree[n].im * cos;

                sommeRe += re;
                sommeIm += im;
            }

            sortie[k] = new complexe(sommeRe, sommeIm);
        }

        return sortie;
    }

}
