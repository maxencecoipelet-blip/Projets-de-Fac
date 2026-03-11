package projetinfo2a;

public class Excavatrice extends Outil {

    public Excavatrice() {
        super(
                "EXC",
                "Excavatrice",
                "Permet de creuser un mur d'une case sans faire exploser la mine qu'elle pourait contenir",
                8// coût énergétique de l'utilisation de l'excavatrice
        );
    }

    @Override
    public void activation(Joueur j) {
        String s="";
        int x = j.getXJoueur();
        int y  = j.getYJoueur();
        do{ 
            System.out.println("Quel mûr voulez-vous creuser ? (haut,bas,gauche,droite)");
            s=Lire.S();
            } while (!"haut".equals(s) && !"bas".equals(s) && !"gauche".equals(s) && !"droite".equals(s));

            if("droite".equals(s)){
                if(j.getPlateau().getCase(x,y).isAccessible(1)){
                    System.out.println("L'accès est déjà possible à droite");
                }
                else{
                    j.getPlateau().getCase(x,y).setAccessible(1);
                    System.out.println("Le mur de droite de votre case a été détruit et l'accès mis en place");
                    j.getPlateau().getCase(x,y+1).setVisible(true);
                }
            }
        
            if("gauche".equals(s)){
                if(j.getPlateau().getCase(x,y).isAccessible(2)){
                    System.out.println("L'accès est déjà possible à gauche");
                }
                else{
                    j.getPlateau().getCase(x,y).setAccessible(2);
                    System.out.println("Le mur de gauche de votre case a été détruit et l'accès mis en place");
                    j.getPlateau().getCase(x,y-1).setVisible(true);
                    
                }
            }

            if("haut".equals(s)){
                if(j.getPlateau().getCase(x,y).isAccessible(3)){
                    System.out.println("L'accès est déjà possible en haut");
                }
                    else{
                    j.getPlateau().getCase(x,y).setAccessible(3);
                        System.out.println("Le mur du haut de votre case a été détruit et l'accès mis en place");
                        j.getPlateau().getCase(x-1,y).setVisible(true);
                    }
                }
            

            if("bas".equals(s)){
            if(j.getPlateau().getCase(x,y).isAccessible(4)){
                System.out.println("L'accès est déjà possible en bas");
            }
                else{
                j.getPlateau().getCase(x,y).setAccessible(4);
                System.out.println("Le mur du bas de votre case a été détruit et l'accès mis en place");
                j.getPlateau().getCase(x+1,y).setVisible(true);
             }
            }


        /*creuse un mur d'une case sans faire exploser la mine qu'elle pourrait contenir, la mine n'est pas désactivée, le joueur ne peut pas rentrer dans la case sans la faire exploser */
    }
}
