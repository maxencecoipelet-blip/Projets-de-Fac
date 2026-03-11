public class IDFT1D{
public static complexe[] IDFT1D(complexe[] entree) {
    int N = entree.length;
    complexe[] sortie = new complexe[N];

    for (int n = 0; n < N; n++) {
        double sommeRe = 0;
        double sommeIm = 0;

        for (int k = 0; k < N; k++) {
            double angle = 2 * Math.PI * k * n / N; // signe inversé
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            double re = entree[k].re * cos - entree[k].im * sin;
            double im = entree[k].re * sin + entree[k].im * cos;

            sommeRe += re;
            sommeIm += im;
        }

        sortie[n] = new complexe(sommeRe / N, sommeIm / N); 
    }

    return sortie;
}
}
