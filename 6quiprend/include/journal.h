#ifndef JOURNAL_H
#define JOURNAL_H


int journal_init (void);

void journal_printf(const char *format,...);

void journal_fermer(void);

#endif