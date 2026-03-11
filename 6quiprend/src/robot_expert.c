#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include <limits.h>
#include "commun.h"
#include "ipc.h"
#include "journal.h"



int get_boeufs_expert(uint8_t carte) {
    if (carte == 0) return 0;
    if (carte == 55) return 7;
    if (carte % 11 == 0) return 5;
    if (carte % 10 == 0) return 3;
    if (carte % 5 == 0) return 2;
    return 1;
}

int calculer_cout_rangee(int index_rangee, environnement_t *env) {
    int total = 0;
    int nb = env->nb_cartes[index_rangee];
    for (int i = 0; i < nb; i++) {
        total += get_boeufs_expert(env->plateau[index_rangee][i]);
    }
    return total;
}


uint8_t choisir_carte_intelligente(uint8_t *main, environnement_t *env, int id) {
    
    //VARIABLES POUR LE MEILLEUR COUP 
    int index_best_safe = -1;
    int min_gap_safe = 1000;

    //VARIABLES POUR LE MEILLEUR SACRIFICE
    int index_best_sacrifice = -1;
    int min_cout_sacrifice = 1000;

    //VARIABLES POUR LE PIRE CAS
    int index_plus_petite = -1;
    uint8_t min_val_carte = 255;

    for (int i = 0; i < NB_CARTES_MANCHE; i++) {
        uint8_t ma_carte = main[i];
        if (ma_carte == 0) continue;

        //On garde trace de la plus petite carte (au cas où rien ne rentre)
        if (ma_carte < min_val_carte) {
            min_val_carte = ma_carte;
            index_plus_petite = i;
        }

        //On cherche où cette carte irait
        int index_rangee_cible = -1;
        int diff_locale = 1000;

        for (int r = 0; r < 4; r++) {
            uint8_t derniere = env->plateau[r][env->nb_cartes[r]-1];
            if (ma_carte > derniere) {
                int diff = ma_carte - derniere;
                if (diff < diff_locale) {
                    diff_locale = diff;
                    index_rangee_cible = r;
                }
            }
        }

        
        if (index_rangee_cible != -1) {
            // La carte rentre quelque part
            if (env->nb_cartes[index_rangee_cible] < 5) {
                // COUP SÛR : La rangée n'est pas pleine.
                // C'est notre priorité absolue. On cherche le plus petit gap ICI.
                if (diff_locale < min_gap_safe) {
                    min_gap_safe = diff_locale;
                    index_best_safe = i;
                }
            } else {
                
                // On ne considère ce coup que si on n'a pas de coup sûr.
                int cout = calculer_cout_rangee(index_rangee_cible, env);
                if (cout < min_cout_sacrifice) {
                    min_cout_sacrifice = cout;
                    index_best_sacrifice = i;
                }
            }
        }
    }

   
    
    //D'abord, on regarde si on peut jouer sans prendre de bœufs
    if (index_best_safe != -1) {
        journal_printf("ROBOT EXPERT %d : Coup SÛR trouvé (Gap=%d)\n", id, min_gap_safe);
        return main[index_best_safe];
    } 
    
    //Si aucun coup sûr, on cherche le sacrifice le moins cher
    if (index_best_sacrifice != -1) {
        journal_printf("ROBOT EXPERT %d : Aïe, SACRIFICE obligé (Coût=%d)\n", id, min_cout_sacrifice);
        return main[index_best_sacrifice];
    } 
    
    //Si la carte ne rentre nulle part (trop petite), on joue la plus petite
    journal_printf("ROBOT EXPERT %d : Carte trop petite, joue le min (%d)\n", id, min_val_carte);
    return main[index_plus_petite];
}

int choisir_rangee_intelligente(environnement_t *env) {
    int min_boeufs = 1000;
    int meilleur_choix = 0;
    for (int i = 0; i < 4; i++) {
        int total = calculer_cout_rangee(i, env);
        if (total < min_boeufs) {
            min_boeufs = total;
            meilleur_choix = i;
        }
    }
    return meilleur_choix;
}


int main(int argc, char *argv[]) {
    int id_joueur = (argc >= 2) ? atoi(argv[1]) : 2;

    if (journal_init() == -1) return 1;
    journal_printf("--- ROBOT EXPERT V2 (J%d) : Démarrage ---\n", id_joueur);

    int qid = ipc_creer_ou_ouvrir();
    if (qid == -1) return 1;

    uint8_t ma_main[NB_CARTES_MANCHE] = {0}; 
    environnement_t env_actuelle = {0};
    message_t msg;
    srand(time(NULL) + id_joueur); 

    while (1) {
        if (ipc_recevoir(qid, MTYPE_JOUEUR(id_joueur), &msg, 0) == -1) break;

        switch (msg.u.ordre.code) {
            case ORD_DISTRIBUTION:
                for (int i=0; i<msg.u.ordre.data.distribution.nb_cartes; i++) 
                    ma_main[i] = msg.u.ordre.data.distribution.cartes[i];
                break;

            case ORD_ENVIRONNEMENT:
                env_actuelle = msg.u.ordre.data.env;
                break;

            case ORD_DEMANDE_JOUER:
                {
                    uint8_t choix = choisir_carte_intelligente(ma_main, &env_actuelle, id_joueur);
                    for(int i=0; i<NB_CARTES_MANCHE; i++) {
                        if(ma_main[i] == choix) { ma_main[i] = 0; break; }
                    }
                    message_t rep = {0};
                    rep.mtype = MTYPE_GESTIONNAIRE;
                    rep.u.action.code = ACT_JOUER_CARTE;
                    rep.u.action.data.jouer.id_joueur = id_joueur;
                    rep.u.action.data.jouer.carte = choix;
                    ipc_envoyer(qid, &rep, 0);
                    journal_printf("ROBOT EXPERT %d : Joue %d\n", id_joueur, choix);
                }
                break;

            case ORD_DEMANDE_CHOIX_RANGEE:
                {
                    int choix = choisir_rangee_intelligente(&env_actuelle);
                    message_t rep = {0};
                    rep.mtype = MTYPE_GESTIONNAIRE;
                    rep.u.action.code = ACT_CHOISIR_RANGEE;
                    rep.u.action.data.choix.id_joueur = id_joueur;
                    rep.u.action.data.choix.index_rangee = choix;
                    ipc_envoyer(qid, &rep, 0);
                    journal_printf("ROBOT EXPERT %d : Choisit rangée %d (Min boeufs)\n", id_joueur, choix+1);
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