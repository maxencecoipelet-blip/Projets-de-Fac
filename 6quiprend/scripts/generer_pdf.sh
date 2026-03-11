

LOG="logs/partie.log"
OUTDIR="out"
TEX="$OUTDIR/rapport.tex"
PDF="$OUTDIR/rapport.pdf"

# Vérification du log
if [ ! -f "$LOG" ]; then
    echo "Fichier de log introuvable : $LOG"
    exit 1
fi

# Vérification de pdflatex
if ! command -v pdflatex >/dev/null 2>&1; then
    echo "pdflatex introuvable (installer texlive)"
    exit 1
fi

# Création du dossier de sortie
mkdir -p "$OUTDIR"

echo "📄 Génération du fichier LaTeX..."

# En-tête LaTeX
cat > "$TEX" <<EOF
\\documentclass[11pt]{article}
\\usepackage[utf8]{inputenc}
\\usepackage[T1]{fontenc}
\\usepackage[french]{babel}
\\usepackage{geometry}
\\geometry{margin=2cm}

\\title{Rapport de partie -- 6 qui prend}
\\author{Projet Systèmes et Réseaux}
\\date{\\today}

\\begin{document}
\\maketitle

\\section*{Résumé de la partie}
EOF

# Injection des statistiques
awk -f scripts/stats.awk "$LOG" >> "$TEX"

# Fin du document
cat >> "$TEX" <<EOF

\\end{document}
EOF

echo "🛠️ Compilation du PDF"

pdflatex -interaction=nonstopmode -output-directory "$OUTDIR" "$TEX" > /dev/null

