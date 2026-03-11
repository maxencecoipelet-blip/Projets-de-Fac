package projetinfo2a;
public class Joueur {

    private String nom;
    private int x;
    private int y;
    private Inventaire inventaire;
    private Jeu jeu;

    public Jeu getJeu(){
        return this.jeu;
    }

    public Plateau getPlateau(){
        return getJeu().getPlateau();
    }
    

    public Joueur(String nom, int x, int y,Jeu j){
    this.jeu=j;
    this.nom = nom;
    this.x=x;
    this.y=y;
    setInventaire(new Inventaire());
    
    }

    public int getXJoueur(){
        return this.x;
    }

    public int getYJoueur(){
        return this.y;
    }

    public void setXJoueur(int x){
        this.x=x;
    }

    public void setYJoueur(int y){
        this.y=y;
    }


    public void avance(int a){
        int x = getXJoueur();
        int y = getYJoueur();
                                            // 'a' ne peut être que entre 1 et 8 (8 possibilités de direction), il faut donc faire un do while là où l'on va appeller la méthode
        if(a==1){ /* vers la droite */
            if((y+1)<getPlateau().getTailleY() && getPlateau().getCase(x,y).isAccessible(1)==true)  
                {setYJoueur(y+1);
                getPlateau().getCase(x,y+1).setVisible(true);
                CaseSpec cj = getPlateau().getCase(x,y);
                CaseSpec cjd = getPlateau().getCase(x,y+1);// cjd pour case joueur déplacement
                    if ("Mine".equals(cjd.lookContent())) 
                    {   
                        System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                        System.out.println("GAME OVER");
                        getJeu().setJeu(false);
                        getPlateau().setPlateauVisible();
                        System.out.println(getPlateau().toString());

                    }
                    else{
                    if(" O ".equals(cjd.getTexture())){
                        System.out.println("Vous avez avancé sur une case contenant un outil");
                        System.out.println("Cette case contient l'outil : " + cjd.lookContent());
                        getInventaire().ajoutOutil(getInventaire().getOutil(cjd.lookContent()));
                        cjd.setTexture(" OJ");
                        cj.setTexture("   ");
                        cj.setContent("   ");
                    }

                    else{
                        if("Grenade".equals(cjd.lookContent())){
                            int nb=0;
                            System.out.println("Vous avez avancé dans une case contenant une caisse de grenades");
                            do{System.out.println("Il y a "+cjd.getNbGre()+" grenades dans cette caisse, combien voulez-vous en prendre ?");
                            nb = Lire.i();} while(nb>cjd.getNbGre() || nb<0);
                            getInventaire().setNbGrenade(getInventaire().getNbGrenade()+nb);
                            cjd.setNbGre(cjd.getNbGre()-nb);
                            cjd.setTexture(" GJ");
                            cj.setTexture("   ");
                        }
                        else{
                            if("Energie".equals(cjd.lookContent())){
                                int nb=0;
                                System.out.println("Vous avez avancé dans une case contenant une réserve d'énergie");
                                do{System.out.println("Il y a "+cjd.getNbEne()+" unité(s) d'énergie dans cette caisse, combien voulez-vous en prendre ?");
                                nb = Lire.i();} while(nb>cjd.getNbEne() || nb<0);
                                getInventaire().setNbEnergie(getInventaire().getNbEnergie()+nb);
                                cjd.setNbEne(cjd.getNbEne()-nb);
                                cjd.setTexture(" EJ");
                                cj.setTexture("   ");
                            }
                            else{
                                if("Arrivee".equals(cjd.lookContent())){
                                    cj.setTexture("   ");
                                    cjd.setTexture(" AJ");
                                    System.out.println("Vous avez atteint l'arrivée, la partie est terminé !!!!");
                                    getJeu().setJeu(false);
                                    getPlateau().setPlateauVisible();
                                    System.out.println(getPlateau().toString());
                                }
                                else{
                                System.out.println("Vous avez avancé dans une case vide");
                                cjd.setTexture(" J ");
                                cj.setTexture("   ");
                                
                                }
                            }
                        }

                        
                    }
                }
                }
                else
                System.out.println("Vous ne pouvez pas aller à droite");
            }
        
            
        if(a==2){ /* vers la gauche */
            if((y-1)>=0 && getPlateau().getCase(x,y).isAccessible(2)==true)
            {   setYJoueur(y-1);
                getPlateau().getCase(x,y-1).setVisible(true);
                CaseSpec cj = getPlateau().getCase(x,y);
                CaseSpec cjd = getPlateau().getCase(x,y-1);// cjd pour case joueur déplacement
                    if ("Mine".equals(cjd.lookContent())) 
                    {   
                        System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                        System.out.println("GAME OVER");
                        getJeu().setJeu(false);
                        getPlateau().setPlateauVisible();
                        System.out.println(getPlateau().toString());

                    }
                    else{
                    if(" O ".equals(cjd.getTexture())){
                        System.out.println("Vous avez avancé sur une case contenant un outil");
                        System.out.println("Cette case contient l'outil : " + cjd.lookContent());
                        getInventaire().ajoutOutil(getInventaire().getOutil(cjd.lookContent()));
                        cjd.setTexture(" OJ");
                        cj.setTexture("   ");
                        cj.setContent("   ");
                        
                    }

                    else{
                        if("Grenade".equals(cjd.lookContent())){
                            int nb=0;
                            System.out.println("Vous avez avancé dans une case contenant une caisse de grenades");
                            do{System.out.println("Il y a "+cjd.getNbGre()+" grenades dans cette caisse, combien voulez-vous en prendre ?");
                            nb = Lire.i();} while(nb>cjd.getNbGre() || nb<0);
                            getInventaire().setNbGrenade(getInventaire().getNbGrenade()+nb);
                            cjd.setNbGre(cjd.getNbGre()-nb);
                            cjd.setTexture(" GJ");
                            cj.setTexture("   ");
                        }
                        else{
                            if("Energie".equals(cjd.lookContent())){
                                int nb=0;
                                System.out.println("Vous avez avancé dans une case contenant une réserve d'énergie");
                                do{System.out.println("Il y a "+cjd.getNbEne()+" unité(s) d'énergie dans cette caisse, combien voulez-vous en prendre ?");
                                nb = Lire.i();} while(nb>cjd.getNbEne() || nb<0);
                                getInventaire().setNbEnergie(getInventaire().getNbEnergie()+nb);
                                cjd.setNbEne(cjd.getNbEne()-nb);
                                cjd.setTexture(" EJ");
                                cj.setTexture("   ");
                            }
                            else{
                                if("Arrivee".equals(cjd.lookContent())){
                                    cj.setTexture("   ");
                                    cjd.setTexture(" AJ");
                                    System.out.println("Vous avez atteint l'arrivée, la partie est terminé !!!!");
                                    getJeu().setJeu(false);
                                    getPlateau().setPlateauVisible();
                                    System.out.println(getPlateau().toString());
                                }
                                else{
                                System.out.println("Vous avez avancé dans une case vide");
                                cjd.setTexture(" J ");
                                cj.setTexture("   ");
                                }
                            }
                        }

                        
                    }
                }
            }
                else
                    System.out.println("Vous ne pouvez pas aller à gauche");
            
          
            }
        
        

        if(a==3){ /* vers le haut */
            if((x-1)>=0 && getPlateau().getCase(x,y).isAccessible(3)==true)
                {
                    setXJoueur(x-1);
                    getPlateau().getCase(x-1,y).setVisible(true);
                    CaseSpec cj = getPlateau().getCase(x,y);
                    CaseSpec cjd = getPlateau().getCase(x-1,y);// cjd pour case joueur déplacement
                        if ("Mine".equals(cjd.lookContent())) 
                        {   
                            System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                            System.out.println("GAME OVER");
                            getJeu().setJeu(false);
                            getPlateau().setPlateauVisible();
                            System.out.println(getPlateau().toString());

                        }
                        else{
                        if(" O ".equals(cjd.getTexture())){
                            System.out.println("Vous avez avancé sur une case contenant un outil");
                            System.out.println("Cette case contient l'outil : " + cjd.lookContent());
                            getInventaire().ajoutOutil(getInventaire().getOutil(cjd.lookContent()));
                            cjd.setTexture(" OJ");
                            cj.setTexture("   ");
                            cj.setContent("   ");
                        }

                        else{
                            if("Grenade".equals(cjd.lookContent())){
                                int nb=0;
                                System.out.println("Vous avez avancé dans une case contenant une caisse de grenades");
                                do{System.out.println("Il y a "+cjd.getNbGre()+" grenades dans cette caisse, combien voulez-vous en prendre ?");
                                nb = Lire.i();} while(nb>cjd.getNbGre() || nb<0);
                                getInventaire().setNbGrenade(getInventaire().getNbGrenade()+nb);
                                cjd.setNbGre(cjd.getNbGre()-nb);
                                cjd.setTexture(" GJ");
                                cj.setTexture("   ");
                            }
                            else{
                                if("Energie".equals(cjd.lookContent())){
                                    int nb=0;
                                    System.out.println("Vous avez avancé dans une case contenant une réserve d'énergie");
                                    do{System.out.println("Il y a "+cjd.getNbEne()+" unité(s) d'énergie dans cette caisse, combien voulez-vous en prendre ?");
                                    nb = Lire.i();} while(nb>cjd.getNbEne() || nb<0);
                                    getInventaire().setNbEnergie(getInventaire().getNbEnergie()+nb);
                                    cjd.setNbEne(cjd.getNbEne()-nb);
                                    cjd.setTexture(" EJ");
                                    cj.setTexture("   ");
                                }
                                else{
                                    if("Arrivee".equals(cjd.lookContent())){
                                        cj.setTexture("   ");
                                        cjd.setTexture(" AJ");
                                        System.out.println("Vous avez atteint l'arrivée, la partie est terminé !!!!");
                                        getJeu().setJeu(false);
                                        getPlateau().setPlateauVisible();
                                        System.out.println(getPlateau().toString());
                                    }
                                    else{
                                    System.out.println("Vous avez avancé dans une case vide");
                                    cjd.setTexture(" J ");
                                    cj.setTexture("   ");
                                    }
                                }
                            }

                            
                        }
                    }
                }
                    else
                    System.out.println("Vous ne pouvez pas aller en haut");
        }
            

        if(a==4){ /* vers le bas */
            if((x+1)<getPlateau().getTailleX() && getPlateau().getCase(x,y).isAccessible(4)==true)
                {
                    setXJoueur(x+1);
                    getPlateau().getCase(x+1,y).setVisible(true);
                    CaseSpec cj = getPlateau().getCase(x,y);
                    CaseSpec cjd = getPlateau().getCase(x+1,y);// cjd pour case joueur déplacement
                        if ("Mine".equals(cjd.lookContent())) 
                        {   
                            System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                            System.out.println("GAME OVER");
                            getJeu().setJeu(false);
                            getPlateau().setPlateauVisible();
                            System.out.println(getPlateau().toString());

                        }
                        else{
                        if(" O ".equals(cjd.getTexture())){
                            System.out.println("Vous avez avancé sur une case contenant un outil");
                            System.out.println("Cette case contient l'outil : " + cjd.lookContent());
                            getInventaire().ajoutOutil(getInventaire().getOutil(cjd.lookContent()));
                            cjd.setTexture(" OJ");
                            cj.setTexture("   ");
                            cj.setContent("   ");
                            
                        }

                        else{
                            if("Grenade".equals(cjd.lookContent())){
                                int nb=0;
                                System.out.println("Vous avez avancé dans une case contenant une caisse de grenades");
                                do{System.out.println("Il y a "+cjd.getNbGre()+" grenades dans cette caisse, combien voulez-vous en prendre ?");
                                nb = Lire.i();} while(nb>cjd.getNbGre() || nb<0);
                                getInventaire().setNbGrenade(getInventaire().getNbGrenade()+nb);
                                cjd.setNbGre(cjd.getNbGre()-nb);
                                cjd.setTexture(" GJ");
                                cj.setTexture("   ");
                            }
                            else{
                                if("Energie".equals(cjd.lookContent())){
                                    int nb=0;
                                    System.out.println("Vous avez avancé dans une case contenant une réserve d'énergie");
                                    do{System.out.println("Il y a "+cjd.getNbEne()+" unité(s) d'énergie dans cette caisse, combien voulez-vous en prendre ?");
                                    nb = Lire.i();} while(nb>cjd.getNbEne() || nb<0);
                                    getInventaire().setNbEnergie(getInventaire().getNbEnergie()+nb);
                                    cjd.setNbEne(cjd.getNbEne()-nb);
                                    cjd.setTexture(" EJ");
                                    cj.setTexture("   ");
                                }
                                else{
                                    if("Arrivee".equals(cjd.lookContent())){
                                        cj.setTexture("   ");
                                        cjd.setTexture(" AJ");
                                        System.out.println("Vous avez atteint l'arrivée, la partie est terminé !!!!");
                                        getJeu().setJeu(false);
                                        getPlateau().setPlateauVisible();
                                        System.out.println(getPlateau().toString());
                                    }
                                    else{
                                    System.out.println("Vous avez avancé dans une case vide");
                                    cjd.setTexture(" J ");
                                    cj.setTexture("   ");
                                    }
                                }
                            }

                            
                        }
                    }
                }
                    else
                    System.out.println("Vous ne pouvez pas aller en bas");
                
               
                
                }

        if(a==5){ /* vers le haut droit */
            if(y+1 < getPlateau().getTailleY() && x-1 >=0 && getPlateau().getCase(x-1,y+1).isAccessible(2)==true && getPlateau().getCase(x-1,y+1).isAccessible(4)==true) {
                setXJoueur(x-1);
                setYJoueur(y+1);
                getPlateau().getCase(x-1,y+1).setVisible(true);
                CaseSpec cj = getPlateau().getCase(x,y);
                CaseSpec cjd = getPlateau().getCase(x-1,y+1);// cjd pour case joueur déplacement
                    if ("Mine".equals(cjd.lookContent())) 
                    {   
                        System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                        System.out.println("GAME OVER");
                        getJeu().setJeu(false);
                        getPlateau().setPlateauVisible();
                        System.out.println(getPlateau().toString());

                    }
                    else{
                    if(" O ".equals(cjd.getTexture())){
                        System.out.println("Vous avez avancé sur une case contenant un outil");
                        System.out.println("Cette case contient l'outil : " + cjd.lookContent());
                        getInventaire().ajoutOutil(getInventaire().getOutil(cjd.lookContent()));
                        cjd.setTexture(" OJ");
                        cj.setTexture("   ");
                        cj.setContent("   ");
                    }

                    else{
                        if("Grenade".equals(cjd.lookContent())){
                            int nb=0;
                            System.out.println("Vous avez avancé dans une case contenant une caisse de grenades");
                            do{System.out.println("Il y a "+cjd.getNbGre()+" grenades dans cette caisse, combien voulez-vous en prendre ?");
                            nb = Lire.i();} while(nb>cjd.getNbGre() || nb<0);
                            getInventaire().setNbGrenade(getInventaire().getNbGrenade()+nb);
                            cjd.setNbGre(cjd.getNbGre()-nb);
                            cjd.setTexture(" GJ");
                            cj.setTexture("   ");
                        }
                        else{
                            if("Energie".equals(cjd.lookContent())){
                                int nb=0;
                                System.out.println("Vous avez avancé dans une case contenant une réserve d'énergie");
                                do{System.out.println("Il y a "+cjd.getNbEne()+" unité(s) d'énergie dans cette caisse, combien voulez-vous en prendre ?");
                                nb = Lire.i();} while(nb>cjd.getNbEne() || nb<0);
                                getInventaire().setNbEnergie(getInventaire().getNbEnergie()+nb);
                                cjd.setNbEne(cjd.getNbEne()-nb);
                                cjd.setTexture(" EJ");
                                cj.setTexture("   ");
                            }
                            else{
                                if("Arrivee".equals(cjd.lookContent())){
                                    cj.setTexture("   ");
                                    cjd.setTexture(" AJ");
                                    System.out.println("Vous avez atteint l'arrivée, la partie est terminé !!!!");
                                    getJeu().setJeu(false);
                                    getPlateau().setPlateauVisible();
                                    System.out.println(getPlateau().toString());
                                }
                                else{
                                System.out.println("Vous avez avancé dans une case vide");
                                cjd.setTexture(" J ");
                                cj.setTexture("   ");
                                }
                            }
                        }

                        
                    }
                }
            }
                else
                System.out.println("Vous ne pouvez pas aller en haut à droite");
        
            }

        if(a==6){ /* vers le bas droite */
            if(y+1 < getPlateau().getTailleY() && x+1 < getPlateau().getTailleX() && getPlateau().getCase(x+1,y+1).isAccessible(3)==true && getPlateau().getCase(x+1,y+1).isAccessible(2)==true ){
                setXJoueur(x+1);
                setYJoueur(y+1);
                getPlateau().getCase(x+1,y+1).setVisible(true);
                CaseSpec cj = getPlateau().getCase(x,y);
                CaseSpec cjd = getPlateau().getCase(x+1,y+1);// cjd pour case joueur déplacement

                    if ("Mine".equals(cjd.lookContent())) 
                    {   
                        System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                        System.out.println("GAME OVER");
                        getJeu().setJeu(false);
                        getPlateau().setPlateauVisible();
                        System.out.println(getPlateau().toString());

                    }
                    else{
                    if(" O ".equals(cjd.getTexture())){
                        System.out.println("Vous avez avancé sur une case contenant un outil");
                        System.out.println("Cette case contient l'outil : " + cjd.lookContent());
                        getInventaire().ajoutOutil(getInventaire().getOutil(cjd.lookContent()));
                        cjd.setTexture(getSymbole());
                        cj.setTexture(" OJ");
                        cj.setTexture("   ");
                        cj.setContent("   ");
                    }

                    else{
                        if("Grenade".equals(cjd.lookContent())){
                            int nb=0;
                            System.out.println("Vous avez avancé dans une case contenant une caisse de grenades");
                            do{System.out.println("Il y a "+cjd.getNbGre()+" grenades dans cette caisse, combien voulez-vous en prendre ?");
                            nb = Lire.i();} while(nb>cjd.getNbGre() || nb<0);
                            getInventaire().setNbGrenade(getInventaire().getNbGrenade()+nb);
                            cjd.setTexture(" GJ");
                            cj.setTexture("   ");
                        }
                        else{
                            if("Energie".equals(cjd.lookContent())){
                                int nb=0;
                                System.out.println("Vous avez avancé dans une case contenant une réserve d'énergie");
                                do{System.out.println("Il y a "+cjd.getNbEne()+" unité(s) d'énergie dans cette caisse, combien voulez-vous en prendre ?");
                                nb = Lire.i();} while(nb>cjd.getNbEne() || nb<0);
                                getInventaire().setNbEnergie(getInventaire().getNbEnergie()+nb);
                                cjd.setNbEne(cjd.getNbEne()-nb);
                                cjd.setTexture(" EJ");
                                cj.setTexture("   ");
                            }
                            else{
                                if("Arrivee".equals(cjd.lookContent())){
                                    cj.setTexture("   ");
                                    cjd.setTexture(" AJ");
                                    System.out.println("Vous avez atteint l'arrivée, la partie est terminé !!!!");
                                    getJeu().setJeu(false);
                                    getPlateau().setPlateauVisible();
                                    System.out.println(getPlateau().toString());
                                }
                                else{
                                System.out.println("Vous avez avancé dans une case vide");
                                cjd.setTexture(" J ");
                                cj.setTexture("   ");
                                }
                            }
                        }

                        
                    }
                }
            }
                else
                System.out.println("Vous ne pouvez pas aller en bas à droite");
            }
        

        if(a==7){ /* vers le haut gauche */
            if(y-1 >=0 && x-1 >=0 && getPlateau().getCase(x-1,y-1).isAccessible(1)==true && getPlateau().getCase(x-1,y-1).isAccessible(4)==true){
                setXJoueur(x-1);
                setYJoueur(y-1);
                getPlateau().getCase(x-1,y-1).setVisible(true);
                CaseSpec cj = getPlateau().getCase(x,y);
                CaseSpec cjd = getPlateau().getCase(x-1,y-1);// cjd pour case joueur déplacement
                    if ("Mine".equals(cjd.lookContent())) 
                        {   
                            System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                            System.out.println("GAME OVER");
                            getJeu().setJeu(false);
                            getPlateau().setPlateauVisible();
                            System.out.println(getPlateau().toString());

                        }
                    else{
                    if(" O ".equals(cjd.getTexture())){
                        System.out.println("Vous avez avancé sur une case contenant un outil");
                        System.out.println("Cette case contient l'outil : " + cjd.lookContent());
                        getInventaire().ajoutOutil(getInventaire().getOutil(cjd.lookContent()));
                        cjd.setTexture(" OJ");
                        cj.setTexture("   ");
                        cj.setContent("   ");
                    }

                    else{
                        if("Grenade".equals(cjd.lookContent())){
                            int nb=0;
                            System.out.println("Vous avez avancé dans une case contenant une caisse de grenades");
                            do{System.out.println("Il y a "+cjd.getNbGre()+" grenades dans cette caisse, combien voulez-vous en prendre ?");
                            nb = Lire.i();} while(nb>cjd.getNbGre() || nb<0);
                            getInventaire().setNbGrenade(getInventaire().getNbGrenade()+nb);
                            cjd.setNbGre(cjd.getNbGre()-nb);
                            cjd.setTexture(" GJ");
                            cj.setTexture("   ");
                        }
                        else{
                            if("Energie".equals(cjd.lookContent())){
                                int nb=0;
                                System.out.println("Vous avez avancé dans une case contenant une réserve d'énergie");
                                do{System.out.println("Il y a "+cjd.getNbEne()+" unité(s) d'énergie dans cette caisse, combien voulez-vous en prendre ?");
                                nb = Lire.i();} while(nb>cjd.getNbEne() || nb<0);
                                getInventaire().setNbEnergie(getInventaire().getNbEnergie()+nb);
                                cjd.setNbEne(cjd.getNbEne()-nb);
                                cjd.setTexture(" EJ");
                                cj.setTexture("   ");
                            }
                            else{
                                if("Arrivee".equals(cjd.lookContent())){
                                    cj.setTexture("   ");
                                    cjd.setTexture(" AJ");
                                    System.out.println("Vous avez atteint l'arrivée, la partie est terminé !!!!");
                                    getJeu().setJeu(false);
                                    getPlateau().setPlateauVisible();
                                    System.out.println(getPlateau().toString());
                                }
                                else{
                                System.out.println("Vous avez avancé dans une case vide");
                                cjd.setTexture(" J ");
                                cj.setTexture("   ");
                                }
                            }
                        }

                        
                    }
                }
            }
                else
                System.out.println("Vous ne pouvez pas aller en haut à gauche");
            }
        

        if(a==8){ /* vers le bas gauche */
            if(y-1 >=0 && x+1 < getPlateau().getTailleX() && getPlateau().getCase(x+1,y-1).isAccessible(1)==true && getPlateau().getCase(x+1,y-1).isAccessible(3)==true ){
                setXJoueur(x+1);
                setYJoueur(y-1);
                getPlateau().getCase(x+1,y-1).setVisible(true);
                CaseSpec cj = getPlateau().getCase(x,y);
                CaseSpec cjd = getPlateau().getCase(x+1,y-1);// cjd pour case joueur déplacement

                    if ("Mine".equals(cjd.lookContent())) 
                        {   
                            System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                            System.out.println("GAME OVER");
                            getJeu().setJeu(false);
                            getPlateau().setPlateauVisible();
                            System.out.println(getPlateau().toString());

                        }
                    else{
                    if(" O ".equals(cjd.getTexture())){
                        System.out.println("Vous avez avancé sur une case contenant un outil");
                        System.out.println("Cette case contient l'outil : " + cjd.lookContent());
                        getInventaire().ajoutOutil(getInventaire().getOutil(cjd.lookContent()));
                        cjd.setTexture(" OJ");
                        cj.setTexture("   ");
                        cj.setContent("   ");
                    }

                    else{
                        if("Grenade".equals(cjd.lookContent())){
                            int nb=0;
                            System.out.println("Vous avez avancé dans une case contenant une caisse de grenades");
                            do{System.out.println("Il y a "+cjd.getNbGre()+" grenades dans cette caisse, combien voulez-vous en prendre ?");
                            nb = Lire.i();} while(nb>cjd.getNbGre() || nb<0);
                            getInventaire().setNbGrenade(getInventaire().getNbGrenade()+nb);
                            cjd.setNbGre(cjd.getNbGre()-nb);
                            cjd.setTexture(" GJ");
                        }
                        else{
                            if("Energie".equals(cjd.lookContent())){
                                int nb=0;
                                System.out.println("Vous avez avancé dans une case contenant une réserve d'énergie");
                                do{System.out.println("Il y a "+cjd.getNbEne()+" unité(s) d'énergie dans cette caisse, combien voulez-vous en prendre ?");
                                nb = Lire.i();} while(nb>cjd.getNbEne() || nb<0);
                                getInventaire().setNbEnergie(getInventaire().getNbEnergie()+nb);
                                cjd.setNbEne(cjd.getNbEne()-nb);
                                cjd.setTexture(" EJ");
                            }
                            else{
                                if("Arrivee".equals(cjd.lookContent())){
                                    cj.setTexture("   ");
                                    System.out.println("Vous avez atteint l'arrivée, la partie est terminé !!!!");
                                    getJeu().setJeu(false);
                                    getPlateau().setPlateauVisible();
                                    System.out.println(getPlateau().toString());
                                }
                                else{
                                System.out.println("Vous avez avancé dans une case vide");
                                cjd.setTexture(" J ");
                                cj.setTexture("   ");
                                }
                            }
                        }

                        
                    }
                }
            }
                else
                System.out.println("Vous ne pouvez pas aller en bas à gauche");
        
        }      
    }


