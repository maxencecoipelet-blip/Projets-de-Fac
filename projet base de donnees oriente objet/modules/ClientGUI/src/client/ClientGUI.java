package client;

import clientlib.ClientLibrairie;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientGUI extends JFrame {
    private final ClientLibrairie client;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    // Couleurs thème - Mode sombre
    private final Color primaryColor = new Color(33, 33, 36); // Arrière-plan principal foncé
    private final Color secondaryColor = new Color(43, 43, 46); // Gris foncé pour les panneaux
    private final Color accentColor = new Color(86, 155, 189); // Bleu accent
    private final Color textColor = new Color(220, 220, 220); // Texte clair
    private final Color lightTextColor = new Color(190, 190, 190); // Texte secondaire
    private final Color errorColor = new Color(235, 87, 87); // Rouge erreur
    private final Color successColor = new Color(76, 175, 80); // Vert succès
    private final Color inputBgColor = new Color(55, 55, 60); // Fond des champs de saisie
    private final Color inputBorderColor = new Color(70, 70, 75); // Bordure des champs de saisie
    private Color cardBgColor = new Color(38, 38, 42); // Fond des cartes
    private final Color cardBorderColor = new Color(60, 60, 65); // Bordure des cartes
    private Color buttonHoverColor = new Color(100, 165, 195); // Survol bouton

    // Composants pour la connexion
    private JPanel connexionPanel;
    private JTextField hostField;
    private JButton connectButton;

    // Composants pour l'authentification
    private JPanel authPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel authStatusLabel;

    // Composants pour le menu principal
    private JPanel menuPanel;
    private JComboBox<String> commandeCombo;
    private JTextField objetField;
    private JTextField idsField;
    private JTextArea resultatArea;
    private JButton executeButton;
    private JButton logoutButton;

    public ClientGUI() {
        super("Client Bibliothèque");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Désactiver la fermeture par défaut
        setSize(800, 600);

        // Enlever la décoration par défaut pour un look plus personnalisé
        setUndecorated(false);

        // Icône de l'application (à remplacer par votre icône)
        //ImageIcon appIcon = createIcon("server", 64, 64);
        ImageIcon appIcon = setIcon("logo", 64, 64); // Sans l'extension .png

        if (appIcon != null) {
            setIconImage(appIcon.getImage());
        }

        client = new ClientLibrairie();

        // Initialisation du layout principal
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(primaryColor);

        // Création des panels
        createConnexionPanel();
        createAuthPanel();
        createMenuPanel();

        // Ajout des panels au panel principal avec CardLayout
        mainPanel.add(connexionPanel, "connexion");
        mainPanel.add(authPanel, "auth");
        mainPanel.add(menuPanel, "menu");

        // Afficher d'abord le panel de connexion
        cardLayout.show(mainPanel, "connexion");

        add(mainPanel);

        // Centre la fenêtre sur l'écran
        setLocationRelativeTo(null);

        // Applique l'apparence moderne
        applyLookAndFeel();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Fermer le client proprement
                client.close();
                // Fermer la fenêtre et quitter l'application
                dispose();
                System.exit(0);
            }
        });
    }

    private void applyLookAndFeel() {
        try {
            // Tentative d'utiliser FlatLaf si disponible
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception e) {
            try {
                // Fallback sur Nimbus si FlatLaf n'est pas disponible
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());

                        // Personnaliser Nimbus pour le mode sombre
                        UIManager.put("control", new Color(45, 45, 48));
                        UIManager.put("text", textColor);
                        UIManager.put("nimbusBase", new Color(38, 38, 42));
                        UIManager.put("nimbusFocus", accentColor);
                        UIManager.put("nimbusLightBackground", secondaryColor);
                        UIManager.put("nimbusSelectionBackground", accentColor);

                        break;
                    }
                }
            } catch (Exception ex) {
                // Si Nimbus échoue aussi, utiliser le look and feel par défaut du système
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception exc) {
                    // Ignorer les erreurs de look and feel
                }
            }
        }

        // Appliquer le look and feel
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void createConnexionPanel() {
        connexionPanel = new JPanel();
        connexionPanel.setLayout(new BorderLayout(10, 10));
        connexionPanel.setBackground(primaryColor);
        connexionPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Panel titre avec logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Logo
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = setIcon("logo", 128, 128);
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        }
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        // Titre
        JLabel titleLabel = new JLabel("Connexion au serveur", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textColor);

        headerPanel.add(logoLabel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Veuillez saisir l'adresse du serveur", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(lightTextColor);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        connexionPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulaire de connexion
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(secondaryColor);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new CompoundBorder(
                        new EmptyBorder(10, 50, 10, 50),
                        new CompoundBorder(
                                new LineBorder(cardBorderColor, 1, true),
                                new EmptyBorder(20, 20, 20, 20)
                        )
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Adresse hôte
        JLabel hostLabel = new JLabel("Adresse du serveur");
        hostLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hostLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hostLabel.setForeground(textColor);

        hostField = createStyledTextField("localhost");
        hostField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Bouton de connexion
        connectButton = createStyledButton("Se connecter");
        connectButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Afficher message d'état
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(errorColor);

        // Ajout des composants au formulaire
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(hostLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(hostField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(connectButton);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(statusLabel);

        connexionPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(primaryColor);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel footerLabel = new JLabel("© 2025 Bibliothèque Application", JLabel.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(130, 130, 130));
        footerPanel.add(footerLabel);

        connexionPanel.add(footerPanel, BorderLayout.SOUTH);

        // Action lors du clic sur le bouton Connecter
        connectButton.addActionListener(e -> {
            String hote = hostField.getText().trim();
            if (hote.isEmpty()) {
                statusLabel.setText("Veuillez entrer une adresse d'hôte");
                displayErrorAnimation(statusLabel);
                return;
            }

            connectButton.setEnabled(false);
            connectButton.setText("Connexion en cours...");

            try {
                client.connecter(hote);
                statusLabel.setText(" ");

                // Animation de transition
                Timer timer = new Timer(500, event -> {
                    connectButton.setEnabled(true);
                    connectButton.setText("Se connecter");
                    cardLayout.show(mainPanel, "auth");
                });
                timer.setRepeats(false);
                timer.start();

            } catch (UnknownHostException ex) {
                statusLabel.setText("Hôte introuvable : " + ex.getMessage());
                displayErrorAnimation(statusLabel);
                connectButton.setEnabled(true);
                connectButton.setText("Se connecter");
            } catch (ConnectException ex) {
                statusLabel.setText("Connexion impossible : " + ex.getMessage());
                displayErrorAnimation(statusLabel);
                connectButton.setEnabled(true);
                connectButton.setText("Se connecter");
            } catch (IOException ex) {
                statusLabel.setText("Erreur : " + ex.getMessage());
                displayErrorAnimation(statusLabel);
                connectButton.setEnabled(true);
                connectButton.setText("Se connecter");
            }
        });
    }

    private void createAuthPanel() {
        authPanel = new JPanel();
        authPanel.setLayout(new BorderLayout(10, 10));
        authPanel.setBackground(primaryColor);
        authPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Panel titre
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Authentification", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textColor);

        JLabel subtitleLabel = new JLabel("Connectez-vous ou créez un compte", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(lightTextColor);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        authPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulaire d'authentification
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(secondaryColor);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new CompoundBorder(
                        new EmptyBorder(10, 50, 10, 50),
                        new CompoundBorder(
                                new LineBorder(cardBorderColor, 1, true),
                                new EmptyBorder(20, 20, 20, 20)
                        )
                ),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Nom d'utilisateur
        JLabel usernameLabel = new JLabel("Nom d'utilisateur");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(textColor);

        usernameField = createStyledTextField("");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(textColor);

        passwordField = createStyledPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Panel boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(secondaryColor);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Boutons
        loginButton = createStyledButton("Se connecter");
        registerButton = createStyledButton("S'inscrire");
        styleButton(registerButton, new Color(66, 133, 142));

        // Statut
        authStatusLabel = new JLabel(" ");
        authStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        authStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        authStatusLabel.setForeground(errorColor);

        // Ajout des boutons
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(registerButton);

        // Ajout des composants au formulaire
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonPanel);

        // Panel dédié pour le message (avec hauteur fixe)
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(secondaryColor);
        statusPanel.setMinimumSize(new Dimension(0, 30)); // Hauteur minimale garantie
        statusPanel.setPreferredSize(new Dimension(0, 30));
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        authStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(authStatusLabel);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(statusPanel);

        authPanel.add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(primaryColor);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton backButton = new JButton("Retour");
        styleButton(backButton, new Color(80, 80, 85));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "connexion"));

        footerPanel.add(backButton);

        authPanel.add(footerPanel, BorderLayout.SOUTH);

        // Action pour le bouton Login
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty()) {
                authStatusLabel.setText("Le nom d'utilisateur ne peut pas être vide.");
                displayErrorAnimation(authStatusLabel);
                return;
            }
            if (password.isEmpty()) {
                authStatusLabel.setText("Le mot de passe ne peut pas être vide.");
                displayErrorAnimation(authStatusLabel);
                return;
            }

            loginButton.setEnabled(false);
            loginButton.setText("Connexion...");

            try {
                boolean authenticated = client.authentification(username, password);
                if (authenticated) {
                    authStatusLabel.setText("Connexion réussie !");
                    authStatusLabel.setForeground(successColor);

                    // Animation de transition
                    Timer timer = new Timer(800, event -> {
                        loginButton.setEnabled(true);
                        loginButton.setText("Se connecter");
                        cardLayout.show(mainPanel, "menu");
                        authStatusLabel.setText(" ");
                        menuPanel.revalidate(); // Rafraîchit le layout
                        menuPanel.repaint();
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    authStatusLabel.setText("Identifiants incorrects");
                    displayErrorAnimation(authStatusLabel);
                    loginButton.setEnabled(true);
                    loginButton.setText("Se connecter");
                }
            } catch (Exception ex) {
                authStatusLabel.setText("Erreur d'authentification: " + ex.getMessage());
                displayErrorAnimation(authStatusLabel);
                loginButton.setEnabled(true);
                loginButton.setText("Se connecter");
            }
        });

        // Action pour le bouton Register
        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty()) {
                authStatusLabel.setText("Le nom d'utilisateur ne peut pas être vide.");
                displayErrorAnimation(authStatusLabel);
                return;
            }
            if (password.isEmpty()) {
                authStatusLabel.setText("Le mot de passe ne peut pas être vide.");
                displayErrorAnimation(authStatusLabel);
                return;
            }

            registerButton.setEnabled(false);
            registerButton.setText("Inscription...");

            try {
                if (client.utilisateurExiste(username)) {
                    authStatusLabel.setText("Ce nom d'utilisateur existe déjà.");
                    displayErrorAnimation(authStatusLabel);
                    registerButton.setEnabled(true);
                    registerButton.setText("S'inscrire");
                    return;
                }

                client.inscription(username, password);
                authStatusLabel.setText("Inscription réussie !");
                authStatusLabel.setForeground(successColor);

                Timer timer = new Timer(1500, event -> {
                    registerButton.setEnabled(true);
                    registerButton.setText("S'inscrire");
                    authStatusLabel.setText("Vous pouvez vous connecter.");
                    authStatusLabel.setForeground(lightTextColor);
                });
                timer.setRepeats(false);
                timer.start();

            } catch (Exception ex) {
                authStatusLabel.setText("Erreur d'inscription: " + ex.getMessage());
                displayErrorAnimation(authStatusLabel);
                registerButton.setEnabled(true);
                registerButton.setText("S'inscrire");
            }
        });
    }

    private void createMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout(10, 10));
        menuPanel.setBackground(primaryColor);

        // Barre de titre avec logo
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(secondaryColor);
        titleBar.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Base de données objet | Serveur : " + hostField.getText());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textColor);

        // Bouton de déconnexion dans la barre de titre
        logoutButton = new JButton("Déconnexion");
        styleButton(logoutButton, new Color(80, 80, 85));
        logoutButton.setForeground(textColor);
        logoutButton.setBorder(new LineBorder(new Color(100, 100, 105), 1, true));
        logoutButton.setBorder(new CompoundBorder(
                new LineBorder(secondaryColor, 1, true),
                new EmptyBorder(5, 15, 5, 15) // Padding: haut, gauche, bas, droite
        ));

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(logoutButton, BorderLayout.EAST);

        menuPanel.add(titleBar, BorderLayout.NORTH);

        // Contenu principal (split en deux: commandes et résultats)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.4); // Donne 40% de l'espace au panneau de commandes
        splitPane.resetToPreferredSizes(); // Force le recalcul
        splitPane.setBackground(primaryColor);
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);

        // Panel de commandes
        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
        commandPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // Permet l'expansion
        commandPanel.setBackground(secondaryColor);
        commandPanel.setBorder(new CompoundBorder(
                new EmptyBorder(15, 15, 15, 15),
                new CompoundBorder(
                        new LineBorder(cardBorderColor, 1, true),
                        new EmptyBorder(15, 15, 15, 15)
                )
        ));

        // Titre du panel de commandes
        JLabel commandTitle = new JLabel("Commandes");
        commandTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        commandTitle.setForeground(textColor);
        commandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Type de commande
        JPanel commandTypePanel = new JPanel();
        commandTypePanel.setLayout(new BoxLayout(commandTypePanel, BoxLayout.X_AXIS));
        commandTypePanel.setBackground(secondaryColor);
        commandTypePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        commandTypePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel commandeLabel = new JLabel("Type de commande:");
        commandeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commandeLabel.setForeground(textColor);

        // Ajout de CREER aux commandes existantes
        String[] commandes = {"LIRE", "CREER", "SUPPRIMER","LISTER"};
        commandeCombo = new JComboBox<>(commandes);
        commandeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commandeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        commandeCombo.setBackground(secondaryColor);
        commandeCombo.setForeground(textColor);

        commandTypePanel.add(commandeLabel);
        commandTypePanel.add(Box.createHorizontalStrut(10));
        commandTypePanel.add(commandeCombo);

        // Paramètres de la commande
        JLabel objetLabel = new JLabel("Nom d'objet:");
        objetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        objetLabel.setForeground(textColor);
        objetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        objetField = createStyledTextField("");
        objetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        objetField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idsLabel = new JLabel("IDs (séparés par espace):");
        idsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idsLabel.setForeground(textColor);
        idsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        idsField = createStyledTextField("");
        idsField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        idsField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bouton d'exécution
        executeButton = createStyledButton("Exécuter");
        executeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        executeButton.setMaximumSize(new Dimension(200, 40));

        // Ajout des composants au panel de commandes
        commandPanel.add(commandTitle);
        commandPanel.add(Box.createVerticalStrut(15));
        commandPanel.add(commandTypePanel);
        commandPanel.add(Box.createVerticalStrut(15));
        commandPanel.add(objetLabel);
        commandPanel.add(Box.createVerticalStrut(5));
        commandPanel.add(objetField);
        commandPanel.add(Box.createVerticalStrut(15));
        commandPanel.add(idsLabel);
        commandPanel.add(Box.createVerticalStrut(5));
        commandPanel.add(idsField);
        commandPanel.add(Box.createVerticalStrut(20));
        commandPanel.add(executeButton);
        commandPanel.add(Box.createVerticalGlue()); // Pousse les éléments vers le haut

        // Panel de résultats
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBackground(secondaryColor);
        resultPanel.setBorder(new CompoundBorder(
                new EmptyBorder(15, 15, 15, 15),
                new CompoundBorder(
                        new LineBorder(cardBorderColor, 1, true),
                        new EmptyBorder(15, 15, 15, 15)
                )
        ));

        // Titre du panel de résultats
        JLabel resultTitle = new JLabel("Résultats");
        resultTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultTitle.setForeground(textColor);

        // Zone de résultat
        resultatArea = new JTextArea();
        resultatArea.setEditable(false);
        resultatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultatArea.setBackground(inputBgColor);
        resultatArea.setForeground(textColor);
        resultatArea.setBorder(new LineBorder(inputBorderColor, 1));
        resultatArea.setCaretColor(textColor);

        JScrollPane scrollPane = new JScrollPane(resultatArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400, 250)); // Taille de départ plus grande
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(inputBgColor);

        resultPanel.add(resultTitle, BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Ajout aux panels principaux
        splitPane.setTopComponent(commandPanel);
        splitPane.setBottomComponent(resultPanel);

        menuPanel.add(splitPane, BorderLayout.CENTER);

        // Barre de statut
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(secondaryColor);
        statusBar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, cardBorderColor),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel statusLabel = new JLabel("Prêt");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(lightTextColor);

        statusBar.add(statusLabel, BorderLayout.WEST);

        menuPanel.add(statusBar, BorderLayout.SOUTH);

        // Modifier l'état des champs en fonction de la commande choisie
        commandeCombo.addActionListener(e -> {
            String selectedCommand = (String) commandeCombo.getSelectedItem();
            if ("LISTER".equals(selectedCommand)) {
                // Désactiver tous les champs
                objetLabel.setEnabled(false);
                objetField.setEnabled(false);
                objetField.setBackground(new Color(55, 55, 60, 120));
                idsLabel.setEnabled(false);
                idsField.setEnabled(false);
                idsField.setBackground(new Color(55, 55, 60, 120));
            } else if ("LIRE".equals(selectedCommand) || "CREER".equals(selectedCommand)) {
                // Désactiver uniquement les IDs
                idsLabel.setEnabled(false);
                idsField.setEnabled(false);
                idsField.setBackground(new Color(55, 55, 60, 120));
                // Réactiver le champ objet
                objetLabel.setEnabled(true);
                objetField.setEnabled(true);
                objetField.setBackground(inputBgColor);
            } else { // SUPPRIMER
                // Activer tous les champs
                objetLabel.setEnabled(true);
                objetField.setEnabled(true);
                objetField.setBackground(inputBgColor);
                idsLabel.setEnabled(true);
                idsField.setEnabled(true);
                idsField.setBackground(inputBgColor);
            }
        });

        // Déclencher l'action pour initialiser l'état
        commandeCombo.setSelectedItem("LIRE");

        // Action lors du clic sur le bouton Exécuter
        executeButton.addActionListener(e -> {
            String commande = (String) commandeCombo.getSelectedItem();
            String objet = objetField.getText().trim();

            if (!"LISTER".equals(commande) && objet.isEmpty()) {
                resultatArea.setText("Erreur: Le nom d'objet ne peut pas être vide.");
                return;
            }

            executeButton.setEnabled(false);
            executeButton.setText("En cours...");
            statusLabel.setText("Exécution de la commande " + commande + "...");

            if ("LIRE".equals(commande)) {
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        String listeObjets = client.lireString(objet);
                        return listeObjets.isEmpty() ?
                                "Aucun objet trouvé de type: " + objet :
                                listeObjets;
                    }

                    @Override
                    protected void done() {
                        try {
                            String result = get();
                            resultatArea.setText(result);
                            statusLabel.setText("Prêt");
                        } catch (Exception ex) {
                            resultatArea.setText("Erreur d'exécution: " + ex.getMessage());
                            statusLabel.setText("Erreur");
                        } finally {
                            executeButton.setEnabled(true);
                            executeButton.setText("Exécuter");
                        }
                    }
                };
                worker.execute();
            } else if ("SUPPRIMER".equals(commande)) {
                String idsText = idsField.getText().trim();
                if (idsText.isEmpty()) {
                    resultatArea.setText("Erreur: Aucun ID spécifié pour la suppression.");
                    executeButton.setEnabled(true);
                    executeButton.setText("Exécuter");
                    statusLabel.setText("Prêt");
                    return;
                }

                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        String[] idParts = idsText.split("\\s+");
                        ArrayList<Integer> ids = new ArrayList<>();

                        for (String idPart : idParts) {
                            try {
                                int id = Integer.parseInt(idPart);
                                ids.add(id);
                            } catch (NumberFormatException ex) {
                                return "Erreur: ID invalide - " + idPart;
                            }
                        }

                        client.supprimer(objet, ids);
                        return "Suppression effectuée avec succès pour les objets de type " + objet + " avec IDs: " + idsText;
                    }

                    @Override
                    protected void done() {
                        try {
                            String result = get();
                            resultatArea.setText(result);
                            statusLabel.setText("Prêt");
                        } catch (Exception ex) {
                            resultatArea.setText("Erreur d'exécution: " + ex.getMessage());
                            statusLabel.setText("Erreur");
                        } finally {
                            executeButton.setEnabled(true);
                            executeButton.setText("Exécuter");
                        }
                    }
                };
                worker.execute();
            } else if ("CREER".equals(commande)) {
                // Lancement de la création via une boîte de dialogue
                ArrayList<Object> objetsACreer = creerObjetsDialog(objet);
                if (objetsACreer.isEmpty()) {
                    resultatArea.setText("Aucun objet créé.");
                    executeButton.setEnabled(true);
                    executeButton.setText("Exécuter");
                    statusLabel.setText("Prêt");
                    return;
                }
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        client.creer(objetsACreer, objet);
                        return "Création effectuée avec succès pour " + objetsACreer.size() + " objet(s) de type " + objet;
                    }

                    @Override
                    protected void done() {
                        try {
                            String result = get();
                            resultatArea.setText(result);
                            statusLabel.setText("Prêt");
                        } catch (Exception ex) {
                            resultatArea.setText("Erreur d'exécution: " + ex.getMessage());
                            statusLabel.setText("Erreur");
                        } finally {
                            executeButton.setEnabled(true);
                            executeButton.setText("Exécuter");
                        }
                    }
                };
                worker.execute();
            } else if ("LISTER".equals(commande)) {
                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        // Appeler la méthode listerTypes() sans utiliser les champs
                        return client.listerTypes();
                    }

                    @Override
                    protected void done() {
                        try {
                            String result = get();
                            resultatArea.setText(result.isEmpty() ?
                                    "Aucun type d'objet enregistré." :
                                    "Types disponibles : " + result
                            );
                            statusLabel.setText("Prêt");
                        } catch (Exception ex) {
                            resultatArea.setText("Erreur : " + ex.getMessage());
                            statusLabel.setText("Erreur");
                        } finally {
                            executeButton.setEnabled(true);
                            executeButton.setText("Exécuter");
                        }
                    }
                };
                worker.execute();
            }
        });

        // Action pour le bouton de déconnexion
        logoutButton.addActionListener(e -> {
            client.close();
            // Réinitialiser les champs
            usernameField.setText("");
            passwordField.setText("");
            objetField.setText("");
            idsField.setText("");
            resultatArea.setText("");
            authStatusLabel.setText(" ");

            // Revenir à l'écran de connexion
            cardLayout.show(mainPanel, "connexion");
        });
    }

    // Méthode qui affiche des boîtes de dialogue pour créer dynamiquement des objets
    // Méthode qui affiche des boîtes de dialogue pour créer dynamiquement des objets
    private ArrayList<Object> creerObjetsDialog(String typeObjet) {
        ArrayList<Object> listeObjets = new ArrayList<>();
        ArrayList<String> nomAttributs = new ArrayList<>();
        ArrayList<String> typeAttributs = new ArrayList<>();

        // Définir les attributs de l'objet via des dialogues
        while (true) {
            String nomAttribut = JOptionPane.showInputDialog(this, "Entrez le nom de l'attribut (ou laissez vide pour terminer) :", "Définition d'attributs", JOptionPane.QUESTION_MESSAGE);
            if (nomAttribut == null || nomAttribut.trim().isEmpty()) {
                break;
            }
            nomAttribut = nomAttribut.trim();

            // Boîte de dialogue avec des boutons pour choisir le type
            String[] typesDisponibles = {"String", "int", "double", "boolean"};
            int choixType = JOptionPane.showOptionDialog(
                    this,
                    "Choisissez le type pour l'attribut '" + nomAttribut + "'",
                    "Sélection du type",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    typesDisponibles,
                    typesDisponibles[0]
            );

            if (choixType == -1) {
                continue; // Annulation, passer à l'attribut suivant
            }

            String typeAttribut = typesDisponibles[choixType].toLowerCase();
            nomAttributs.add(nomAttribut);
            typeAttributs.add(typeAttribut);
        }

        if (nomAttributs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun attribut défini. Création annulée.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return listeObjets;
        }

        // Saisie des valeurs pour chaque objet
        do {
            Map<String, Object> objet = new LinkedHashMap<>();
            for (int i = 0; i < nomAttributs.size(); i++) {
                String attrName = nomAttributs.get(i);
                String attrType = typeAttributs.get(i);
                boolean valeurValide = false;
                while (!valeurValide) {
                    String saisie = JOptionPane.showInputDialog(this, "Entrez la valeur pour '" + attrName + "' (" + attrType + ") :", "Saisie de valeurs", JOptionPane.QUESTION_MESSAGE);
                    if (saisie == null) {
                        saisie = "";
                    }
                    try {
                        Object value;
                        switch (attrType) {
                            case "string":
                                value = saisie;
                                break;
                            case "int":
                                value = Integer.parseInt(saisie);
                                break;
                            case "double":
                                value = Double.parseDouble(saisie);
                                break;
                            case "boolean":
                                if (saisie.equalsIgnoreCase("true") || saisie.equals("1"))
                                    value = true;
                                else if (saisie.equalsIgnoreCase("false") || saisie.equals("0"))
                                    value = false;
                                else
                                    throw new NumberFormatException();
                                break;
                            default:
                                value = saisie;
                                break;
                        }
                        objet.put(attrName, value);
                        valeurValide = true;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Format incorrect pour '" + attrName + "'. Veuillez réessayer.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            listeObjets.add(objet);
            int choix = JOptionPane.showConfirmDialog(this, "Voulez-vous créer un autre objet de type " + typeObjet + " ?", "Créer un autre objet", JOptionPane.YES_NO_OPTION);
            if (choix != JOptionPane.YES_OPTION) {
                break;
            }
        } while (true);

        return listeObjets;
    }

    // Méthodes utilitaires pour les composants UI
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(inputBgColor);
        field.setForeground(textColor);
        field.setBorder(new CompoundBorder(
                new LineBorder(inputBorderColor, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        field.setCaretColor(textColor);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(inputBgColor);
        field.setForeground(textColor);
        field.setBorder(new CompoundBorder(
                new LineBorder(inputBorderColor, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        field.setCaretColor(textColor);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, accentColor);
        return button;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(bgColor.darker(), 1, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        button.setBorder(new CompoundBorder(
                new LineBorder(bgColor.darker(), 1, true),
                new EmptyBorder(5, 15, 5, 15) // Padding: haut, gauche, bas, droite
        ));
    }

    private ImageIcon setIcon(String name, int width, int height) {
        try {
            // Chemin corrigé : "/icons/nom_fichier" (sans extension)
            String iconPath = "/icons/" + name + ".png";
            java.net.URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } else {
                // Fallback si l'image n'est pas trouvée, appel à createIcon
                System.err.println("Fichier introuvable : " + iconPath);
                return createIcon("server", width, height);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Méthode pour créer des icônes en cas d'absence d'image
    private ImageIcon createIcon(String name, int width, int height) {
        try {
            String iconPath = "/icons/" + name + ".png";
            java.net.URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } else {
                // Création d'une icône générique
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, width, height, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, width / 4));
                g2d.drawString("B", width / 3, height / 2 + height / 10);
                g2d.dispose();
                return new ImageIcon(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Animation pour les messages d'erreur
    private void displayErrorAnimation(JLabel label) {
        label.setForeground(errorColor);
        // Animation de secousse
        final int originalX = label.getLocation().x;
        final Timer timer = new Timer(30, null);
        final int[] offset = {-5, 5, -4, 4, -3, 3, -2, 2, -1, 1, 0};
        final int[] index = {0};
        timer.addActionListener(e -> {
            if (index[0] < offset.length) {
                label.setLocation(originalX + offset[index[0]], label.getLocation().y);
                index[0]++;
            } else {
                timer.stop();
                label.setLocation(originalX, label.getLocation().y);
            }
        });
        timer.start();
    }


    public Color getCardBgColor() {
        return cardBgColor;
    }

    public void setCardBgColor(Color cardBgColor) {
        this.cardBgColor = cardBgColor;
    }

    public Color getButtonHoverColor() {
        return buttonHoverColor;
    }

    public void setButtonHoverColor(Color buttonHoverColor) {
        this.buttonHoverColor = buttonHoverColor;
    }

    public static void main(String[] args) {
        // Configurer l'apparence sur l'EDT
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            } catch (Exception e) {
                try {
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception ex) {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception exc) {
                        // Ignorer l'erreur
                    }
                }
            }
            ClientGUI app = new ClientGUI();
            app.setVisible(true);
        });
    }
}
