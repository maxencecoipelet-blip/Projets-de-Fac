package clientlib;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientLibrairie implements AutoCloseable {
    private Socket socket;
    private ObjectOutputStream sortie;
    private ObjectInputStream entree;
    private final ObjectMapper objectMapper;

    public ClientLibrairie() {
        this.objectMapper = new ObjectMapper();
    }

    public static ClientLibrairie getConnection(String hote, String nomUtilisateur, String motDePasse) throws IOException, ClassNotFoundException {
        ClientLibrairie client = new ClientLibrairie();
        client.connecter(hote);

        if (!client.authentification(nomUtilisateur, motDePasse)) {
            client.close();
            throw new IOException("Erreur : Échec de l'authentification pour l'utilisateur " + nomUtilisateur);
        }
        return client;
    }

    public void connecter(String hote) throws IOException {
        try {
            InetAddress.getByName(hote);
        } catch (UnknownHostException e) {
            throw new IOException("Hôte introuvable : " + hote, e);
        }

        socket = new Socket(hote, 8080);
        sortie = new ObjectOutputStream(socket.getOutputStream());
        entree = new ObjectInputStream(socket.getInputStream());
    }

    // Les méthodes d'authentification restent inchangées
    public boolean utilisateurExiste(String nomUtilisateur) throws IOException, ClassNotFoundException {
        sortie.writeObject("INSCRIPTION");

        sortie.writeObject(nomUtilisateur);
        sortie.flush();

        String reponse = (String) entree.readObject();

        if ("UTILISATEUR OK".equals(reponse)) {
            return false;
        } else {
            System.out.println("Le nom d'utilisateur " + nomUtilisateur + " est déjà utilisé, veuillez en choisir un autre.");
            return true;
        }
    }

    public boolean inscription(String nomUtilisateur, String motDePasse) throws IOException, ClassNotFoundException {
        sortie.writeObject(motDePasse);
        sortie.flush();

        String reponse = (String) entree.readObject();

        if ("INSCRIPTION OK".equals(reponse)) {
            System.out.println("Inscription réussie pour " + nomUtilisateur);
            return true;
        } else {
            return false;
        }
    }

    public boolean authentification(String nomUtilisateur, String motDePasse) throws IOException, ClassNotFoundException {
        sortie.writeObject("AUTHENTIFICATION");

        sortie.writeObject(nomUtilisateur);
        sortie.writeObject(motDePasse);
        sortie.flush();

        String reponseAuth = (String) entree.readObject();

        if ("AUTHENTIFICATION OK".equals(reponseAuth)) {
            System.out.println("Authentification réussie pour " + nomUtilisateur);
            return true;
        } else {
            return false;
        }
    }

    // Nouvelle méthode pour convertir un objet Java en Map
    private Map<String, Object> convertToMap(Object obj) {
        // Ajoute le type de l'objet pour pouvoir l'identifier côté serveur
        return objectMapper.convertValue(obj, Map.class);
    }

    // Nouvelle méthode pour convertir une Map générique en objet Java
    private <T> T convertToObject(Map<String, Object> map, Class<T> clazz) {
        // Retirer le champ "type" ajouté par le serveur
        map.remove("type");
        return objectMapper.convertValue(map, clazz);
    }

    private void isConnectionValide() {
        if (socket == null) {
            throw new IllegalStateException("Erreur : Le client n'est pas connecté au serveur. Veuillez vous connecter en appelant <getConnection(String hote, String nomUtilisateur, String motDePasse)>.");
        }
        if (socket.isClosed()) {
            throw new IllegalStateException("Erreur : La connection avec le serveur a été fermée.");
        }
        if (sortie == null && entree == null) {
            throw new IllegalStateException("Erreur : Les flux de communications n'ont pas été initialisés.");
        }
    }

    public boolean creer(ArrayList<Object> listeObjets, String type) throws IOException, ClassNotFoundException {
        isConnectionValide();
        if (listeObjets.isEmpty()) {
            System.out.println("Erreur : La liste d'objets à créer est vide.");
            return false;
        }

        // Conversion des objets Java en Map
        List<Map<String, Object>> listeMaps = new ArrayList<>();
        for (Object obj : listeObjets) {
            listeMaps.add(convertToMap(obj));
        }

        sortie.writeObject("CREER");
        sortie.flush();

        sortie.writeObject(type);
        sortie.flush();

        sortie.writeObject(listeMaps);
        sortie.flush();

        String reponse = (String) entree.readObject();
        if ("CREATION OK".equals(reponse)) {
            System.out.println("Création de " + listeObjets.size() + " " + type + " réussie.");
            return true;
        }
        System.out.println("Erreur : ( Serveur : " + reponse + " ) \n" +
                "Création de " + listeObjets.size() + " " + type + " échouée, ce type " +
                "d'objet existe déja sur le serveur. Veuillez ajouter des objets avec la méthode ajouter.");
        return false;
    }

    public boolean creer(ArrayList<Object> listeObjets) throws IOException, ClassNotFoundException {
        String type = listeObjets.get(0).getClass().getSimpleName();
        return creer(listeObjets, type);
    }

    public <T> ArrayList<T> lire(String type, Class<T> clazz) throws IOException, ClassNotFoundException {
        isConnectionValide();

        sortie.writeObject("LIRE");
        sortie.flush();

        sortie.writeObject(type);
        sortie.flush();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> listeMaps = (List<Map<String, Object>>) entree.readObject();

        ArrayList<T> listeObjets = new ArrayList<>();
        for (Map<String, Object> map : listeMaps) {
            listeObjets.add(convertToObject(map, clazz));
        }

        if (listeObjets.isEmpty()) {
            System.out.println("Erreur : Aucun objet de type " + type + " n'est enregistré sur le serveur.");
        } else {
            System.out.println("Lecture de " + listeObjets.size() + " objet(s) de type " + type);
        }
        return listeObjets;
    }

    public <T> ArrayList<T> lire(Object objetReference, Class<T> clazz) throws IOException, ClassNotFoundException {
        return lire(objetReference.getClass().getSimpleName(), clazz);
    }

    public boolean ajouter(ArrayList<Object> nouveauxObjets) throws IOException, ClassNotFoundException {
        isConnectionValide();

        if (nouveauxObjets.isEmpty()) {
            System.out.println("Erreur : La liste d'objets à ajouter est vide.");
            return false;
        }

        // Conversion des objets Java en Map
        List<Map<String, Object>> listeMaps = new ArrayList<>();
        for (Object obj : nouveauxObjets) {
            listeMaps.add(convertToMap(obj));
        }

        sortie.writeObject("AJOUTER");
        sortie.flush();

        String type = nouveauxObjets.get(0).getClass().getSimpleName();
        sortie.writeObject(type);
        sortie.flush();

        sortie.writeObject(listeMaps);
        sortie.flush();

        String reponse = (String) entree.readObject();
        if ("AJOUT OK".equals(reponse)) {
            System.out.println("Ajout de " + nouveauxObjets.size() + " " + nouveauxObjets.get(0).getClass().getSimpleName() + " réussie.");
            return true;
        }
        System.out.println("Erreur : (Serveur : " + reponse + " ) \n" +
                "Ajout de " + nouveauxObjets.size() + " " + nouveauxObjets.get(0).getClass().getSimpleName() + "échouée. La collection a tout de même étée créée");
        return false;
    }

    public boolean ajouter(Object nouveauObjet) throws IOException, ClassNotFoundException {
        ArrayList<Object> nouveauxObjets = new ArrayList<>();
        nouveauxObjets.add(nouveauObjet);
        return ajouter(nouveauxObjets);
    }

    public int supprimer(String type, ArrayList<Integer> listeIdSupprimer) throws IOException, ClassNotFoundException {
        isConnectionValide();

        if (listeIdSupprimer.isEmpty()) {
            System.out.println("Erreur : La liste d'IDs à supprimer est vide.");
            return 0;
        }

        Set<Integer> set = new LinkedHashSet<>(listeIdSupprimer);
        listeIdSupprimer = new ArrayList<>(set);
        listeIdSupprimer.sort(Collections.reverseOrder());

        sortie.writeObject("SUPPRIMER");
        sortie.flush();

        sortie.writeObject(type);
        sortie.flush();

        sortie.writeObject(listeIdSupprimer);
        sortie.flush();

        int nbSuppression = Integer.parseInt((String) entree.readObject());
        System.out.println("Suppression de " + nbSuppression + " " + type + ".");

        return nbSuppression;
    }

    public int supprimer(ArrayList<Object> listeObjetsSupprimer) throws IOException, ClassNotFoundException {
        isConnectionValide();

        if (listeObjetsSupprimer.isEmpty()) {
            System.out.println("Erreur : La liste d'objets à supprimer est vide.");
            return 0;
        }

        // Conversion des objets Java en Map
        List<Map<String, Object>> listeMaps = new ArrayList<>();
        for (Object obj : listeObjetsSupprimer) {
            listeMaps.add(convertToMap(obj));
        }

        sortie.writeObject("SUPPRIMER LISTE OBJETS");
        sortie.flush();

        String type = listeObjetsSupprimer.get(0).getClass().getSimpleName();
        sortie.writeObject(type);
        sortie.flush();

        sortie.writeObject(listeMaps);
        sortie.flush();

        int nbSuppression = Integer.parseInt((String) entree.readObject());
        System.out.println("Suppression de " + nbSuppression + " " + listeObjetsSupprimer.get(0).getClass().getSimpleName() + ".");

        return nbSuppression;
    }

    public String lireString(String type) throws IOException, ClassNotFoundException {
        isConnectionValide();

        sortie.writeObject("LIRE STRING");
        sortie.flush();

        sortie.writeObject(type);
        sortie.flush();

        String stringListeObjets = (String) entree.readObject();

        if (stringListeObjets.isEmpty()) {
            System.out.println("Erreur : Aucun objet de type " + type + " n'est enregistré sur le serveur.");
        } else {
            System.out.println("Lecture de(s) objet(s) de type " + type + " en String.");
        }
        return stringListeObjets;
    }

    public String listerTypes() throws IOException, ClassNotFoundException {
        isConnectionValide();

        sortie.writeObject("LISTER");
        sortie.flush();

        String listeTypeObjet = (String) entree.readObject();

        if (listeTypeObjet.isEmpty()) {
            System.out.println("Aucun objet enregistré sur le serveur.");
            return "";
        } else {
            System.out.println("Types disponibles : " + listeTypeObjet);
            return listeTypeObjet;
        }
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            try {
                sortie.writeObject("STOP");
                sortie.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            if (sortie != null) {
                sortie.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture de sortieObjet :");
            e.printStackTrace();
        }

        try {
            if (entree != null) {
                entree.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture de entreeObjet :");
            e.printStackTrace();
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture de socket :");
            e.printStackTrace();
        }
    }
}