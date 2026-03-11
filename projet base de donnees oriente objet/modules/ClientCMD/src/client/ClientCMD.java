package client;

import clientlib.ClientLibrairie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;
import java.util.LinkedHashMap;

public class ClientCMD {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        ClientLibrairie client = new ClientLibrairie();

        boolean connecte = false;

        // Saisie des informations de connexion
        while (!connecte) {
            System.out.print("Entrez l'adresse de l'hôte (ex: localhost) : ");
            String hote = scanner.nextLine();

            try {
                client.connecter(hote);
                connecte = true; // Si la connexion réussit, on sort de la boucle
            } catch (IOException e) {
                System.err.println("Erreur : " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }

        boolean authentifie = false;

        String choix ;
        String nomUtilisateur;
        String motDePasse;

        while (!authentifie) {
            choix = "";
            while(!choix.equalsIgnoreCase("INSCRIPTION") && !choix.equalsIgnoreCase("1") && !choix.equalsIgnoreCase("LOGIN") && !choix.equalsIgnoreCase("2")) {
                System.out.print("Tapez INSCRIPTION (1) ou LOGIN (2) : ");
                choix = scanner.nextLine().trim();
            }
            switch (choix.toUpperCase()) {
                case "1" : case "INSCRIPTION" :
                    nomUtilisateur = "";
                    boolean nomUtilisateurValide = false;

                    while (!nomUtilisateurValide) {
                        System.out.print("Entrez le nom d'utilisateur : ");
                        nomUtilisateur = scanner.nextLine();

                        if(nomUtilisateur.isEmpty()) {
                            System.out.print("Le nom d'utilisateur ne peut pas être vide. ");
                        }
                        else {
                            nomUtilisateurValide = !client.utilisateurExiste(nomUtilisateur);
                        }
                    }

                    motDePasse = "";

                    while (motDePasse.isEmpty()) {
                        System.out.print("Entrez le mot de passe : ");
                        motDePasse = scanner.nextLine();

                        if (motDePasse.isEmpty()) {
                            System.out.print("Le mot de passe ne peut pas être vide. ");
                        } else {
                            client.inscription(nomUtilisateur, motDePasse);
                        }
                    }
                    break;

                case "2" : case "LOGIN" :
                    nomUtilisateur = "";
                    motDePasse = "";

                    while (nomUtilisateur.isEmpty()) {
                        System.out.print("Entrez le nom d'utilisateur : ");
                        nomUtilisateur = scanner.nextLine();

                        if (nomUtilisateur.isEmpty()) {
                            System.out.print("Le nom d'utilisateur ne peut pas être vide. ");
                        }
                    }

                    while (motDePasse.isEmpty()) {
                        System.out.print("Entrez le mot de passe : ");
                        motDePasse = scanner.nextLine();

                        if (motDePasse.isEmpty()) {
                            System.out.print("Le mot de passe ne peut pas être vide. ");
                        }
                    }
                    
                    authentifie = client.authentification(nomUtilisateur, motDePasse);
                    break;
            }
        }

        String commande = "";

        afficherMenu();
        
        // Boucle du menu interactif
        while (!commande.equalsIgnoreCase("STOP")) {

            commande = scanner.nextLine();

            /* Découper la commande utilisateur en segments selon les espaces "\\s+" est
            une regex qui gère un ou plusieurs espaces (même multiples) */
            String[] parts = commande.split("\\s+");

            // Si la commande est vide (ex: entrée vide ou espaces), ignorer et redemander
            if (parts.length == 0) continue;

            /* Extraire le premier mot de la commande et le mettre en majuscules
               Permet une gestion case-insensitive des commandes (ex: "lire" == "LIRE") */
            String action = parts[0].toUpperCase();

            switch (action) {
                case "LIRE":
                    if (parts.length < 2) {
                        System.out.println("Usage: LIRE <nomObjet>");
                        break;
                    }
                    String typeLire = parts[1];
                    try {
                        String listeObjets = client.lireString(typeLire);
                        System.out.println(listeObjets);
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Erreur lors de la lecture: " + e.getMessage());
                    }
                    break;

                case "CREER":
                    if (parts.length < 2) {
                        System.out.println("Usage: CREER <nomObjet>");
                        break;
                    }
                    String typeObjet = parts[1];
                    try {
                        ArrayList<Object> objetsACreer = creerObjetsDynamiques(scanner, typeObjet);
                        if (!objetsACreer.isEmpty()) {
                            client.creer(objetsACreer, typeObjet);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la création: " + e.getMessage());
                    }
                    break;

                case "SUPPRIMER":
                    if (parts.length < 3) {
                        System.out.println("Usage: SUPPRIMER <nomObjet> <ID1> <ID2> ...");
                        break;
                    }
                    // Récupère le type d'objet à supprimer depuis le 2ème argument de la commande
                    // Ex: "SUPPRIMER Livre 1 2" → typeSupprimer = "Livre"
                    String typeSupprimer = parts[1];

                    // Initialise une liste pour stocker les IDs à supprimer (conversion String → Integer)
                    ArrayList<Integer> ids = new ArrayList<>();

                    // Parcourt les arguments suivants (à partir du 3ème élément) qui représentent les IDs
                    // Ex: "SUPPRIMER Livre 1 deux 3" → parts[2]="1", parts[3]="deux", parts[4]="3"
                    for (int i = 2; i < parts.length; i++) {
                        try {
                            int id = Integer.parseInt(parts[i]);
                            ids.add(id);
                        } catch (NumberFormatException e) {
                            // Gère le cas où l'argument n'est pas un nombre valide
                            System.err.println("ID invalide ignoré: " + parts[i]);
                        }
                    }
                    try {
                        client.supprimer(typeSupprimer, ids);
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Erreur lors de la suppression: " + e.getMessage());
                    }
                    break;

                case "LISTER":
                    try {
                        client.listerTypes();
                    } catch (IOException | ClassNotFoundException ex) {
                        System.err.println("Erreur lors du listage des types d'objet : " + ex.getMessage());
                    }
                    break;

                case "AIDE":
                    afficherMenu();
                    break;

                case "STOP":
                    client.close();
                    break;

                default:
                    System.out.println("Commande non reconnue. Les commandes valides sont LIRE, CREER, SUPPRIMER, LISTER, AIDE,  STOP.");
                    break;
            }
        }
        scanner.close();
    }

    private static void afficherMenu() {
        System.out.println("\n----------------------------------------------------\n" +
                "Commande : LIRE <nomObjet>\n" +
                "----------------------------------------------------\n" +
                "Description : \n" +
                "  Permet de récupérer et d'afficher les objets enregistrés correspondant \n" +
                "  au nom fourni." +
                "\n" +
                "----------------------------------------------------\n" +
                "Commande : CREER <nomObjet>\n" +
                "----------------------------------------------------\n" +
                "Description : \n" +
                "  Crée un ou plusieurs objets du type spécifié avec des attributs \n" +
                "  personnalisables (String, int, double ou boolean)." +
                "\n" +
                "----------------------------------------------------\n" +
                "Commande : SUPPRIMER <nomObjet> <ID1> <ID2> ...\n" +
                "----------------------------------------------------\n" +
                "Description : \n" +
                "  Supprime les objets du type spécifié dont les identifiants (ID) \n" +
                "  correspondent aux valeurs indiquées." +
                "\n" +
                "----------------------------------------------------\n" +
                "Commande : LISTER\n" +
                "----------------------------------------------------\n" +
                "Description : \n" +
                "  Liste tous les types d'objets présents sur le serveur." +
                "\n" +
                "----------------------------------------------------\n" +
                "Commande : AIDE\n" +
                "----------------------------------------------------\n" +
                "Description : \n" +
                "  Affiche ce menu." +
                "\n" +
                "----------------------------------------------------\n" +
                "Commande : STOP\n" +
                "----------------------------------------------------\n" +
                "Description : \n" +
                "  Termine la session en fermant la connexion avec le serveur.\n" +
                "----------------------------------------------------");
    }

    private static ArrayList<Object> creerObjetsDynamiques(Scanner scanner, String typeObjet) {
        ArrayList<Object> listeObjets = new ArrayList<>();

        System.out.println("Création d'objets de type: " + typeObjet);

        // Définir les attributs de l'objet
        ArrayList<String> nomAttributs = new ArrayList<>();
        ArrayList<String> typeAttributs = new ArrayList<>();

        boolean continuerDefinition = true;
        while (continuerDefinition) {
            System.out.print("Nom de l'attribut (ou 'fin' pour terminer la définition): ");
            String nomAttribut = scanner.nextLine().trim();

            if (nomAttribut.equalsIgnoreCase("fin")) {
                continuerDefinition = false;
                continue;
            }

            if (nomAttribut.isEmpty()) {
                System.out.println("Le nom de l'attribut ne peut pas être vide.");
                continue;
            }

            nomAttributs.add(nomAttribut);

            String typeAttribut;
            boolean typeValide = false;
            while (!typeValide) {
                System.out.print("Type de l'attribut (String, int, double, boolean): ");
                typeAttribut = scanner.nextLine().trim().toLowerCase();

                if (typeAttribut.equals("string") || typeAttribut.equals("int") ||
                        typeAttribut.equals("double") || typeAttribut.equals("boolean")) {
                    typeAttributs.add(typeAttribut);
                    typeValide = true;
                } else {
                    System.out.println("Type non valide. Veuillez choisir parmi String, int, double, boolean.");
                }
            }
        }

        if (nomAttributs.isEmpty()) {
            System.out.println("Aucun attribut défini. Création annulée.");
            return listeObjets;
        }

        // Création d'objets
        boolean continuerCreation = true;
        while (continuerCreation) {
            Map<String, Object> objet = new LinkedHashMap<>();

            System.out.println("\n--- Saisie des valeurs pour un nouvel objet ---");

            // Saisie des valeurs pour chaque attribut
            for (int i = 0; i < nomAttributs.size(); i++) {
                String nomAttribut = nomAttributs.get(i);
                String typeAttribut = typeAttributs.get(i);

                boolean valeurValide = false;
                while (!valeurValide) {
                    System.out.print(nomAttribut + " (" + typeAttribut + "): ");
                    String saisie = scanner.nextLine().trim();

                    try {
                        switch (typeAttribut) {
                            case "string":
                                objet.put(nomAttribut, saisie);
                                valeurValide = true;
                                break;

                            case "int":
                                objet.put(nomAttribut, Integer.parseInt(saisie));
                                valeurValide = true;
                                break;

                            case "double":
                                objet.put(nomAttribut, Double.parseDouble(saisie));
                                valeurValide = true;
                                break;

                            case "boolean":
                                if (saisie.equalsIgnoreCase("true") || saisie.equalsIgnoreCase("false") ||
                                        saisie.equals("1") || saisie.equals("0")) {
                                    objet.put(nomAttribut, Boolean.parseBoolean(saisie) || saisie.equals("1"));
                                    valeurValide = true;
                                } else {
                                    System.out.println("Veuillez entrer 'true', 'false', '1' ou '0'.");
                                }
                                break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Format incorrect. Veuillez réessayer.");
                    }
                }
            }

            listeObjets.add(objet);

            System.out.print("Voulez-vous créer un autre objet de ce type? (oui/non): ");
            String reponse = scanner.nextLine().trim().toLowerCase();
            continuerCreation = reponse.equals("oui") || reponse.equals("o") || reponse.equals("y") || reponse.equals("yes");
        }

        return listeObjets;
    }
}