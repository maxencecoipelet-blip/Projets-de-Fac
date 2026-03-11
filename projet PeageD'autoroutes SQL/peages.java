import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.DriverManager;


public class peages {

    //on se connecte à la base de données
    private static Connection getConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/peages";
            String user = "postgres";
            String password = "Lollipop26!";
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Péages les plus empruntés
    private static void getPeagesEmpruntes(Connection connection) {
        String query = "SELECT nom, COUNT(*) AS nb_passages " +
                "FROM ticket t " +
                "INNER JOIN peage p ON t.peage_entree_id = p.id OR t.peage_sortie_id = p.id " +
                "GROUP BY nom " +
                "ORDER BY nb_passages DESC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("péages les plus empruntés :");
            while (resultSet.next()) {
                String nom = resultSet.getString("nom");
                int nbPassages = resultSet.getInt("nb_passages");
                System.out.println("Péage : " + nom + ", Passages : " + nbPassages);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Liste des trajets d’un utilisateur donné
    private static void getTrajetUtilisateur(Connection connection, int userId) {
        String query = "SELECT t.date_imprime, p1.nom AS peage_entree, p2.nom AS peage_sortie, t.cout " +
                "FROM ticket t " +
                "INNER JOIN peage p1 ON t.peage_entree_id = p1.id " +
                "INNER JOIN peage p2 ON t.peage_sortie_id = p2.id " +
                "WHERE t.automobiliste_id = " + userId;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("liste des trajets de cet automobiliste " + userId + " :");
            while (resultSet.next()) {
                String date = resultSet.getString("date_imprime");
                String peageEntree = resultSet.getString("peage_entree");
                String peageSortie = resultSet.getString("peage_sortie");
                double cout = resultSet.getDouble("cout");
                System.out.println("Date : " + date + ", Entrée : " + peageEntree + ", Sortie : " + peageSortie + ", Coût : " + cout);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Question 3 : Automobilistes non abonnés qui font souvent le même trajet
    private static void getNonAboTrajet(Connection connection) {
        String query = "SELECT a.nom, a.prenom, p1.nom AS peage_entree, p2.nom AS peage_sortie, COUNT(*) AS nb_trajets " +
                "FROM ticket t " +
                "INNER JOIN peage p1 ON t.peage_entree_id = p1.id " +
                "INNER JOIN peage p2 ON t.peage_sortie_id = p2.id " +
                "INNER JOIN automobiliste a ON t.automobiliste_id = a.id " +
                "LEFT JOIN badge b ON b.automobiliste_id = a.id " +
                "WHERE (b.id IS NULL OR b.statut = 'restitue') " +
                "GROUP BY a.nom, a.prenom, p1.nom, p2.nom " +
                "HAVING COUNT(*) > 1";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("automobilistes non abonnés qui font souvent le même trajet :");
            while (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String entree = resultSet.getString("peage_entree");
                String sortie = resultSet.getString("peage_sortie");
                int nbTrajets = resultSet.getInt("nb_trajets");
                System.out.println("Nom : " + nom + ", Prénom : " + prenom + ", Trajet : " + entree + " -> " + sortie + ", Nombre de trajets : " + nbTrajets);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Question 4 : Portions d’autoroute les plus empruntées
    private static void getAutorouteEmpruntes(Connection connection) {
        String query = "SELECT p1.nom AS peage_entree, p2.nom AS peage_sortie, COUNT(*) AS nb_trajets " +
                "FROM ticket t " +
                "INNER JOIN peage p1 ON t.peage_entree_id = p1.id " +
                "INNER JOIN peage p2 ON t.peage_sortie_id = p2.id " +
                "GROUP BY p1.nom, p2.nom " +
                "ORDER BY nb_trajets DESC";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("portions d'autoroute les plus empruntées :");
            while (resultSet.next()) {
                String entree = resultSet.getString("peage_entree");
                String sortie = resultSet.getString("peage_sortie");
                int nbTrajets = resultSet.getInt("nb_trajets");
                System.out.println("Entrée : " + entree + ", Sortie : " + sortie + ", Nombre de trajets : " + nbTrajets);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Question 5 : Automobilistes avec abonnement expiré mais encore un badge
    private static void getAboExpireBadge(Connection connection) {
        String query = "SELECT a.nom, a.prenom, b.id AS badge_id " +
                "FROM badge b " +
                "INNER JOIN abonnement ab ON b.abonnement_id = ab.id " +
                "INNER JOIN automobiliste a ON b.automobiliste_id = a.id " +
                "WHERE ab.date_fin < CURRENT_DATE";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("automobilistes avec un abonnement expiré mais qui possede encore un badge :");
            while (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                int badgeId = resultSet.getInt("badge_id");
                System.out.println("Nom : " + nom + ", Prénom : " + prenom + ", id du badge : " + badgeId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // On propose un menu pour choisir quelle requête choisir pour répondre aux 5 questions du sujet
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = getConnection()) {
            if (connection == null) {
                System.err.println("impossible de se connecter à la base de données.");
                return;
            }

            while (true) {
                System.out.println("Menu");
                System.out.println("1. quels sont les péages les plus empruntés ?"); //Question 1 du sujet
                System.out.println("2. liste des trajets d’un automobiliste donné"); //Question 2 du sujet
                System.out.println("3. automobilistes non abonnés qui font souvent le même trajet"); //Question 3 du sujet
                System.out.println("4. portions d’autoroute les plus empruntées"); //Question 4 du sujet
                System.out.println("5. automobilistes avec abonnement expiré mais encore un badge"); //Question 5 du sujet
                System.out.println("0. quitter");
                System.out.print("choisissez une option : ");

                int choix = scanner.nextInt();
                switch (choix) {
                    case 1:
                        getPeagesEmpruntes(connection);
                        break;
                    case 2:
                        System.out.print("entrez l'id de l'automobiliste : ");
                        int userId = scanner.nextInt();
                        getTrajetUtilisateur(connection, userId);
                        break;
                    case 3:
                        getNonAboTrajet(connection);
                        break;
                    case 4:
                        getAutorouteEmpruntes(connection);
                        break;
                    case 5:
                        getAboExpireBadge(connection);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("erreur");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



