#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <time.h>
#include <unistd.h>
#include <string.h>

#include "commun.h"
#include "ipc.h"
#include "journal.h"

#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/select.h>
#include <errno.h>


#define PORT_SERVEUR 4242


int nb_joueurs_actifs = 2;


int mode_reseau = 0;                 // 0=local, 1=réseau activé
int nb_joueurs_reseau = 0;           
int sock_joueur[NB_JOUEURS_MAX + 1]; // sock_joueur[id] si réseau, sinon -1

static int est_reseau(int id) {
    return mode_reseau && id >= 1 && id <= nb_joueurs_reseau;
}


int envoyer_joueur(int qid, int id_joueur, message_t *m);
int recevoir_joueur(int qid, int id_joueur, message_t *m);


//structures
typedef struct {
    uint8_t cartes[104];
    int index_pioche;
} paquet_t;

typedef struct {
    uint8_t cartes[6];
    uint8_t nb_cartes;
} rangee_t;

typedef struct {
    int id_joueur;
    uint8_t carte;
} tour_joueur_t;

paquet_t paquet;
rangee_t plateau[4];
int scores[NB_JOUEURS_MAX + 1] = {0};


static int recv_all(int sock, void *buf, size_t len)
{
    uint8_t *p = (uint8_t *)buf;
    size_t recu = 0;

    while (recu < len) {
        ssize_t n = recv(sock, p + recu, len - recu, 0);
        if (n == 0) return -1;   // connexion fermée
        if (n < 0) {
            if (errno == EINTR) continue;
            return -1;
        }
        recu += (size_t)n;
    }
    return 0;
}

static int send_all(int sock, const void *buf, size_t len)
{
    const uint8_t *p = (const uint8_t *)buf;
    size_t env = 0;

    while (env < len) {
        ssize_t n = send(sock, p + env, len - env, 0);
        if (n < 0) {
            if (errno == EINTR) continue;
            return -1;
        }
        if (n == 0) return -1;
        env += (size_t)n;
    }
    return 0;
}


int get_boeufs(uint8_t carte) {
    if (carte == 55) return 7;
    if (carte % 11 == 0) return 5;
    if (carte % 10 == 0) return 3;
    if (carte % 5 == 0) return 2;
    return 1;
}

int calculer_score_rangee(int index_rangee) {
    int total = 0;
    for (int i = 0; i < plateau[index_rangee].nb_cartes; i++) {
        total += get_boeufs(plateau[index_rangee].cartes[i]);
    }
    return total;
}

int compare_tours(const void *a, const void *b) {
    tour_joueur_t *t1 = (tour_joueur_t *)a;
    tour_joueur_t *t2 = (tour_joueur_t *)b;
    return (t1->carte - t2->carte);
}

void swap(uint8_t *a, uint8_t *b) {
    uint8_t temp = *a;
    *a = *b;
    *b = temp;
}

void initialiser_et_melanger_paquet() {
    for (int i = 0; i < 104; i++) paquet.cartes[i] = (uint8_t)(i + 1);
    paquet.index_pioche = 0;
    srand(time(NULL));
    for (int i = 103; i > 0; i--) {
        int j = rand() % (i + 1);
        swap(&paquet.cartes[i], &paquet.cartes[j]);
    }
    journal_printf("GESTIONNAIRE : Paquet mélangé.\n");
}

void initialiser_plateau() {
    for (int i = 0; i < 4; i++) {
        plateau[i].cartes[0] = paquet.cartes[paquet.index_pioche++];
        plateau[i].nb_cartes = 1;
        journal_printf("GESTIONNAIRE : Rangée %d init avec %d\n", i+1, plateau[i].cartes[0]);
    }
}

void log_plateau() {
    journal_printf("--- ÉTAT PLATEAU ---\n");
    for(int i=0; i<4; i++) {
        journal_printf("R%d: ", i+1);
        for(int j=0; j<plateau[i].nb_cartes; j++) {
            journal_printf("%d ", plateau[i].cartes[j]);
        }
        journal_printf("\n");
    }
    journal_printf("--------------------\n");
}


int envoyer_joueur(int qid, int id_joueur, message_t *m)
{
    m->mtype = MTYPE_JOUEUR(id_joueur);

    if (est_reseau(id_joueur)) {
        int s = sock_joueur[id_joueur];
        if (s < 0) return -1;
        return (send_all(s, m, sizeof(*m)) == 0) ? 0 : -1;
    }
    return ipc_envoyer(qid, m, 0);
}


