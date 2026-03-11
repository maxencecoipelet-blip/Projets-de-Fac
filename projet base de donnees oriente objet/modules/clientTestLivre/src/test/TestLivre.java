package test;

import clientlib.ClientLibrairie;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/***  Test de lecture d'objets Livres créees en mode CMD à éxécuter après avoir créee des objets
 *  de nom Livre avec les attributs nom(String), auteur(String), prix(double) ***/
public class TestLivre {
    public static void main(String[] args) {
        try(ClientLibrairie clientLibrairie = ClientLibrairie.getConnection(args[0],args[1],args[2])) {
            //Lecture
            ArrayList<Livre> resultatServeur = clientLibrairie.lire("Livre",Livre.class);
            System.out.println("Résultat lecture après avoir lu des objets en mode commande : " + resultatServeur);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
class Livre implements Serializable {
    public String nom;
    public String auteur;
    public double prix;

    public Livre() {}

    public Livre(String nom, String auteur, double prix) {
        this.nom = nom;
        this.auteur = auteur;
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Livre{" +
                "nom='" + nom + '\'' +
                ", auteur='" + auteur + '\'' +
                ", prix=" + prix +
                '}';
    }
}