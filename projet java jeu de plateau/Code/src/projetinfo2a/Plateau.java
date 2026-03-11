package projetinfo2a;

public class Plateau {

    //Attributs
   
   private CaseSpec[][] plateau;
   private int tailleX;
   private int tailleY;
   private Joueur joueur;
   private Jeu jeu;
   
   public Joueur getJoueur(){
       return this.joueur;
   }
   
   public void setJoueur(Joueur j){
       this.joueur=j;
   }
   
   public Jeu getJeu(){
       return this.jeu;
   }
   
   public void setJeu(Jeu j){
       this.jeu=j;
   }
   
   //Constructeurs
   
   public Plateau(){                          //Constructeur par défault (Cases vide, taille 5,5)
       this.plateau = new CaseSpec[5][5];
       for(int i = 0 ; i<5 ; i++){
           for(int j = 0 ; j<5 ; j++){
               this.plateau[i][j]= new CaseSpec(i,j,2,getJeu());
           }
       }
   }
   
   public Plateau(int X, int Y,Jeu jo ){     //Constructeur standart (Cases aléatoires [de type 0 à type 4], taille en paramètre)
       setJeu(jo);        
       this.tailleX=X;
       this.tailleY=Y;
       this.plateau = new CaseSpec[X][Y];
       for(int i = 0 ; i<tailleX ; i++){
           for(int j = 0 ; j<tailleY ; j++){
               int random = (int) (Math.random() * 101); // renvoie entre 0 et 100
               if(random >= 0 && random <= 19)
               this.plateau[i][j]= new CaseSpec(i,j,4,getJeu()); //proba qu'une case contienne un outil
            else{

                if(random >=20 && random <= 39) 
                    this.plateau[i][j]= new CaseSpec(i,j,3,getJeu()); //proba qu'une case contienne une réserve d'énergie
                else{
                        if(random >=40 && random <= 55) 
                            this.plateau[i][j]= new CaseSpec(i,j,2,getJeu()); //proba qu'une case contienne une caisse de grenade
                        else{
            
                                if(random >= 56 && random < 71) 
                                    this.plateau[i][j]= new CaseSpec(i,j,1,getJeu()); //proba qu'une case contienne une mine
                                else
                                    this.plateau[i][j]= new CaseSpec(i,j,0,getJeu()); //probabe qu'une case soit vide
                        }
                    }
                }
            }   
        }
       genereArrive();
   }
   
   //Méthodes

   public String toString(){                    //Affiche le plateau avec la texture des cases.
       String chaine="";
       for(int i = 0 ; i<this.tailleX; i++){
           for(int j = 0 ; j<this.tailleY ; j++){
             chaine=chaine+this.plateau[i][j].getTexture();  //Ce getTexture() est une méthode de la classe abstaire Case et fonctionne pour toutes les cases.
           }
         chaine=chaine+"\n";
       }
     return chaine;
   }

   public void setPlateauVisible(){
    for(int i=0;i<this.tailleX;i++){
        for(int j=0; j<this.tailleY;j++){
            this.plateau[i][j].setVisible(true);
        }
    }
   }

   public void setPlateauInvisible(){
    for(int i=0;i<this.tailleX;i++){
        for(int j=0; j<this.tailleY;j++){
            this.plateau[i][j].setVisible(false);
        }
    }
    int x = getJoueur().getXJoueur();
    int y = getJoueur().getYJoueur();
    getCase(x,y).setVisible(true);
   }
   
   public CaseSpec getCase(int x, int y){
       return this.plateau[x][y];
   }
   
   public int getTailleX(){
       return this.tailleX;
   }
   
   public int getTailleY(){
       return this.tailleY;
   }

   public void genereArrive(){
    int random = (int) (1 + Math.random() * 4);
    if(random==1){
        this.plateau[0][0]=new CaseSpec(0,0,5,getJeu()); //génère dans le coin en haut à gauche
    }

    if(random==2){
        this.plateau[0][getTailleY()-1]=new CaseSpec(0,0,5,getJeu()); //génère dans le coin en haut à droite
    }

    if(random==3){
        this.plateau[getTailleX()-1][0]=new CaseSpec(0,0,5,getJeu()); //génère dans le coin en bas à gauche
    }

    if(random==4){
        this.plateau[getTailleX()-1][getTailleY()-1]=new CaseSpec(0,0,5,getJeu()); //génère dans le coin en bas à droite
    }

   }
}