int recevoir_joueur(int qid, int id_joueur, message_t *m)
{
    if (est_reseau(id_joueur)) {
        int s = sock_joueur[id_joueur];
        if (s < 0) return -1;
        return (recv_all(s, m, sizeof(*m)) == 0) ? 0 : -1;
    }

    // Local IPC : on attend un message correspondant à ce joueur
    while (1) {
        if (ipc_recevoir(qid, MTYPE_GESTIONNAIRE, m, 0) == -1)
            return -1;

        if ((m->u.action.code == ACT_JOUER_CARTE &&
             m->u.action.data.jouer.id_joueur == id_joueur)
            ||
            (m->u.action.code == ACT_CHOISIR_RANGEE &&
             m->u.action.data.choix.id_joueur == id_joueur))
        {
            return 0;
        }
        // sinon message d'un autre joueur : on continue
    }
}


void diffuser_environnement(int qid) {
    message_t m = {0};
    m.u.ordre.code = ORD_ENVIRONNEMENT;

    for(int i=0; i<4; i++) {
        m.u.ordre.data.env.nb_cartes[i] = plateau[i].nb_cartes;
        for(int j=0; j<plateau[i].nb_cartes; j++) {
            m.u.ordre.data.env.plateau[i][j] = plateau[i].cartes[j];
        }
    }
    for(int i=0; i<=NB_JOUEURS_MAX; i++) {
        m.u.ordre.data.env.scores[i] = scores[i];
    }

    for (int i = 1; i <= nb_joueurs_actifs; i++) {
        envoyer_joueur(qid, i, &m);
    }
}

void envoyer_main_joueur(int qid, int id_joueur, uint8_t *main_temp) {
    message_t m = {0};
    m.u.ordre.code = ORD_DISTRIBUTION;
    m.u.ordre.data.distribution.id_joueur = (uint8_t)id_joueur;
    m.u.ordre.data.distribution.nb_cartes = NB_CARTES_MANCHE;

    for (int i = 0; i < NB_CARTES_MANCHE; i++) {
        m.u.ordre.data.distribution.cartes[i] = main_temp[i];
    }
    envoyer_joueur(qid, id_joueur, &m);
}

int demander_choix_rangee(int qid, int id_joueur, uint8_t carte_jouee) {
    journal_printf("ACTION : Demande choix rangée à J%d (carte %d)\n", id_joueur, carte_jouee);

    message_t m = {0};
    m.u.ordre.code = ORD_DEMANDE_CHOIX_RANGEE;
    envoyer_joueur(qid, id_joueur, &m);

    message_t rep;
    while(1) {
        if (recevoir_joueur(qid, id_joueur, &rep) == -1) return 0;

        if (rep.u.action.code == ACT_CHOISIR_RANGEE &&
            rep.u.action.data.choix.id_joueur == id_joueur) {

            int choix = rep.u.action.data.choix.index_rangee;
            if (choix >= 0 && choix < 4) {
                journal_printf("RÉPONSE : J%d choisit la rangée %d\n", id_joueur, choix+1);
                return choix;
            }
        }
    }
}


void traiter_carte_jouee(int qid, int id_joueur, uint8_t carte) {
    int meilleur_index = -1;
    int diff_min = 1000;

    for (int i = 0; i < 4; i++) {
        uint8_t derniere = plateau[i].cartes[plateau[i].nb_cartes - 1];
        if (carte > derniere) {
            int diff = carte - derniere;
            if (diff < diff_min) {
                diff_min = diff;
                meilleur_index = i;
            }
        }
    }

    if (meilleur_index == -1) {
        int index_choisi = demander_choix_rangee(qid, id_joueur, carte);
        int pts = calculer_score_rangee(index_choisi);
        journal_printf("ACTION : J%d ramasse rangée %d (%d pts)\n", id_joueur, index_choisi+1, pts);
        scores[id_joueur] += pts;

        plateau[index_choisi].nb_cartes = 1;
        plateau[index_choisi].cartes[0] = carte;
    }
    else {
        if (plateau[meilleur_index].nb_cartes >= 5) {
            int boeufs = calculer_score_rangee(meilleur_index);
            journal_printf("ACTION : J%d prend la rangée %d (6e carte, %d pts)\n", id_joueur, meilleur_index+1, boeufs);
            scores[id_joueur] += boeufs;

            plateau[meilleur_index].nb_cartes = 1;
            plateau[meilleur_index].cartes[0] = carte;
        } else {
            int pos = plateau[meilleur_index].nb_cartes;
            plateau[meilleur_index].cartes[pos] = carte;
            plateau[meilleur_index].nb_cartes++;
            journal_printf("ACTION : J%d pose %d sur rangée %d\n", id_joueur, carte, meilleur_index+1);
        }
    }
}


