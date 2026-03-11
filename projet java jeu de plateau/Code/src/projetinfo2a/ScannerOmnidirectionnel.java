package projetinfo2a;

public class ScannerOmnidirectionnel extends Outil{
    
    public ScannerOmnidirectionnel() {
        super(
                "SO",
                "ScannerOmnidirectionnel",
                "Permet de détecter au travers des murs le nombre de cases contigües contenant un objet",
                1// coût énergétique de l'utilisation du scanner omnidirectionnel
        );
    }

    @Override
    public void activation(Joueur j){
        int compteur = 0;
        int x = j.getXJoueur();
        int y = j.getYJoueur();
        
         /* vers la droite */
            
        if((y+1)<j.getPlateau().getTailleY() && (j.getPlateau().getCase(x,y+1).lookContent()!="Vide"||j.getPlateau().getCase(x,y+1).getTexture()!="   "))  
            compteur = compteur + 1;

         /* vers la gauche */
        if((y-1)>=0 && (j.getPlateau().getCase(x,y-1).getTexture()!="   " ||j.getPlateau().getCase(x,y-1).lookContent()!="Vide"))  
            compteur = compteur + 1;
        

         /* vers le haut */
         if((x-1)>=0 && (j.getPlateau().getCase(x-1,y).getTexture()!="   " ||j.getPlateau().getCase(x-1,y).lookContent()!="Vide" ))  
            compteur = compteur + 1;

         /* vers le bas */
         if((x+1)<j.getPlateau().getTailleX() && (j.getPlateau().getCase(x+1,y).getTexture()!="   "||j.getPlateau().getCase(x+1,y).lookContent()!="Vide"))  
            compteur = compteur + 1;

         /* vers le haut droit */
         if((y+1)<j.getPlateau().getTailleY() && (x-1)>=0 && (j.getPlateau().getCase(x-1,y+1).getTexture()!="   "||j.getPlateau().getCase(x-1,y+1).lookContent()!="Vide" ))  
            compteur = compteur + 1;
        

         /* vers le bas droit */
         if((y+1)<j.getPlateau().getTailleY() && (x+1)<j.getPlateau().getTailleX() && (j.getPlateau().getCase(x+1,y+1).getTexture()!="   "||j.getPlateau().getCase(x+1,y+1).lookContent()!="Vide"))  
            compteur = compteur + 1;
        
        

         /* vers le haut gauche */
         if((y-1)>=0 && (x-1)>=0 && (j.getPlateau().getCase(x-1,y-1).getTexture()!="   "||j.getPlateau().getCase(x-1,y-1).lookContent()!="Vide")) 
            compteur = compteur + 1;
        

         /* vers le bas gauche */
         if((y+1)>=0 && (x+1)<j.getPlateau().getTailleX() && (j.getPlateau().getCase(x+1,y-1).getTexture()!="   "||j.getPlateau().getCase(x+1,y-1).lookContent()!="Vide"))  
            compteur = compteur + 1;

         System.out.println("Il y a " + compteur +" objets dans les cases contigües au joueur");
        }
    }
