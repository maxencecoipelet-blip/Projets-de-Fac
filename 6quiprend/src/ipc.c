#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>

#include "commun.h"
#include "ipc.h"


static size_t ipc_taille_message(void)
{
    return sizeof(message_t) - sizeof(long);
}


int ipc_creer_ou_ouvrir(void)
{
    int qid = msgget(CLE_FILE_MSG, IPC_CREAT | 0666);
    if (qid == -1) {
        perror("ipc_creer_ou_ouvrir: msgget");
        return -1;
    }
    return qid;
}

int ipc_detruire(int qid)
{
    if (msgctl(qid, IPC_RMID, NULL) == -1) {
        perror("ipc_detruire: msgctl(IPC_RMID)");
        return -1;
    }
    return 0;
}


int ipc_envoyer(int qid, const message_t *msg, int flags)
{
    size_t taille = ipc_taille_message();

    if (msgsnd(qid, msg, taille, flags) == -1) {
        perror("ipc_envoyer: msgsnd");
        return -1;
    }
    return 0;
}


int ipc_recevoir(int qid, long type, message_t *msg, int flags)
{
    size_t taille = ipc_taille_message();

    if (msgrcv(qid, msg, taille, type, flags) == -1) {
        if (errno != EINTR) {  
            perror("ipc_recevoir: msgrcv");
        }
        return -1;
    }
    return 0;
}
