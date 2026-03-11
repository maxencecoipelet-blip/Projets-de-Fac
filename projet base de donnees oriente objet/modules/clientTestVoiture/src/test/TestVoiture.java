package test;

import clientlib.ClientLibrairie;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class TestVoiture {
    public static void main(String[] args) {
        ArrayList<Object> voitures = new ArrayList<>();
        voitures.add(new Voiture("3008","Peugeot",130,7));
        voitures.add(new Voiture("Swift","Suzuki",90,4));
        voitures.add(new Voiture("Yaris","Toyota",120,5));
        voitures.add(new Voiture("Corolla","Toyota",140,5));

        try(ClientLibrairie clientLibrairie = ClientLibrairie.getConnection(args[0],args[1],args[2])) {
            //Création
            clientLibrairie.creer(voitures);
            ArrayList<Voiture> resultatServeurCreation = clientLibrairie.lire("Voiture",Voiture.class);
            System.out.println("Résultat création : " + resultatServeurCreation);

            //Suppression
            ArrayList<Integer> listeId = new ArrayList<>(); listeId.add(1); listeId.add(3);
            clientLibrairie.supprimer("Voiture",listeId);
            ArrayList<Voiture> resultatServeurSuppression = clientLibrairie.lire("Voiture",Voiture.class);
            System.out.println("Résultat suppression premier et troisième : " + resultatServeurSuppression);

            //Suppression ListeObjet
            clientLibrairie.supprimer(voitures);
            ArrayList<Voiture> resultatServeurSuppression2 = clientLibrairie.lire("Voiture",Voiture.class);
            System.out.println("Résultat suppression avec liste initiale de voitures : " + resultatServeurSuppression2);

            //Ajout
            clientLibrairie.ajouter(new Voiture("Picanto","Kia",80,5));
            ArrayList<Voiture> resultatServeurAjout = clientLibrairie.lire("Voiture",Voiture.class);
            System.out.println("Résultat ajout 1 voiture : " + resultatServeurAjout);

            //Ajout Liste
            clientLibrairie.ajouter(voitures);
            ArrayList<Voiture> resultatServeurAjout2 = clientLibrairie.lire("Voiture",Voiture.class);
            System.out.println("Résultat ajout 4 voiture : " + resultatServeurAjout2);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
class Voiture implements Serializable {
    public String nom;
    public String marque;
    public int nbChevaux;
    public int nbPlaces;

    public Voiture() {}

    public Voiture(String nom, String marque, int nbChevaux, int nbPlaces) {
        this.nom = nom;
        this.marque = marque;
        this.nbChevaux = nbChevaux;
        this.nbPlaces = nbPlaces;
    }

    @Override
    public String toString() {
        return "Voiture{" +
                "nom='" + nom + '\'' +
                ", marque='" + marque + '\'' +
                ", nbChevaux=" + nbChevaux +
                ", nbPlaces=" + nbPlaces +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voiture voiture = (Voiture) o;
        return nbChevaux == voiture.nbChevaux &&
                nbPlaces == voiture.nbPlaces &&
                Objects.equals(nom, voiture.nom) &&
                Objects.equals(marque, voiture.marque);
    }
}