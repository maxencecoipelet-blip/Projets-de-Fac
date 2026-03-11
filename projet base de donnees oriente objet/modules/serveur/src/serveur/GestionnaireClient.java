package serveur;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class GestionnaireClient implements Runnable {
    private final Socket socket;
    private ObjectInputStream entree;
    private ObjectOutputStream sortie;
    private String nomUtilisateur;
    private final GestionnaireDonnees gestionnaireDonnees;
    private volatile boolean authentifie;

    public GestionnaireClient(Socket socket, GestionnaireDonnees gestionnaireDonnees) {
        this.socket = socket;
        this.gestionnaireDonnees = gestionnaireDonnees;
        this.authentifie = false;
    }

    @Override
    public void run() {
        try {
            sortie = new ObjectOutputStream(socket.getOutputStream());
            entree = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                String commandeAuth = (String) entree.readObject();
                int nbTentativeAuth = 0;

                switch (commandeAuth) {
                    case "INSCRIPTION" :
                        gestionInscription();
                        break;
                    case "AUTHENTIFICATION" :
                        gestionAuthentification(nbTentativeAuth);
                        nbTentativeAuth++;
                        break;
                    case "STOP" :
                        fermer();
                        printLogFormat("Déconnexion du client avant authentification.");
                        break;
                    default : sortie.writeObject("ERREUR: Commande INSCRIPTION ou AUTHENTIFICATION requise.");
                }
                sortie.flush();

                if(authentifie){
                    String commande;
                    while ((commande = (String) entree.readObject()) != null) {
                        gestionCommande(commande);
                    }
                }

                // Fermeture après 5 tentatives échouées
                if (nbTentativeAuth >= 5 && !authentifie) {
                    sortie.writeObject("Erreur : Trop de tentatives d'authentification. Connexion fermée.");
                    printLogFormat("L'utilisateur est déconnecté après trop de tentatives d'authentifications incorrectes.");
                    fermer();
                    break;
                }
            }

        } catch (IOException e) {
            if (!socket.isClosed()) {
                printLogFormat("L'utilisateur s'est déconnecté sans fermer le socket pendant l'authentification.");
            }
        } catch (ClassNotFoundException e) {
            if (!socket.isClosed()) {
                e.printStackTrace();
                System.err.println("Erreur : " + e.getMessage());
            }
        } finally {
            fermer();
        }
    }

    // Méthodes d'authentification et d'inscription inchangées
    private void gestionInscription() throws IOException, ClassNotFoundException {
        String nomUtilisateur = (String) entree.readObject();

        if(GestionnaireCompte.utilisateurExiste(nomUtilisateur)) {
            sortie.writeObject("UTILISATEUR ECHEC: Utilisateur existe déjà.");
            return;
        }
        sortie.writeObject("UTILISATEUR OK");

        String motDePasse = (String) entree.readObject();

        boolean succes = GestionnaireCompte.inscrireUtilisateur(nomUtilisateur, motDePasse);
        if (succes) {
            sortie.writeObject("INSCRIPTION OK");
            printLogFormat("L'utilisateur " + nomUtilisateur + " s'est inscrit.");
        } else {
            sortie.writeObject("INSCRIPTION ECHEC: Utilisateur existe déjà.");
        }
    }

    private void gestionAuthentification(int nbTentativeAuth) throws IOException, ClassNotFoundException {
        String nomUtilisateur = (String) entree.readObject();
        String motDePasse = (String) entree.readObject();

        if (GestionnaireCompte.verifierIdentifiants(nomUtilisateur, motDePasse)) {
            authentifie = true;
            this.nomUtilisateur = nomUtilisateur;
            sortie.writeObject("AUTHENTIFICATION OK");
            printLogFormat("L'utilisateur " + nomUtilisateur + " s'est authentifié.");
        } else {
            sortie.writeObject("AUTHENTIFICATION ECHEC: Tentative " + nbTentativeAuth);
        }
    }

    private void gestionCommande(String commande) throws IOException, ClassNotFoundException {
        printLogFormat("Commande recue : " + commande);

        switch (commande.toUpperCase()) {
            case "CREER": {
                String type = (String) entree.readObject();

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> listeMaps = (List<Map<String, Object>>) entree.readObject();

                if (gestionnaireDonnees.creer(type,listeMaps)) {
                    printLogFormat("L'utilisateur " + nomUtilisateur + " a créé une collection de "
                            + listeMaps.size() + " " + type);
                    sortie.writeObject("CREATION OK");
                } else {
                    printLogFormat("L'utilisateur " + nomUtilisateur + " a voulu créer une collection de "
                            + type + " qui échoue car elle existe déjà.");
                    sortie.writeObject("CREATION ECHEC");
                }
                sortie.flush();
                break;
            }
            case "LIRE": {
                String type = (String) entree.readObject();

                List<Map<String, Object>> listeMaps = gestionnaireDonnees.lire(type);

                sortie.writeObject(listeMaps);
                sortie.flush();

                if (listeMaps.isEmpty()) {
                    printLogFormat("L'utilisateur " + nomUtilisateur + " a voulu lire les objets de type " + type
                            + " qui ne sont pas enregistrés sur le serveur.");
                } else {
                    printLogFormat("L'utilisateur " + nomUtilisateur + " a lu les objets de type : " + type);
                }
                break;
            }
            case "AJOUTER": {
                String type = (String) entree.readObject();

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> listeMaps = (List<Map<String, Object>>) entree.readObject();

                if (gestionnaireDonnees.ajouter(type, listeMaps)) {
                    printLogFormat("L'utilisateur " + nomUtilisateur + " a ajouté une collection de "
                            + listeMaps.size() + " " + type);
                    sortie.writeObject("AJOUT OK");
                } else {
                    printLogFormat("L'utilisateur " + nomUtilisateur + " a voulu ajouter une collection de "
                            + type + " qui a été créée car elle n'existait pas.");
                    sortie.writeObject("AJOUT CREEE");
                }
                sortie.flush();
                break;
            }
            case "SUPPRIMER": {
                String type = (String) entree.readObject();

                @SuppressWarnings("unchecked")
                List<Integer> listeIdSupprimer = (List<Integer>) entree.readObject();

                int nbSuppression = gestionnaireDonnees.supprimer(type, listeIdSupprimer);

                sortie.writeObject("" + nbSuppression);
                sortie.flush();

                printLogFormat("L'utilisateur " + nomUtilisateur + " a supprimé " + nbSuppression + " " + type);
                break;
            }
            case "SUPPRIMER LISTE OBJETS": {
                String type = (String) entree.readObject();

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> listeMaps = (List<Map<String, Object>>) entree.readObject();

                int nbSuppression = gestionnaireDonnees.supprimerListeObj(type, listeMaps);

                sortie.writeObject("" + nbSuppression);
                sortie.flush();

                printLogFormat("L'utilisateur " + nomUtilisateur + " a supprimé " + nbSuppression + " " + type);
                break;
            }
            case "LIRE STRING": {
                String type = (String) entree.readObject();

                String stringListeObjets = gestionnaireDonnees.lireString(type);

                sortie.writeObject(stringListeObjets);
                sortie.flush();

                printLogFormat("L'utilisateur " + nomUtilisateur + " a lu les objets de type " + type + " en String");
                break;
            }
            case "LISTER": {
                String listeTypeObjet = gestionnaireDonnees.listerTypes();

                sortie.writeObject(listeTypeObjet);
                sortie.flush();

                printLogFormat("L'utilisateur " + nomUtilisateur + " a listé les types d'objets disponibles.");
                break;
            }
            case "STOP": {
                printLogFormat("L'utilisateur " + nomUtilisateur +" s'est déconnecté.");
                sortie.flush();
                fermer();
                break;
            }
            default:
                System.out.println("Commande inconnue reçue : " + commande);
                break;
        }
    }

    // Le reste reste identique
    private void printLogFormat(String message) {
        String dateTime = Serveur.getTime();
        String ip;
        if((socket != null && !socket.isClosed())){
            ip = socket.getInetAddress().getHostAddress();
        }
        else{
            ip = "IP inconnue";
        }

        String user;
        if(nomUtilisateur != null){
            user = nomUtilisateur;
        }
        else{
            user = "non-authentifié";
        }

        System.out.println( String.format("%s[%s][%s] : %s",
                dateTime,
                ip,
                user,
                message
        ));
    }

    public void fermer() {
        try {
            if (sortie != null) sortie.close();
            if (entree != null) entree.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
        }
    }
}