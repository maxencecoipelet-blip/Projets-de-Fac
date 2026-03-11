package projetinfo2a;

public class Main {

    public static void main(String[] args){
    
    int lignes;
    int colonnes;
    String nom;
    
    
        System.out.println("Veuillez choisir un nom");  // Choisit le nom
        nom = Lire.S();
        do{
            System.out.println(nom+", veuillez choisir la hauteur du plateau en donnant un nombre impair de lignes");  // Choisit le nombre de lignes
            lignes = Lire.i();
        }while(lignes == 0 || lignes%2 == 0);
    
        do{
            System.out.println("Veuillez maintenant donner un nombre impair de colonnes pour la largeur");  //Choisit le nombre de colonnes
            colonnes = Lire.i();
        }while(colonnes == 0 || colonnes%2 == 0);
   
    Jeu jeu = new Jeu(true);
    Plateau p = new Plateau(lignes,colonnes,jeu);
    Joueur j = new Joueur(nom,lignes,colonnes,jeu);
    jeu.setPlateau(p);
    int jx = lignes/2;
    int jy = colonnes/2;
    j.setXJoueur(jx);
    j.setYJoueur(jy);
    p.getCase(j.getXJoueur(),j.getYJoueur()).setTexture(j.getSymbole());
    p.getCase(j.getXJoueur(),j.getYJoueur()).setVisible(true);
    p.setJoueur(j);
    jeu.joue();
    
        }
    }



