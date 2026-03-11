#include <stdio.h>
#include <stdarg.h>
#include <stdlib.h>
#include "journal.h"



//chemin du log, ici et pas dans l'interface (.h)
static const char *LOG_FILE_PATH = "logs/partie.log";

// Pointeur interne vers le fichier de journalisation 
static FILE *fp_log = NULL;



int journal_init(void)
{
    fp_log = fopen(LOG_FILE_PATH, "a");
    if (!fp_log) {
        perror("journal_init: erreur ouverture fichier log");
        return -1;
    }
    return 0;
}



void journal_printf(const char *format, ...)
{
    if (!fp_log) {
        
        return;
    }

    va_list args;
    va_start(args, format);
    vfprintf(fp_log, format, args);
    va_end(args);

    fflush(fp_log);  
}


void journal_fermer(void)
{
    if (fp_log) {
        fclose(fp_log);
        fp_log = NULL;
    }
}
