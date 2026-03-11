

BEGIN {
    tours = 0
    prises_j1 = 0
    prises_j2 = 0
    score_j1 = 0
    score_j2 = 0
}

# Détection des tours
/^=== TOUR/ {
    tours++
}

# Prises de rangées (points intermédiaires)
/ACTION : J1 ramasse/ {
    prises_j1++
    if (match($0, /\(([0-9]+) pts\)/, m)) {
        score_j1 += m[1]
    }
}

/ACTION : J2 prend la rangée/ || /ACTION : J2 ramasse/ {
    prises_j2++
    if (match($0, /\(([0-9]+) pts\)/, m)) {
        score_j2 += m[1]
    }
}

# Scores finaux (source fiable)
/^SCORE_FINAL_J1:/ {
    score_j1 = $2
}

/^SCORE_FINAL_J2:/ {
    score_j2 = $2
}

END {
    print "\\subsection*{Statistiques générales}"
    print "\\begin{itemize}"
    print "\\item Nombre total de tours : " tours
    print "\\item Prises de rangées J1 : " prises_j1
    print "\\item Prises de rangées J2 : " prises_j2
    print "\\end{itemize}"

    print "\\subsection*{Scores finaux}"
    print "\\begin{itemize}"
    print "\\item Joueur 1 : " score_j1 " points"
    print "\\item Joueur 2 : " score_j2 " points"
    print "\\end{itemize}"

    print "\\subsection*{Résultat}"
    if (score_j1 < score_j2) {
        print "Le joueur 1 remporte la partie."
    } else if (score_j2 < score_j1) {
        print "Le joueur 2 remporte la partie."
    } else {
        print "La partie se termine par une égalité."
    }
}
