#ifndef IPC_H
#define IPC_H
#include "commun.h"

int ipc_creer_ou_ouvrir(void);
int ipc_detruire(int qid);
int ipc_envoyer(int qid, const message_t *sg, int flags);
int ipc_recevoir(int qid, long type, message_t *msg, int flags);

#endif