static void serveur_accepter_joueurs_reseau(void)
{
    for (int i = 0; i <= NB_JOUEURS_MAX; i++) sock_joueur[i] = -1;

    int srv = socket(AF_INET, SOCK_STREAM, 0);
    if (srv < 0) { perror("socket"); exit(1); }

    int opt = 1;
    setsockopt(srv, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(PORT_SERVEUR);
    addr.sin_addr.s_addr = INADDR_ANY;

    if (bind(srv, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        perror("bind"); exit(1);
    }
    if (listen(srv, nb_joueurs_reseau) < 0) {
        perror("listen"); exit(1);
    }

    journal_printf("GESTIONNAIRE : attente de %d joueur(s) réseau sur port %d...\n",
                   nb_joueurs_reseau, PORT_SERVEUR);

    for (int id = 1; id <= nb_joueurs_reseau; id++) {
        int cli = accept(srv, NULL, NULL);
        if (cli < 0) { perror("accept"); exit(1); }

        sock_joueur[id] = cli;

        uint8_t id_octet = (uint8_t)id;
        if (send_all(cli, &id_octet, sizeof(id_octet)) == -1) {
            perror("send id joueur");
            exit(1);
        }
        journal_printf("GESTIONNAIRE : joueur réseau connecté, id=%d\n", id);
    }

    close(srv);
}


int main(int argc, char *argv[])
{
    // comment jouer : 
    //   ./gestionnaire N pour jouer en local (N = nb joueurs)
    //   ./gestionnaire N --net K pour jouer en réseau (N = nb joueurs et K = nb joueurs réseaux)

    if (argc >= 2) {
        nb_joueurs_actifs = atoi(argv[1]);
        if (nb_joueurs_actifs < 2 || nb_joueurs_actifs > NB_JOUEURS_MAX) {
            printf("Erreur : Le nombre de joueurs doit être entre 2 et %d\n", NB_JOUEURS_MAX);
            return 1;
        }
    }

    if (argc >= 3 && strcmp(argv[2], "--net") == 0) {
        mode_reseau = 1;
        nb_joueurs_reseau = (argc >= 4) ? atoi(argv[3]) : 1;
        if (nb_joueurs_reseau < 1) nb_joueurs_reseau = 1;
        if (nb_joueurs_reseau > nb_joueurs_actifs) nb_joueurs_reseau = nb_joueurs_actifs;
    } else {
        mode_reseau = 0;
        nb_joueurs_reseau = 0;
    }

    if (journal_init() == -1) return 1;
    journal_printf("--- GESTIONNAIRE : Nouvelle Partie (%d joueurs, %d réseau) ---\n",
                   nb_joueurs_actifs, nb_joueurs_reseau);

    int qid = ipc_creer_ou_ouvrir();
    if (qid == -1) { journal_fermer(); return 1; }

    if (mode_reseau) {
        serveur_accepter_joueurs_reseau();
    }

    initialiser_et_melanger_paquet();
    initialiser_plateau();
    sleep(1);

    // Distribution
    uint8_t mains[NB_JOUEURS_MAX + 1][NB_CARTES_MANCHE];
    for (int c = 0; c < NB_CARTES_MANCHE; c++) {
        for (int j = 1; j <= nb_joueurs_actifs; j++) {
            mains[j][c] = paquet.cartes[paquet.index_pioche++];
        }
    }
    for (int j = 1; j <= nb_joueurs_actifs; j++) {
        envoyer_main_joueur(qid, j, mains[j]);
    }
    sleep(1);

    // 10 tours
    for (int tour = 1; tour <= NB_CARTES_MANCHE; tour++) {
        journal_printf("\n=== TOUR %d ===\n", tour);

        log_plateau();
        diffuser_environnement(qid);
        sleep(1);

        // Demande jouer à tous
        message_t m_demande = {0};
        m_demande.u.ordre.code = ORD_DEMANDE_JOUER;
        for (int j = 1; j <= nb_joueurs_actifs; j++) {
            envoyer_joueur(qid, j, &m_demande);
        }

        // Réception : 1 carte par joueur (mix réseau + local)
        int recu[NB_JOUEURS_MAX + 1] = {0};
        int nb_reponses = 0;
        tour_joueur_t cartes_du_tour[NB_JOUEURS_MAX];

        while (nb_reponses < nb_joueurs_actifs) {

            //Lire tous les messages IPC disponibles (non bloquant)
            while (1) {
                message_t msg_ipc;
                if (ipc_recevoir(qid, MTYPE_GESTIONNAIRE, &msg_ipc, IPC_NOWAIT) == -1) {
                    break; // plus rien
                }
                if (msg_ipc.u.action.code == ACT_JOUER_CARTE) {
                    int id = msg_ipc.u.action.data.jouer.id_joueur;
                    if (id >= 1 && id <= nb_joueurs_actifs && !recu[id]) {
                        cartes_du_tour[nb_reponses].id_joueur = id;
                        cartes_du_tour[nb_reponses].carte = msg_ipc.u.action.data.jouer.carte;
                        recu[id] = 1;
                        nb_reponses++;
                        journal_printf("LOCAL: J%d joue %d\n", id, msg_ipc.u.action.data.jouer.carte);
                    }
                }
            }

            //Lire un (ou plusieurs) socket(s) réseau prêt(s)
            if (mode_reseau && nb_joueurs_reseau > 0) {
                fd_set rfds;
                FD_ZERO(&rfds);
                int maxfd = -1;

                for (int id = 1; id <= nb_joueurs_reseau; id++) {
                    if (!recu[id] && sock_joueur[id] >= 0) {
                        FD_SET(sock_joueur[id], &rfds);
                        if (sock_joueur[id] > maxfd) maxfd = sock_joueur[id];
                    }
                }

                if (maxfd >= 0) {
                    struct timeval tv;
                    tv.tv_sec = 0;
                    tv.tv_usec = 20000; // 20ms

                    int r = select(maxfd + 1, &rfds, NULL, NULL, &tv);
                    if (r > 0) {
                        for (int id = 1; id <= nb_joueurs_reseau; id++) {
                            int s = sock_joueur[id];
                            if (s >= 0 && !recu[id] && FD_ISSET(s, &rfds)) {
                                message_t msg_net;
                                if (recv_all(s, &msg_net, sizeof(msg_net)) == -1) {
                                    journal_printf("RESEAU: joueur %d déconnecté\n", id);
                                    recu[id] = 1;
                                    nb_reponses++;
                                    continue;
                                }
                                if (msg_net.u.action.code == ACT_JOUER_CARTE) {
                                    int jid = msg_net.u.action.data.jouer.id_joueur;
                                    if (jid >= 1 && jid <= nb_joueurs_actifs && !recu[jid]) {
                                        cartes_du_tour[nb_reponses].id_joueur = jid;
                                        cartes_du_tour[nb_reponses].carte = msg_net.u.action.data.jouer.carte;
                                        recu[jid] = 1;
                                        nb_reponses++;
                                        journal_printf("RESEAU: J%d joue %d\n", jid, msg_net.u.action.data.jouer.carte);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // évite de spin CPU si rien n'arrive
            usleep(1000);
        }

        // Traitement trié
        qsort(cartes_du_tour, nb_joueurs_actifs, sizeof(tour_joueur_t), compare_tours);
        for (int i = 0; i < nb_joueurs_actifs; i++) {
            traiter_carte_jouee(qid, cartes_du_tour[i].id_joueur, cartes_du_tour[i].carte);
        }
    }

    journal_printf("\n--- FIN_PARTIE_SCORES ---\n");
    for (int j = 1; j <= nb_joueurs_actifs; j++) {
        journal_printf("SCORE_FINAL_J%d: %d\n", j, scores[j]);
    }

    // Fin
    diffuser_environnement(qid);
    sleep(1);

    message_t m_fin = {0};
    m_fin.u.ordre.code = ORD_FIN_PARTIE;
    for (int j = 1; j <= nb_joueurs_actifs; j++) {
        envoyer_joueur(qid, j, &m_fin);
    }
    sleep(1);

    // Nettoyage
    if (mode_reseau) {
        for (int id = 1; id <= nb_joueurs_reseau; id++) {
            if (sock_joueur[id] >= 0) close(sock_joueur[id]);
        }
    }

    ipc_detruire(qid);
    journal_printf("--- FIN ---\n");
    journal_fermer();
    return 0;
}
