public class IFFT1D{
public static complexe[] IFFT1D(complexe[] entree) {
    int N = entree.length;

    // Conjuguer les entrées
    complexe[] conjugue = new complexe[N];
    for (int i = 0; i < N; i++) {
        conjugue[i] = entree[i].conjugue();
    }

    // Appliquer la FFT directe
    complexe[] transformee = FFT.FFT(conjugue);

    // Conjuguer à nouveau et diviser par N
    for (int i = 0; i < N; i++) {
        transformee[i] = transformee[i].conjugue().scalaire(1.0 / N);
    }

    return transformee;
}
}