#ifndef COMMUN_H
#define COMMUN_H
#include <stdint.h>

// Constantes
#define NB_JOUEURS_MAX 6
#define NB_CARTES_MANCHE 10
#define CLE_FILE_MSG 0x12345678 

// Types de messages
#define MTYPE_GESTIONNAIRE 1
#define MTYPE_JOUEUR(id) (2+(id))

//ORDRES (Gestionnaire -> Joueurs)
typedef enum {
    ORD_DISTRIBUTION = 1,
    ORD_DEMANDE_JOUER,
    ORD_ENVIRONNEMENT,   
    ORD_DEMANDE_CHOIX_RANGEE, 
    ORD_FIN_PARTIE 
} code_ordre_t;

//ACTIONS (Joueurs -> Gestionnaire)
typedef enum{
    ACT_JOUER_CARTE = 1,
    ACT_CHOISIR_RANGEE 
} code_action_t;


//STRUCTURES

typedef struct {
    uint8_t id_joueur;
    uint8_t nb_cartes;
    uint8_t cartes[NB_CARTES_MANCHE];
} distrib_t;

typedef struct {
    uint8_t id_joueur;
    uint8_t carte;
} jouer_carte_t;

typedef struct {
    uint8_t id_joueur;
    uint8_t index_rangee; 
} choix_rangee_t;


typedef struct {
    uint8_t plateau[4][6]; 
    uint8_t nb_cartes[4];  
    int scores[NB_JOUEURS_MAX+1]; 
} environnement_t;

// Message SysV
typedef struct{
    long mtype; 
    union {
        struct{
            code_ordre_t code; 
            union{
                distrib_t distribution;
                environnement_t env;
            } data;
        } ordre;

        struct {
            code_action_t code; 
            union{
                jouer_carte_t jouer;
                choix_rangee_t choix; 
            } data;
        } action;
    } u;
} message_t;

#endif