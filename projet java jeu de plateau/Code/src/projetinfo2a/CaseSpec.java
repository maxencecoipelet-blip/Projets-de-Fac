package projetinfo2a;

public class CaseSpec extends Case {

    private String texture;
    boolean visible=false;
    private String content;
    private Jeu jeu;
    private boolean murDroit;
    private boolean murGauche;
    private boolean murHaut;
    private boolean murBas;
    private int nbGre;
    private int nbEne;

    public int getNbGre(){
        return this.nbGre;
    }

    public int getNbEne(){
        return this.nbEne;
    }

    public void setNbGre(int a){
        this.nbGre=a;
    }

    public void setNbEne(int a){
        this.nbEne=a;
    }
    
    public Jeu getJeu(){
        return this.jeu;
    }

    public void setJeu(Jeu j){
        this.jeu=j;
    }
    public boolean getMurDroit(){
        return this.murDroit;
    }

    public void setMurDroit(boolean a){
        this.murDroit=a;
    }

    public boolean getMurGauche(){
        return this.murGauche;
    }

    public void setMurGauche(boolean a){
        this.murGauche=a;
    }

    public boolean getMurHaut(){
        return this.murHaut;
    }

    public void setMurHaut(boolean a){
        this.murHaut=a;
    }

    public boolean getMurBas(){
        return this.murBas;
    }

    public void setMurBas(boolean a){
        this.murBas=a;
    }

    public String getTexture(){                    
        if(isVisible())
        return this.texture;
        else
        return "░░░";  //"░"
    }

    public void setTexture(String texture){
        this.texture=texture;
    }

    //Méthodes

    public boolean isVisible(){     //check la visibilité              
      return this.visible;
    }

    public void setVisible(boolean b){         //rendre visibile ou invisible
        this.visible=b;
    }
    
    public void setContent(String content){        //Set le contenu de la case
        this.content=content;
    }


    public CaseSpec(int i, int j, int type, Jeu je){          //Constructeur
        super(i,j);
        setJeu(je);
        setMurHaut(false);
        setMurBas(false);
        setMurGauche(false);
        setMurDroit(false);

        switch(type){
            case 0:
                this.texture = "   "; //Vide
                this.content = "Vide";
                break;
            case 1:
                this.texture = " M ";  //Mines
                this.content = "Mine";
            
                break;
            case 2:
                this.texture = " G ";  //Grenades
                this.content = "Grenade";
                int gre = 1 + (int)  (Math.random()*10);
                setNbGre(gre);

                break;
            case 3:
                this.texture = " E "; //Energie
                this.content = "Energie";
                int ener = 1 + (int)  (Math.random()*20);
                setNbEne(ener);
                break;
            case 4:
                this.texture = " O ";  //Outils [Quand on arrive sur un outil, on fait un math.random() pour déterminer quel outil apparait]
                double random = 1 + (int)(Math.random() * 6);
                  if(random == 1)
                  this.content = "Excavatrice";
                  if(random == 2)
                  this.content = "ScannerUnidirectionnel";
                  if(random == 3)
                  this.content = "ScannerOmnidirectionnel";
                  if(random == 4)
                  this.content = "DetecteurMassique";
                  if(random == 5)
                  this.content = "UniteDeminage";
                  if(random == 6)
                  this.content = "DetecteurMines";
                break;

            case 5:
            this.texture = " A "; //case arrivée
            this.content="Arrivee";
            break;

            default:
                this.texture = " - ";
                this.content = "erreur";
                break;
        }

    }

    //Méthodes pour le contenu des salles

    public String lookContent(){     //Donne le contenu d'une salle
        return this.content;
    }

    public void setAccessible(int a){       //4 possibilités, haut bas gauche ou droite (a entre 1 et 4), on rend l'acces inverse accessible de la case où l'on veut aller
        int x = getJeu().getPlateau().getJoueur().getXJoueur();
        int y = getJeu().getPlateau().getJoueur().getYJoueur();       
        if(a==1){ //droite
            setMurDroit(true);
            getJeu().getPlateau().getCase(x,y+1).setMurGauche(true);
        }

        if(a==2){ //gauche
            setMurGauche(true);
            getJeu().getPlateau().getCase(x,y-1).setMurDroit(true);
        }

        if(a==3){ //haut
            setMurHaut(true);
            getJeu().getPlateau().getCase(x-1,y).setMurBas(true);
        }

        if(a==4){ //bas
            setMurBas(true);
            getJeu().getPlateau().getCase(x+1,y).setMurHaut(true);
        }
    }

    public boolean isAccessible(){ //savoir si une case a au moins un acces
        if(this.murHaut==false && this.murBas==false && this.murGauche==false && this.murDroit==false){
            return false;
        }
        else
            return true;
    }

    public boolean isAccessible(int a) {//savoir si la case de la direction choisi est accessible, par exemple si celle en haut est accessible (4 choix possibles)
        switch (a) {
            case 1:  // Droit
                return this.murDroit;
            case 2:  // Gauche
                return this.murGauche;
            case 3:  // Haut
                return this.murHaut;
            case 4:  // Bas
                return this.murBas;
            default:
                return false;
        }
    }
  
}


