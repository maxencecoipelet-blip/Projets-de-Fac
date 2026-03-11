package serveur;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Serveur {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println(getTime() + " Démarrage du serveur");

        try {
            InetAddress adresseServeur = InetAddress.getLocalHost();
            System.out.println("Nom de l'hôte : " + adresseServeur.getHostName());
            System.out.println("Adresse IP : " + adresseServeur.getHostAddress());
        }catch (UnknownHostException e) {
            System.out.println("Impossible d'obtenir l'adresse IP du serveur");
            e.printStackTrace();
        }

        try (ServerSocket socketServeur = new ServerSocket(PORT)) {
            System.out.println("Le serveur écoute sur le port " + PORT);
            GestionnaireDonnees gestionnaireDonnees = new GestionnaireDonnees("data");
            while (true) {
                Socket socket = socketServeur.accept();
                System.out.println(getTime() + " Nouveau client en cours de connexion depuis :" + socket.getInetAddress().getHostName()+" "+socket);
                new Thread(new GestionnaireClient(socket, gestionnaireDonnees)).start();  // Lancer un thread pour gérer le client
            }
        } catch (IOException ex) {
            System.out.println("Erreur serveur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String getTime(){
        return "[" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "]";
    }
}