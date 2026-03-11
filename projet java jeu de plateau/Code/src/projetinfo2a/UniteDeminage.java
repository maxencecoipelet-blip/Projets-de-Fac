package projetinfo2a;

public class UniteDeminage extends Outil { 
    public UniteDeminage()
    {
        super
        (
                "UD",
                "UniteDeminage",
                "Désactive une mine située dans une salle contigüe accessible",
                2//coût énergétique de l'utilisation de l'unité de déminage
        );
    }

    @Override
    public void activation(Joueur j){
        String s="";
        do{System.out.println("Dans quelle salle contigüe voulez-vous désactiver une mine ? (n'importe quelle direction : 'haut', 'bas droite', 'gauche', 'haut droite', etc)");
            s=Lire.S();
        } while (!"haut".equals(s) && !"bas".equals(s) && !"gauche".equals(s) && !"droite".equals(s) && !"haut gauche".equals(s) && !"bas gauche".equals(s) && !"haut droite".equals(s) && !"bas droite".equals(s));
        int x = j.getXJoueur();
        int y = j.getYJoueur();
        if("haut".equals(s)){
            if((x-1)>=0 && j.getPlateau().getCase(x,y).isAccessible(3) && "Mine".equals(j.getPlateau().getCase(x-1,y).lookContent())){
                j.getPlateau().getCase(x-1,y).setContent("Vide");
            }

            else{
                System.out.println("La case n'existe pas, n'est pas accessible ou alors il n'y a pas de mine dedans, vous ne pouvez donc rien faire");
            }
        }


        if("bas".equals(s)){
           
            if((x+1)<j.getPlateau().getTailleX() && j.getPlateau().getCase(x,y).isAccessible(4) && "Mine".equals(j.getPlateau().getCase(x+1,y).lookContent())){
                j.getPlateau().getCase(x+1,y).setContent("Vide");
            }

            else{
                System.out.println("La case n'existe pas, n'est pas accessible ou alors il n'y a pas de mine dedans, vous ne pouvez donc rien faire");
            }
        }

        if("gauche".equals(s)){
           
            if((y-1)>=0 && j.getPlateau().getCase(x,y).isAccessible(2) && "Mine".equals(j.getPlateau().getCase(x,y-1).lookContent())){
                j.getPlateau().getCase(x, y-1).setContent("Vide");
            }

            else{
                System.out.println("La case n'existe pas, n'est pas accessible ou alors il n'y a pas de mine dedans, vous ne pouvez donc rien faire");
            }
        }

        if("droite".equals(s)){
            
            if((y+1)<j.getPlateau().getTailleY() && j.getPlateau().getCase(x,y).isAccessible(1) && "Mine".equals(j.getPlateau().getCase(x,y+1).lookContent())){
                j.getPlateau().getCase(x, y+1).setContent("Vide");
            }

            else{
                System.out.println("La case n'existe pas, n'est pas accessible ou alors il n'y a pas de mine dedans, vous ne pouvez donc rien faire");
            }
        }

        if("haut droite".equals(s)){
            if((y+1)<j.getPlateau().getTailleY() && (x-1)>=0 && j.getPlateau().getCase(x-1,y+1).isAccessible(2) && j.getPlateau().getCase(x-1,y+1).isAccessible(4) && "Mine".equals(j.getPlateau().getCase(x-1,y+1).lookContent())){
                j.getPlateau().getCase(x-1,y+1).setContent("Vide");
            }
            else{
                System.out.println("La case n'existe pas, n'est pas accessible ou alors il n'y a pas de mine dedans, vous ne pouvez donc rien faire");
            }
        }

        if("bas droite".equals(s)){
            if((y+1)<j.getPlateau().getTailleY() && (x+1)<j.getPlateau().getTailleX() && j.getPlateau().getCase(x+1,y+1).isAccessible(2) && j.getPlateau().getCase(x+1,y+1).isAccessible(4) && "Mine".equals(j.getPlateau().getCase(x+1,y+1).lookContent())){
                j.getPlateau().getCase(x+1,y+1).setContent("Vide");
            }
            else{
                System.out.println("La case n'existe pas, n'est pas accessible ou alors il n'y a pas de mine dedans, vous ne pouvez donc rien faire");
            }
        }

        if("haut gauche".equals(s)){
            if((y-1)>=0 && (x-1)>=0 && j.getPlateau().getCase(x-1,y-1).isAccessible(1) && j.getPlateau().getCase(x-1,y-1).isAccessible(4) &&"Mine".equals(j.getPlateau().getCase(x-1,y-1).lookContent())){
                j.getPlateau().getCase(x-1,y-1).setContent("Vide");
            }

            else{
                System.out.println("La case n'existe pas, n'est pas accessible ou alors il n'y a pas de mine dedans, vous ne pouvez donc rien faire");
            }
        }

        if("bas gauche".equals(s)){
            if((y-1)>=0 && (x+1)<j.getPlateau().getTailleX() && j.getPlateau().getCase(x+1,y-1).isAccessible(1) && j.getPlateau().getCase(x+1,y-1).isAccessible(3) && "Mine".equals(j.getPlateau().getCase(x+1,y-1).lookContent())){
                j.getPlateau().getCase(x+1,y-1).setContent("Vide");
            }
            else{
                System.out.println("La case n'existe pas, n'est pas accessible ou alors il n'y a pas de mine dedans, vous ne pouvez donc rien faire");
            }
        }

    }
}