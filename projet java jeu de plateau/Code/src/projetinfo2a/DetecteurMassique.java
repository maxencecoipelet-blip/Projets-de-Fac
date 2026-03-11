package projetinfo2a;

public class DetecteurMassique extends Outil{

    public DetecteurMassique() {
        super(
                "DMA",
                "DetecteurMassique",
                "Permet de connaitre approximativement le nombre d'objets situés dans une direction donnée(à 10%)",
                2// coût énergétique de l'utilisation du détecteur massique
        );
    }

    @Override
    public void activation(Joueur j) {
        String s="";
        do{
            System.out.println("Dans quelle direction voulez-vous regardez ? (haut,bas,gauche,droite)");
            s=Lire.S();
        } while (!"haut".equals(s) && !"bas".equals(s) && !"gauche".equals(s) && !"droite".equals(s));
        int x = j.getXJoueur();
        int y = j.getYJoueur();
        int compteur = 0;
        if("droite".equals(s)){
           
            for(int i=x;i<j.getPlateau().getTailleX();i++){
                if(!"Vide".equals(j.getPlateau().getCase(i, y).lookContent()))
                    compteur = compteur + 1;
            }
            double a = compteur * 0.10;
            double nb10moins = compteur - a;
            double nb10plus = compteur + a;
            int nbObj = (int) (Math.random() * (nb10plus - nb10moins + 1) + nb10moins);
            System.out.println("Il y a environ "+nbObj+" objets dans cette direction");
        }

        if("gauche".equals(s)){
            for(int i=x;i>=0;i--){
                if(!"Vide".equals(j.getPlateau().getCase(i, y).lookContent()))
                    compteur = compteur + 1;
            }
            double a = compteur * 0.10;
            double nb10moins = compteur - a;
            double nb10plus = compteur + a;
            int nbObj = (int) (Math.random() * (nb10plus - nb10moins + 1) + nb10moins);
            System.out.println("Il y a environ "+nbObj+" objet(s) dans cette direction");
        }

        if("haut".equals(s)){
            for(int i=y;i>=0;i--){
                if(!"Vide".equals(j.getPlateau().getCase(x, i).lookContent()))
                    compteur = compteur + 1;
            }
            double a = compteur * 0.10;
            double nb10moins = compteur - a;
            double nb10plus = compteur + a;
            int nbObj = (int) (Math.random() * (nb10plus - nb10moins + 1) + nb10moins);
            System.out.println("Il y a environ "+nbObj+" objets dans cette direction");
        }

        if("bas".equals(s)){
            
            for(int i=y;i<j.getPlateau().getTailleY();i++){
                if(!"Vide".equals(j.getPlateau().getCase(x, i).lookContent()))
                    compteur = compteur + 1;
            }
            double a = compteur * 0.10;
            double nb10moins = compteur - a;
            double nb10plus = compteur + a;
            int nbObj = (int) (Math.random() * (nb10plus - nb10moins + 1) + nb10moins);
            System.out.println("Il y a environ "+nbObj+" objets dans cette direction");
        }
        
        
    }
}
