#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include "commun.h"
#include "ipc.h"
#include "journal.h"

int main(int argc, char *argv[]) {
    // Le robot peut prendre un ID en argument, sinon par défaut c'est le 2
    int id_joueur = (argc >= 2) ? atoi(argv[1]) : 2;

    if (journal_init() == -1) return 1;
    journal_printf("--- ROBOT (J%d) : Démarrage ---\n", id_joueur);

    int qid = ipc_creer_ou_ouvrir();
    if (qid == -1) return 1;

    uint8_t ma_main[NB_CARTES_MANCHE] = {0}; 
    message_t msg;
    srand(time(NULL) + id_joueur); // Graine aléatoire

    while (1) {
        if (ipc_recevoir(qid, MTYPE_JOUEUR(id_joueur), &msg, 0) == -1) break;

        switch (msg.u.ordre.code) {
            case ORD_DISTRIBUTION:
                for (int i=0; i<msg.u.ordre.data.distribution.nb_cartes; i++) 
                    ma_main[i] = msg.u.ordre.data.distribution.cartes[i];
                journal_printf("ROBOT %d : Cartes reçues.\n", id_joueur);
                break;

            case ORD_ENVIRONNEMENT:
                // Le robot reçoit le plateau mais s'en fiche pour l'instant
                break;

            case ORD_DEMANDE_JOUER:
                // Stratégie bête : Joue la première carte dispo
                {
                    uint8_t carte = 0;
                    int idx = -1;
                    for(int i=0; i<NB_CARTES_MANCHE; i++) {
                        if(ma_main[i] != 0) { carte = ma_main[i]; idx=i; break; }
                    }
                    if (carte != 0) {
                        message_t rep = {0};
                        rep.mtype = MTYPE_GESTIONNAIRE;
                        rep.u.action.code = ACT_JOUER_CARTE;
                        rep.u.action.data.jouer.id_joueur = id_joueur;
                        rep.u.action.data.jouer.carte = carte;
                        ipc_envoyer(qid, &rep, 0);
                        ma_main[idx] = 0;
                        journal_printf("ROBOT %d : Joue %d\n", id_joueur, carte);
                    }
                }
                break;

            case ORD_DEMANDE_CHOIX_RANGEE:
                // Stratégie bête : Choisit une rangée au hasard (0 à 3)
                {
                    int choix = rand() % 4;
                    message_t rep = {0};
                    rep.mtype = MTYPE_GESTIONNAIRE;
                    rep.u.action.code = ACT_CHOISIR_RANGEE;
                    rep.u.action.data.choix.id_joueur = id_joueur;
                    rep.u.action.data.choix.index_rangee = choix;
                    ipc_envoyer(qid, &rep, 0);
                    journal_printf("ROBOT %d : Choisit rangée %d au hasard\n", id_joueur, choix+1);
                }
                break;

            case ORD_FIN_PARTIE:
                goto fin;
        }
    }
fin:
    journal_fermer();
    return 0;
}