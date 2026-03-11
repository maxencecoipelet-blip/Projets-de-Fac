package projetinfo2a;
public class Jeu {

    private boolean isJeu = true;
    private Plateau plateau; 

    public Plateau getPlateau(){
        return this.plateau;
    }

    public void setPlateau(Plateau p){
        this.plateau=p;
    }

    public Joueur getJoueur(){
        return this.getPlateau().getJoueur();
    }
    public void setJeu(boolean b) {
        this.isJeu = b;
    }

    public boolean isJeu() {
        return this.isJeu;
    }

    public Jeu(boolean b){
        this.isJeu = b;
    }

    public void joue(){
        Inventaire i = getJoueur().getInventaire();
        i.ajoutOutil((Outil) new ScannerUnidirectionnel());
        i.ajoutOutil((Outil) new DetecteurMines());
        i.ajoutOutil((Outil) new ScannerOmnidirectionnel());
        while(this.isJeu){
            System.out.println(this.getPlateau().toString());
            
            int choix;
            do{
                System.out.println("Choisissez une action:");
                System.out.println("1 - Déplacer");
                System.out.println("2 - Utiliser un outil");
                System.out.println("3 - Lancer une grenade");
                System.out.println("4 - Quitter le jeu");
                System.out.println("5 - Rendre le plateau visible");
                System.out.println("6 - Cacher le plateau");
                choix=Lire.i();
            }while(choix < 1 || choix > 6);

               
                switch(choix){

                case 1 : //déplacement
                int mouv=0;
                do{System.out.println("Ou voulez-vous vous déplacer ? 1- Droite, 2- Gauche, 3- Haut, 4- Bas 5- Haut Droite 6- Bas Droite 7- Haut Gauche 8- Bas Gauche");
                mouv=Lire.i();
                }   while(mouv < 1 || mouv > 8);
                getJoueur().avance(mouv);

                break;
            
                case 2 : //utiliser un outil 
                
                System.out.println();
                System.out.println();
                System.out.println(getJoueur().getInventaire().toString());
                System.out.println();
                int o=0;
                do{System.out.println("Quel outil voulez-vous utiliser ? (vous ne les avez pas forcément tous)"+"\n"+"1 pour Détecteur Massique "+ "\n" + "2 pour Détecteur de mines"+"\n"+"3 pour Excavatrice"+"\n"+"4 pour Scanner Omnidirectionnel"+"\n"+"5 pour Scanner Unidirectionnel"+"\n"+"6 pour Unité de déminage");
                o = Lire.i();} while(o<1 || o>6);
                
                if(o==1){
                    Outil ou = new DetecteurMassique();
                    if(getJoueur().getInventaire().aDejaOutil(ou))
                        ou.utilise(getJoueur());
                    else
                        System.out.println("Vous n'avez pas cet outil voyons !");
                    
                }

                if(o==2){
                    Outil ou = new DetecteurMines();
                    if(getJoueur().getInventaire().aDejaOutil(ou))
                        ou.utilise(getJoueur());
                    else
                        System.out.println("Vous n'avez pas cet outil voyons !");
                    
                }

                if(o==3){
                    Outil ou = new Excavatrice();
                    if(getJoueur().getInventaire().aDejaOutil(ou))
                        ou.utilise(getJoueur());
                    else
                        System.out.println("Vous n'avez pas cet outil voyons !");
                    
                }

                if(o==4){
                    Outil ou = new ScannerOmnidirectionnel();
                    if(getJoueur().getInventaire().aDejaOutil(ou))
                        ou.utilise(getJoueur());
                    else
                        System.out.println("Vous n'avez pas cet outil voyons !");
                    
                }

                if(o==5){
                    Outil ou = new ScannerUnidirectionnel();
                    if(i.aDejaOutil(ou)) 
                        ou.utilise(getJoueur());
                    else
                        System.out.println("Vous n'avez pas cet outil voyons !");
                    
                }

                if(o==6){
                    Outil ou = new UniteDeminage();
                    if(getJoueur().getInventaire().aDejaOutil(ou))
                        ou.utilise(getJoueur());
                    else
                        System.out.println("Vous n'avez pas cet outil voyons !");
                }
                break;

                case 3 : //lancer une grenade
                System.out.println(i.toString());
                int lg;
                do{
                    System.out.println("Dans quelle direction voules-vous lancer une grenade ?(1-Droite, 2-Gauche, 3-Haut, 4-Bas)");
                    lg=Lire.i();
                }while(lg<1 || lg>4);
                if(lg==1)
                    getJoueur().lanceGrenade(1);
                if(lg==2)
                    getJoueur().lanceGrenade(2);
                if(lg==3)
                    getJoueur().lanceGrenade(3);
                if(lg==4)
                    getJoueur().lanceGrenade(4);

                break;
                case 4 : //fin du jeu
                System.out.println("Merci d'avoir joué !");
                    setJeu(false);
                break;

                case 5 : //rendre le plateau visible
                System.out.println("Le plateau est désormais visible");
                getPlateau().setPlateauVisible();
                break;

                case 6 : //rend le plateau totalement invisible (attention)
                System.out.println("Le plateau est désormais caché");
                getPlateau().setPlateauInvisible();
                break;


            }
        }
    }
}


