package projetinfo2a;
public class ScannerUnidirectionnel extends Outil
{   
    public ScannerUnidirectionnel()
    {
        super
        (
                "SU",
                "ScannerUnidirectionnel",
                "Détecte à travers les murs la distance à laquelle se situe le premier objet (à 20% près)",
                2//coût énergétique de l'utilisation du scanner unidirectionnel
        );
    }    

    @Override
    public void activation(Joueur j){
        int compteur = 0;
        String s="";
        do{
            System.out.println("Dans quelle direction voulez-vous regarder ? (haut,bas,gauche,droite)");
            s=Lire.S();
        } while (!"haut".equals(s) && !"bas".equals(s) && !"gauche".equals(s) && !"droite".equals(s));

        int x = j.getXJoueur();
        int y = j.getYJoueur();
        boolean stop = false;

        if("haut".equals(s)){
            
            for(int i=x;i>=0 && stop==false;i--){
                if(!"Vide".equals(j.getPlateau().getCase(i, y).lookContent())){
                    stop=true;
                    compteur = compteur +1;
                }
                else{
                    compteur = compteur + 1;
                }
            }
            double a = compteur * 0.20;
            double nb20moins = compteur - a;
            double nb20plus = compteur + a;
            int totalCase = (int) (Math.random() * (nb20plus - nb20moins + 1) + nb20moins);
            System.out.println("Le premier objet en haut est à environ "+totalCase+" case(s).");
        }

        if("bas".equals(s)){
            
            for(int i=x;i<j.getPlateau().getTailleX() && stop==false;i++){
                if(!"Vide".equals(j.getPlateau().getCase(i, y).lookContent())){
                    stop=true;
                    compteur = compteur + 1;
                }
                else{
                    compteur = compteur +1;
                }
            }
            double a = compteur * 0.20;
            double nb20moins = compteur - a;
            double nb20plus = compteur + a;
            int totalCase = (int) (Math.random() * (nb20plus - nb20moins + 1) + nb20moins);
            System.out.println("Le premier objet en bas est à environ "+totalCase+" case(s).");
            
        }

        if("gauche".equals(s)){
            
            for(int i=y;i>=0 && stop==false;i--){
                if(!"Vide".equals(j.getPlateau().getCase(x, i).lookContent())){
                    stop=true;
                    compteur = compteur + 1;
                }

                else{
                    compteur = compteur + 1;
                }
            }
            double a = compteur * 0.20;
            double nb20moins = compteur - a;
            double nb20plus = compteur + a;
            int totalCase = (int) (Math.random() * (nb20plus - nb20moins + 1) + nb20moins);
            System.out.println("Le premier objet à gauche est à environ "+totalCase+" case(s).");
        }

        if("droite".equals(s)){
            for(int i=y;i<j.getPlateau().getTailleY() && stop == false;i++){
                if(!"Vide".equals(j.getPlateau().getCase(x, i).lookContent())){
                    stop=true;
                    compteur = compteur + 1;
                }

                else{
                    compteur = compteur + 1;
                }
            }
            double a = compteur * 0.20;
            double nb20moins = compteur - a;
            double nb20plus = compteur + a;
            int totalCase = (int) (Math.random() * (nb20plus - nb20moins + 1) + nb20moins);
            System.out.println("Le premier objet à droite est à environ "+totalCase+" case(s).");
        }


  }
}
