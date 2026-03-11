#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#include "commun.h"


#define PORT_SERVEUR 4242
#define IP_SERVEUR   "127.0.0.1" //ip local 

//couleurs
#define RESET   "\033[0m"
#define RED     "\033[31m"
#define GREEN   "\033[32m"
#define YELLOW  "\033[33m"
#define BLUE    "\033[34m"
#define CYAN    "\033[36m"


static void die(const char *msg) {
    perror(msg);
    exit(EXIT_FAILURE);
}

static void clear_screen(void) {
    system("clear");
}

static int compare(const void *a, const void *b) {
    return (*(uint8_t *)a - *(uint8_t *)b);
}


static int recv_all(int sock, void *buf, size_t len) {
    uint8_t *p = buf;
    size_t recu = 0;

    while (recu < len) {
        ssize_t n = recv(sock, p + recu, len - recu, 0);
        if (n <= 0) return -1;
        recu += (size_t)n;
    }
    return 0;
}

//affichage
static void afficher_environnement(const environnement_t *env) {
    clear_screen();

    printf(YELLOW "\n================================================\n");
    printf("                ÉTAT DU PLATEAU                 \n");
    printf("================================================\n" RESET);

    for (int i = 0; i < 4; i++) {
        printf("Rangée %d : ", i + 1);
        for (int j = 0; j < env->nb_cartes[i]; j++) {
            printf("[%3d] ", env->plateau[i][j]);
        }
        if (env->nb_cartes[i] >= 5)
            printf(RED " <--- DANGER !" RESET);
        printf("\n");
    }

    printf(YELLOW "------------------------------------------------\n" RESET);
    printf(CYAN "SCORES : ");
    for (int j = 1; j <= NB_JOUEURS_MAX; j++) {
        printf("J%d = %d  ", j, env->scores[j]);
    }
    printf("\n" YELLOW "================================================\n\n" RESET);
}


int main(int argc, char **argv) {
    const char *ip   = (argc >= 2) ? argv[1] : IP_SERVEUR;
    int port         = (argc >= 3) ? atoi(argv[2]) : PORT_SERVEUR;

    //socket
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) die("socket");

    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port   = htons((uint16_t)port);
    if (inet_pton(AF_INET, ip, &addr.sin_addr) <= 0)
        die("inet_pton");

    if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) < 0)
        die("connect");

   
    uint8_t id_joueur;
    if (recv_all(sock, &id_joueur, sizeof(id_joueur)) == -1)
        die("recv id_joueur");

    printf(BLUE "\n=== JOUEUR RÉSEAU %d CONNECTÉ ===\n" RESET, id_joueur);

    uint8_t ma_main[NB_CARTES_MANCHE] = {0};
    int nb_cartes_restantes = 0;
    int derniers_scores[NB_JOUEURS_MAX + 1] = {0};

    message_t msg, rep;

    while (1) {
        if (recv_all(sock, &msg, sizeof(msg)) == -1) {
            printf("Connexion fermée par le serveur.\n");
            break;
        }

        switch (msg.u.ordre.code) {

            case ORD_DISTRIBUTION: {
                distrib_t *d = &msg.u.ordre.data.distribution;
                nb_cartes_restantes = d->nb_cartes;

                for (int i = 0; i < nb_cartes_restantes; i++)
                    ma_main[i] = d->cartes[i];

                qsort(ma_main, NB_CARTES_MANCHE, sizeof(uint8_t), compare);

                printf(GREEN "\n>>> Cartes distribuées !\n" RESET);
                break;
            }

            case ORD_ENVIRONNEMENT:
                memcpy(derniers_scores,
                       msg.u.ordre.data.env.scores,
                       sizeof(derniers_scores));
                afficher_environnement(&msg.u.ordre.data.env);
                break;

            case ORD_DEMANDE_JOUER: {
                printf(BLUE "\n--- À VOUS DE JOUER ---\n" RESET);
                printf("Vos cartes : " GREEN);
                for (int i = 0; i < NB_CARTES_MANCHE; i++) {
                    if (ma_main[i] != 0)
                        printf("[%d] ", ma_main[i]);
                }
                printf(RESET "\n");

                int choix = 0, index = -1;
                while (index == -1) {
                    printf("Votre choix > ");
                    if (scanf("%d", &choix) == 1) {
                        for (int i = 0; i < NB_CARTES_MANCHE; i++) {
                            if (ma_main[i] == choix) {
                                index = i;
                                break;
                            }
                        }
                    } else {
                        while (getchar() != '\n');
                    }
                    if (index == -1)
                        printf(RED "Carte invalide.\n" RESET);
                }

                memset(&rep, 0, sizeof(rep));
                rep.mtype = MTYPE_GESTIONNAIRE;
                rep.u.action.code = ACT_JOUER_CARTE;
                rep.u.action.data.jouer.id_joueur = id_joueur;
                rep.u.action.data.jouer.carte = (uint8_t)choix;

                send(sock, &rep, sizeof(rep), 0);

                ma_main[index] = 0;
                printf("Carte %d envoyée. En attente...\n", choix);
                break;
            }

            case ORD_DEMANDE_CHOIX_RANGEE: {
                printf(RED "\n!!! VOTRE CARTE EST TROP PETITE !!!\n" RESET);
                printf("Choisissez une rangée (1-4) > ");

                int r = 0;
                while (r < 1 || r > 4) {
                    scanf("%d", &r);
                    if (r < 1 || r > 4)
                        printf(RED "Invalide (1-4) > " RESET);
                }

                memset(&rep, 0, sizeof(rep));
                rep.mtype = MTYPE_GESTIONNAIRE;
                rep.u.action.code = ACT_CHOISIR_RANGEE;
                rep.u.action.data.choix.id_joueur = id_joueur;
                rep.u.action.data.choix.index_rangee = (uint8_t)(r - 1);

                send(sock, &rep, sizeof(rep), 0);
                break;
            }

            case ORD_FIN_PARTIE:
                printf(BLUE "\n========== PARTIE TERMINÉE ==========\n" RESET);
                for (int j = 1; j <= NB_JOUEURS_MAX; j++)
                    printf("J%d : %d pts\n", j, derniers_scores[j]);
                printf(BLUE "=====================================\n" RESET);
                close(sock);
                return 0;

            default:
                break;
        }
    }

    close(sock);
    return 0;
}
