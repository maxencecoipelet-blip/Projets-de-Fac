package serveur;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GestionnaireCompte {
    /* La variable static assure un stockage unique des utilisateurs, partagé par toutes les connexions.
    Cela évite la duplication des données et permet un accès global sans créer plusieurs instances de la classe*/

    private static final String FICHIER_COMPTE = "compte.ser";
    private static Map<String, String> comptes;
    private static final String REPERTOIRE_DONNEES = "comptes";

    static {
            File repertoire = new File(REPERTOIRE_DONNEES);
            if (!repertoire.exists()) {
                repertoire.mkdirs();
            }
            comptes = chargerComptes();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> chargerComptes() {
        if(!new File(REPERTOIRE_DONNEES + "/" + FICHIER_COMPTE).exists()) {
            return new ConcurrentHashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(REPERTOIRE_DONNEES + "/" + FICHIER_COMPTE))) {
            return (Map<String, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aucun utilisateur chargé ou erreur : " + e.getMessage());
            return null;
        }
    }

    // Hacher un mot de passe avec SHA-256
    private static String hacherMotDePasse(String motDePasse) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(motDePasse.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hachage", e);
        }
    }

    // Inscription d'un nouvel utilisateur
    public static boolean inscrireUtilisateur(String nomUtilisateur, String motDePasse) {
        if (utilisateurExiste(nomUtilisateur)) {
            return false; // L'utilisateur existe déjà
        }
        comptes.put(nomUtilisateur, hacherMotDePasse(motDePasse));
        return sauvegarderComptes();
    }

    public static boolean utilisateurExiste(String nomUtilisateur) {
        return comptes.containsKey(nomUtilisateur);
    }

    // Vérification des identifiants lors de la connexion
    public static boolean verifierIdentifiants(String nomUtilisateur, String motDePasse) {
        return comptes.containsKey(nomUtilisateur) &&
                comptes.get(nomUtilisateur).equals(hacherMotDePasse(motDePasse));
    }

    // Sauvegarder les comptes dans un fichier
    private static boolean sauvegarderComptes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REPERTOIRE_DONNEES + "/" + FICHIER_COMPTE))) {
            oos.writeObject(comptes);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur de sauvegarde des comptes : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
