package projetinfo2a;

import java.util.ArrayList;


public class Inventaire {
    private int nbEnergie;
    private int nbGrenade;
    private ArrayList<Outil> outils;

    public Inventaire(){
        this.nbEnergie = 20;
        this.nbGrenade = 10;
        this.outils = new ArrayList<Outil>();
    }

    //Getters Setters
    public ArrayList<Outil> getOutils(){
        return this.outils;
    }
    
    public int getNbEnergie(){
        return this.nbEnergie;
    }

    public int getNbGrenade(){
        return this.nbGrenade;
    }

    public void setNbEnergie(int nb){
        this.nbEnergie = nb;
    }

    public void setNbGrenade(int nb){
        this.nbGrenade = nb;
    }

    public void ajoutOutil(Outil o) {
        // Parcourir la liste des outils pour voir si un outil du même type est déjà présent
        for (Outil outilExist : this.outils) {
            if (outilExist.getClass().equals(o.getClass())) {
                System.out.println("Vous possédez déjà cet outil");
                return; // Sortir de la méthode si un outil similaire est trouvé
            }
        }
        // Ajouter l'outil à la liste si aucun outil similaire n'a été trouvé
        this.outils.add(o);
    }

    public void removeOutil(Outil o){
        this.outils.remove(o);
    }

    public void addGrenade(int nb){
        this.nbGrenade += nb;
    }

    public void addEnergie(int nb){
        this.nbEnergie += nb;
    }

    public Outil getOutil(int i){
        return this.outils.get(i);
    }

    public Outil getOutil (String nomdeoutil){                         //Permet de recolter un outil
        if(nomdeoutil.equals("UniteDeminage")){
            return new UniteDeminage();
        }
        if(nomdeoutil.equals("DetecteurMines")){
            return new DetecteurMines();
        }
        if(nomdeoutil.equals("DetecteurMassique")){
            return new DetecteurMassique();
        }
        if(nomdeoutil.equals("ScannerUnidirectionnel")){
            return new ScannerUnidirectionnel();
        }
        if(nomdeoutil.equals("ScannerOmnidirectionnel")){
            return new ScannerOmnidirectionnel();
        }
        if(nomdeoutil.equals("Excavatrice")){
            return new Excavatrice();
        }

        else{
            System.out.println("erreur");
            return null;
            
        }
    }

    public String printOutils(){
        String s = "";
        for (int i=0; i<this.outils.size(); i++){
            s += getOutil(i).toString() + ", ";
        }
        return s;
    }

    public String toString(){
        return "Voici votre inventaire :"+"\n"+"Energie : " + this.nbEnergie + ", Grenades : " + this.nbGrenade + "\n" + "Outils : " + this.printOutils();
    }

    public boolean aDejaOutil(Outil o) {
        
        for (int i = 0; i < getOutils().size(); i++) {
            if (getOutil(i).getClass().equals(o.getClass())) {
                return true;  
            }
        }
        return false;  
    }
}
