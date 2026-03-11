
CC = gcc
CFLAGS = -Wall -Iinclude


SRC = src/gestionnaire.c src/joueur.c src/joueur_reseau.c \
      src/robot.c src/robot_expert.c \
      src/ipc.c src/journal.c 

OBJ = $(SRC:.c=.o)


DOSSIER_BIN = build


GESTIONNAIRE   = $(DOSSIER_BIN)/gestionnaire
JOUEUR         = $(DOSSIER_BIN)/joueur
JOUEUR_RESEAU  = $(DOSSIER_BIN)/joueur_reseau
ROBOT          = $(DOSSIER_BIN)/robot
ROBOT_EXPERT   = $(DOSSIER_BIN)/robot_expert



tout: dossiers $(GESTIONNAIRE) $(JOUEUR) $(JOUEUR_RESEAU) $(ROBOT) $(ROBOT_EXPERT)


$(GESTIONNAIRE): src/gestionnaire.o src/ipc.o src/journal.o | dossiers
	$(CC) $(CFLAGS) $^ -o $@

$(JOUEUR): src/joueur.o src/ipc.o src/journal.o | dossiers
	$(CC) $(CFLAGS) $^ -o $@

$(JOUEUR_RESEAU): src/joueur_reseau.o src/journal.o src/ipc.o | dossiers
	$(CC) $(CFLAGS) $^ -o $@

$(ROBOT): src/robot.o src/ipc.o src/journal.o | dossiers
	$(CC) $(CFLAGS) $^ -o $@

$(ROBOT_EXPERT): src/robot_expert.o src/ipc.o src/journal.o | dossiers
	$(CC) $(CFLAGS) $^ -o $@




src/%.o: src/%.c | dossiers
	$(CC) $(CFLAGS) -c $< -o $@


.PHONY: dossiers nettoyer executer stats pdf

dossiers:
	@mkdir -p $(DOSSIER_BIN) logs


nettoyer:
	rm -f src/*.o
	rm -f $(DOSSIER_BIN)/*
	rm -f logs/partie.log


executer: tout
	./$(INIT) & \
	./$(GESTIONNAIRE) 3 & \
	./$(JOUEUR) 1 & \
	./$(ROBOT_EXPERT) 2 & \
	./$(JOUEUR_RESEAU) 127.0.0.1 4242 & \
	wait


stats:
	awk -f scripts/stats.awk logs/partie.log

pdf:
	sh scripts/gen_pdf.sh
