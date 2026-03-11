package projetinfo2a;

abstract public class Outil {
    private int conso;
    private String symbole;
    private String descriptif;
    private String nom;
    
    public int getConsommationEnergetique()
        {return this.conso;}

    protected void setConsommationEnergetique(int conso)
        {this.conso = conso;}

    public String getDescriptif()
        {return this.descriptif;}

    protected void setDescriptif(String descriptif)
        {this.descriptif = descriptif;}

    public boolean isUtilisablePar(Joueur j)
    {
        if(j.getInventaire().getNbEnergie()<getConsommationEnergetique()){
            System.out.println("Vous n'avez pas assez d'énergie pour utiliser cet outil");
            return false;
        }
        else{
            return true;
        }
        
    }
   

    public void utilise(Joueur j)
    {
        if(this.isUtilisablePar(j)){
            this.activation(j);
            int a = j.getInventaire().getNbEnergie()-getConsommationEnergetique();
            j.getInventaire().setNbEnergie(a);
        }
        /*
            Utilisation de l'outil. Il faut vérifier que le joueur a assez d'énergie (Sinon, faire un message d'erreur),
            activer l'outil (différemment suivant l'outil) et diminuer l'énergie du joueur en conséquence
        */
    }
    public String getSymbole() {
        return  this.symbole;
    }
    public void setSymbole(String symbole) {
        this.symbole = symbole;
    }

    public String getNom() {
        return this.nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }


    public abstract void activation(Joueur j); // activation spécifique à chaque outil

    public Outil(String symbole,String nom, String descriptif, int conso)
    {
        setSymbole(symbole);
        setNom(nom);
        this.setDescriptif(descriptif);
        this.setConsommationEnergetique(conso);
    }

    public String toString() {
    return getNom();
    }

    public String printSymbole() {
        return getSymbole();
    }



    @Override
    public boolean equals(Object autre)
    {
        if (this == autre) return true;
        if (autre == null) return false;
        if (getClass() != autre.getClass())return false;
        return true;
    }
}

