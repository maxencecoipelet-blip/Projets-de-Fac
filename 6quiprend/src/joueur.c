#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "commun.h"
#include "ipc.h"
#include "journal.h"

#define RESET   "\033[0m"
#define RED     "\033[31m"
#define GREEN   "\033[32m"
#define YELLOW  "\033[33m"
#define BLUE    "\033[34m"
#define CYAN    "\033[36m"

void clear_screen() {
    system("clear");
}

int compare(const void *a, const void *b) {
    return (*(uint8_t *)a - *(uint8_t *)b);
}

int main(int argc, char *argv[]) {
    if (argc < 2) { printf("Usage: %s <id>\n", argv[0]); return 1; }
    int id_joueur = atoi(argv[1]);

    if (journal_init() == -1) return 1;
    
    int qid = ipc_creer_ou_ouvrir();
    if (qid == -1) return 1;

    clear_screen();
    printf(BLUE "\n=== JOUEUR %d CONNECTÉ ===\n" RESET, id_joueur);
    printf("En attente...\n");

    uint8_t ma_main[NB_CARTES_MANCHE] = {0}; 
    int nb_cartes_restantes = 0;
    
    // Pour mémoriser les scores jusqu'à la fin
    int derniers_scores[NB_JOUEURS_MAX+1] = {0};

    message_t msg;

    while (1) {
        if (ipc_recevoir(qid, MTYPE_JOUEUR(id_joueur), &msg, 0) == -1) break;

        switch (msg.u.ordre.code) {
            
            case ORD_DISTRIBUTION:
                nb_cartes_restantes = msg.u.ordre.data.distribution.nb_cartes;
                for (int i=0; i<nb_cartes_restantes; i++) 
                    ma_main[i] = msg.u.ordre.data.distribution.cartes[i];
                qsort(ma_main, NB_CARTES_MANCHE, sizeof(uint8_t), compare);
                printf(GREEN "\n>>> Cartes distribuées !\n" RESET);
                break;

            case ORD_ENVIRONNEMENT:
                clear_screen();
                
                // Mémorisation des scores pour la fin
                derniers_scores[1] = msg.u.ordre.data.env.scores[1];
                derniers_scores[2] = msg.u.ordre.data.env.scores[2];

                printf(YELLOW "\n================================================\n");
                printf("                ÉTAT DU PLATEAU                 \n");
                printf("================================================\n" RESET);
                
                
                for(int i=0; i<4; i++) {
                    uint8_t nb = msg.u.ordre.data.env.nb_cartes[i];
                    
                    printf("Rangée %d : ", i+1);
                    for(int j=0; j<nb; j++) {
                        // On affiche chaque carte présente dans la rangée
                        printf("[%3d] ", msg.u.ordre.data.env.plateau[i][j]);
                    }

                    if (nb >= 5) printf(RED " <--- DANGER !" RESET);
                    printf("\n");
                }
                
                printf(YELLOW "------------------------------------------------\n" RESET);
                printf(CYAN "SCORES : J1 = %d pts | J2 = %d pts\n" RESET, 
                       derniers_scores[1], derniers_scores[2]);
                printf(YELLOW "================================================\n" RESET);
                break;

            case ORD_DEMANDE_JOUER:
                printf(BLUE "\n--- A VOUS DE JOUER ---\n" RESET);
                printf("Vos cartes : " GREEN);
                for (int i=0; i<NB_CARTES_MANCHE; i++) {
                    if (ma_main[i] != 0) printf("[%d] ", ma_main[i]);
                }
                printf(RESET "\n");
                
                int choix = 0, index = -1;
                while(index == -1) {
                    printf("Votre choix > ");
                    if (scanf("%d", &choix) == 1) {
                        for(int i=0; i<NB_CARTES_MANCHE; i++) {
                            if(ma_main[i] == choix && choix != 0) index = i;
                        }
                    } else { while(getchar()!='\n'); }
                    
                    if (index == -1) printf(RED "Carte invalide ou absente.\n" RESET);
                }

                message_t rep = {0};
                rep.mtype = MTYPE_GESTIONNAIRE;
                rep.u.action.code = ACT_JOUER_CARTE;
                rep.u.action.data.jouer.id_joueur = id_joueur;
                rep.u.action.data.jouer.carte = choix;
                ipc_envoyer(qid, &rep, 0);
                
                ma_main[index] = 0;
                printf("Carte %d envoyée. En attente...\n", choix);
                break;

            case ORD_DEMANDE_CHOIX_RANGEE:
                printf(RED "\n!!! VOTRE CARTE EST TROP PETITE !!!\n" RESET);
                printf("Choisissez une rangée à ramasser (1-4) > ");
                int rangee = 0;
                while (rangee < 1 || rangee > 4) {
                    scanf("%d", &rangee);
                    if (rangee < 1 || rangee > 4) printf(RED "Invalide (1-4) > " RESET);
                }
                
                message_t rep_choix = {0};
                rep_choix.mtype = MTYPE_GESTIONNAIRE;
                rep_choix.u.action.code = ACT_CHOISIR_RANGEE;
                rep_choix.u.action.data.choix.id_joueur = id_joueur;
                rep_choix.u.action.data.choix.index_rangee = rangee - 1; 
                ipc_envoyer(qid, &rep_choix, 0);
                printf("Vous ramassez la rangée %d.\n", rangee);
                break;

            case ORD_FIN_PARTIE:
                // Affichage du vainqueur basé sur les derniers scores reçus
                printf(BLUE "\n========== PARTIE TERMINÉE ==========\n" RESET);
                printf("Score Final JOUEUR 1 : %d\n", derniers_scores[1]);
                printf("Score Final JOUEUR 2 : %d\n", derniers_scores[2]);
                
                if (derniers_scores[1] < derniers_scores[2]) {
                    printf(GREEN "🏆 LE JOUEUR 1 A GAGNÉ ! 🏆\n" RESET);
                } else if (derniers_scores[2] < derniers_scores[1]) {
                    printf(GREEN "🏆 LE JOUEUR 2 A GAGNÉ ! 🏆\n" RESET);
                } else {
                    printf(YELLOW "🤝 ÉGALITÉ PARFAITE ! 🤝\n" RESET);
                }
                printf(BLUE "=====================================\n" RESET);
                goto fin;
        }
    }
fin:
    journal_fermer();
    return 0;
}