                                            // la grenade est perdu si il n'y a pas de mur, si elle n'est pas perdue, le joueur rentre dans la salle qu'il vient d'ouvrir (donc un acces est créé), si il y a une mine dedans, il meurt
    public void lanceGrenade(int a){        //4 possibilités, droite gauche haut bas
        int x = getXJoueur();
        int y = getYJoueur();
        if(a==1){ //lancer à droite
            if((y+1)<getPlateau().getTailleY() && getPlateau().getCase(x,y).isAccessible(1)){
                System.out.println("La case à droite de vous est déjà accessible, la grenade est donc perdu");
                getInventaire().setNbGrenade((getInventaire().getNbGrenade()-1));
            }
            else{
                if((y+1)<getPlateau().getTailleY() && "Mine".equals(getPlateau().getCase(x,y+1).lookContent())){
                    System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                    System.out.println("GAME OVER");
                    getJeu().setJeu(false);
                    getPlateau().setPlateauVisible();
                    System.out.println(getPlateau().toString());
                }

                else{
                    if((y+1)<getPlateau().getTailleY() && !getPlateau().getCase(x,y).isAccessible(1)){
                        getPlateau().getCase(x,y).setAccessible(1);
                        getInventaire().setNbGrenade((getInventaire().getNbGrenade()-1));
                        avance(1);

                    }

                    else
                        System.out.println("La case à droite de vous n'existe pas");
                    
                }
            }
        }

        if(a==2){ //lancer à gauche
            if((y-1)>=0 && getPlateau().getCase(x,y).isAccessible(2)){
                System.out.println("La case à gauche de vous est déjà accessible, la grenade est donc perdu");
                getInventaire().setNbGrenade((getInventaire().getNbGrenade()-1));
            }
            else{
                if((y-1)>=0 && "Mine".equals(getPlateau().getCase(x,y-1).lookContent())){
                    System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                    System.out.println("GAME OVER");
                    getJeu().setJeu(false);
                    getPlateau().setPlateauVisible();
                    System.out.println(getPlateau().toString());
                }

                else{
                    if((y-1)>=0 && !getPlateau().getCase(x,y).isAccessible(2)){
                        getPlateau().getCase(x,y).setAccessible(2);
                        getInventaire().setNbGrenade((getInventaire().getNbGrenade()-1));
                        avance(2);
                    }

                    else
                        System.out.println("La case à gauche de vous n'existe pas");
                    
                }
            }
        }

        if(a==3){ //lancer en haut
            if((x-1)>=0 && getPlateau().getCase(x,y).isAccessible(3)){
                System.out.println("La case en haut de vous est déjà accessible, la grenade est donc perdu");
                getInventaire().setNbGrenade((getInventaire().getNbGrenade()-1));
            }
            else{
                if((x-1)>=0 && "Mine".equals(getPlateau().getCase(x-1,y).lookContent())){
                    System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                    System.out.println("GAME OVER");
                    getJeu().setJeu(false);
                    getPlateau().setPlateauVisible();
                    System.out.println(getPlateau().toString());
                }

                else{
                    if((x-1)>=0 && !getPlateau().getCase(x,y).isAccessible(3)){
                        getPlateau().getCase(x,y).setAccessible(3);
                        getInventaire().setNbGrenade((getInventaire().getNbGrenade()-1));
                        avance(3);
                    }

                    else
                        System.out.println("La case en haut de vous n'existe pas");
                    
                }
            }
        }

        if(a==4){ //lancer en bas
            if((x+1)<getPlateau().getTailleX() && getPlateau().getCase(x,y).isAccessible(4)){
                System.out.println("La case en bas de vous est déjà accessible, la grenade est donc perdu");
                getInventaire().setNbGrenade((getInventaire().getNbGrenade()-1));
            }
            else{
                if((x+1)<getPlateau().getTailleX() && "Mine".equals(getPlateau().getCase(x+1,y).lookContent())){
                    System.out.println("OH NOOON !!! Vous avez avancé dans une case contenant une mine ! Elle a explosé et vous a tué... ");
                    System.out.println("GAME OVER");
                    getJeu().setJeu(false);
                    getPlateau().setPlateauVisible();
                    System.out.println(getPlateau().toString());
                }

                else{
                    if((x+1)<getPlateau().getTailleX() && !getPlateau().getCase(x,y).isAccessible(4)){
                        getPlateau().getCase(x,y).setAccessible(4);
                        getInventaire().setNbGrenade((getInventaire().getNbGrenade()-1));
                        avance(4);
                    }

                    else
                        System.out.println("La case en bas de vous n'existe pas");
                    
                }
            }
        }
    }
    
    public void setInventaire(Inventaire i){
        this.inventaire = i;
    }
    public Inventaire getInventaire(){
        return this.inventaire;
    }

    public String getSymbole(){
        return " J ";
    }
}
