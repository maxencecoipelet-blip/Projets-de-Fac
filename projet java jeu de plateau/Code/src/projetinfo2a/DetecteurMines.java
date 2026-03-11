package projetinfo2a;

public class DetecteurMines extends Outil {
    

    public DetecteurMines() {
        super(
                "DMI",
                "DetecteurMines",
                "Permet de connaître le nombre total de mines qui si situent dans les cases contigües",
                3// coût énergétique de l'utilisation du détecteur de mines
        );
    }

    @Override
    public void activation(Joueur j){
        int compteur = 0;
        int x = j.getXJoueur();
        int y = j.getYJoueur();
        
         /* vers le haut */
            
         if((x-1)>=0 && "Mine".equals(j.getPlateau().getCase(x-1,y).lookContent()))  
         compteur = compteur + 1;

         /* vers le bas */
        
            if((x+1)<j.getPlateau().getTailleX() && "Mine".equals(j.getPlateau().getCase(x+1,y).lookContent()))  
            compteur = compteur + 1;
        

         /* vers la gauche */
         
            if((y-1)>=0 && "Mine".equals(j.getPlateau().getCase(x,y-1).lookContent()))  
            compteur = compteur + 1;

         /* vers la droite */
         
            if((y+1)<j.getPlateau().getTailleY() && "Mine".equals(j.getPlateau().getCase(x,y+1).lookContent()))  
            compteur = compteur + 1;

         /* vers le haut droit */
         if((y+1)<j.getPlateau().getTailleY() && (x-1)>=0 && "Mine".equals(j.getPlateau().getCase(x-1,y+1).lookContent()))  
            compteur = compteur + 1;
        

         /* vers le bas droit */
         if((y+1)<j.getPlateau().getTailleY() && (x+1)<j.getPlateau().getTailleX() && "Mine".equals(j.getPlateau().getCase(x+1,y+1).lookContent()))  
            compteur = compteur + 1;
        
        

         /* vers le haut gauche */
         if((y-1)>=0 && (x-1)>=0 && "Mine".equals(j.getPlateau().getCase(x-1,y-1).lookContent()))  
            compteur = compteur + 1;
        

         /* vers le bas gauche */
         if((y-1)>=0 && (x+1)<j.getPlateau().getTailleX() && "Mine".equals(j.getPlateau().getCase(x+1,y-1).lookContent()))  
            compteur = compteur + 1;

         System.out.println("Il y a " + compteur +" mine(s) dans les cases contigües au joueur");
        }

        

    }
