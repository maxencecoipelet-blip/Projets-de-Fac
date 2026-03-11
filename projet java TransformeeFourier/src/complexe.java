public class complexe {
    public final double re;  
    public final double im;  

    public complexe(double reelle, double imaginaire) {
        this.re = reelle;
        this.im = imaginaire;
    }

    public complexe ajouter(complexe b) {
        return new complexe(this.re + b.re, this.im + b.im);
    }

    public complexe soustraire(complexe b) {
        return new complexe(this.re - b.re, this.im - b.im);
    }

    public complexe mul(complexe b) {
        return new complexe(
            this.re * b.re - this.im * b.im,
            this.re * b.im + this.im * b.re
        );
    }

    public complexe scalaire(double a) {
        return new complexe(a * re, a * im);
    }

    public complexe conjugue() {
        return new complexe(re, -im);
    }

    @Override
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        return re + (im < 0 ? " - " : " + ") + Math.abs(im) + "i";
    }
}
