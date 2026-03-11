public class MainFourier {

    // Afficher un vecteur 1D de complexes
    private static void afficherVecteur(String titre, complexe[] v) {
        System.out.println("=== " + titre + " ===");
        for (complexe c : v) {
            System.out.println(c);
        }
        System.out.println();
    }

    // Afficher une matrice 2D de complexes
    private static void afficherMatrice(String titre, complexe[][] m) {
        System.out.println("=== " + titre + " ===");
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {


        // Signal 1D complexe (pas que des réels)
        complexe[] signal1D = new complexe[] {
            new complexe(1, 0),    // 1 + 0i
            new complexe(0, 1),    // 0 + i
            new complexe(-1, 2),   // -1 + 2i
            new complexe(2, -1)    // 2 - i
        };

        afficherVecteur("Signal 1D d'origine", signal1D);

       
        complexe[] dft1D = DFT1D.DFT1D(signal1D);
        afficherVecteur("DFT1D(signal1D)", dft1D);

        complexe[] idft1D = IDFT1D.IDFT1D(dft1D);
        afficherVecteur("IDFT1D(DFT1D(signal1D))", idft1D);

        
        
        complexe[] fft1D = FFT.FFT(signal1D);
        afficherVecteur("FFT(signal1D)", fft1D);

        complexe[] ifft1D = IFFT1D.IFFT1D(fft1D);
        afficherVecteur("IFFT1D(FFT(signal1D))", ifft1D);



        // Matrice 2D de complexes 2x2 (plus simple à lire à l'écran)
        complexe[][] matrice2D = new complexe[][] {
            { new complexe(1, 0),   new complexe(0, 1)   },
            { new complexe(-1, 2),  new complexe(2, -1)  }
        };

        afficherMatrice("Matrice 2D d'origine", matrice2D);

      
        complexe[][] dft2D = DFT2D.DFT2D(matrice2D);
        afficherMatrice("DFT2D(matrice2D)", dft2D);

        complexe[][] idft2D = IDFT2D.IDFT2D(dft2D);
        afficherMatrice("IDFT2D(DFT2D(matrice2D))", idft2D);

        complexe[][] fft2D = FFT2D.FFT2D(matrice2D);
        afficherMatrice("FFT2D(matrice2D)", fft2D);

        complexe[][] ifft2D = IFFT2D.IFFT2D(fft2D);
        afficherMatrice("IFFT2D(FFT2D(matrice2D))", ifft2D);
    }
}

