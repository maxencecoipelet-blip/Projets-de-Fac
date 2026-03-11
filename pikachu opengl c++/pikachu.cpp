#ifdef __APPLE__
#include <GLUT/glut.h>
#else
#include <GL/freeglut.h>
#endif
#include <cstdlib>
#include <cmath>
#include <cstdio>
#include <jpeglib.h>
#include <jerror.h>

#ifdef __WIN32
#pragma comment (lib, "jpeg.lib")
#endif

// ========== VARIABLES GLOBALES ==========

// Contrôles souris/caméra
char presse;
int anglex = 0, angley = 0, x, y, xold, yold;
float distanceCamera = 8.0f;

// Animation de la queue
float angleQueue = 0.0f;
float vitesseQueue = 1.5f;
float amplitudeQueue = 40.0f;
float offsetQueue = -100.0f;

// ANIMATION DES OREILLES
float angleOreilles = 0.0f;
float vitesseOreilles = 2.0f;
float amplitudeOreilles = 8.0f;

// Animation de la Pokéball
bool pokeballVisible = false;
float pokeballX = 0.0f;
float pokeballY = 5.0f;
float pokeballZ = 3.0f;
float rotationPokeball = 0.0f;

// États d'animation
enum EtatAnimation {
    INACTIF,        // Rien
    LANCEMENT,      // Pokéball lancée
    IMPACT,         // Impact + rebond
    CAPTURE,        // Pikachu rétrécit
    CAPTURE_FINIE   // Terminé
};

EtatAnimation etatAnimation = INACTIF;
float tempsAnimation = 0.0f;
float echellePikachu = 1.0f;
bool pikachuVisible = true;

// Variables pour les éclairs
bool afficherEclairs = false;
float timerEclairs = 0.0f;
float dureeEclairs = 1.5f;

// Textures
#define LARG_ARENE 1080
#define HAUT_ARENE 810
unsigned char imageArene[LARG_ARENE * HAUT_ARENE * 3];

#define LARG_TETE 960
#define HAUT_TETE 960
unsigned char imageTete[LARG_TETE * HAUT_TETE * 3];

#define LARG_POKEBALL 256
#define HAUT_POKEBALL 256
unsigned char imagePokeball[LARG_POKEBALL * HAUT_POKEBALL * 3];

GLuint textureArene;
GLuint textureTete;
GLuint texturePokeball;

// ========== PROTOTYPES ==========
void chargerImageJpeg(const char *fichier, unsigned char* buffer, int width, int height);
void initTextures();
void initLumieres();
void initOpenGL();
void dessinerCorps(float R, float scaleY);
void dessinerTete(float R, int slices, int stacks);
void dessinerOreille(float height, float baseRadius);
void dessinerBras(float radius, float length);
void dessinerJambe(float radius, float length);
void dessinerSegmentQueue();
void dessinerQueue();
void dessinerArene();
void dessinerPokeball(float R);
void mettreAJourAnimationPokeball();
void dessinerEclairs();
void animation();
void drawPikachu();
void affichage();
void clavier(unsigned char touche, int x, int y);
void specialKeys(int key, int x, int y);
void reshape(int w, int h);
void mouse(int button, int state, int x, int y);
void mousemotion(int x, int y);

// ========== INITIALISATION ==========

void initOpenGL() {
    glClearColor(0.6f, 0.8f, 1.0f, 1.0f);
    glEnable(GL_DEPTH_TEST);
    glShadeModel(GL_SMOOTH);
    initTextures();
    initLumieres();
}

// ========== CHARGEMENT JPEG ==========

void chargerImageJpeg(const char *fichier, unsigned char* buffer, int width, int height) {
    struct jpeg_decompress_struct cinfo;
    struct jpeg_error_mgr jerr;
    FILE *file;
    unsigned char *ligne;

    cinfo.err = jpeg_std_error(&jerr);
    jpeg_create_decompress(&cinfo);

#ifdef __WIN32
    if (fopen_s(&file, fichier, "rb") != 0)
#else
    if ((file = fopen(fichier, "rb")) == 0)
#endif
    {
        fprintf(stderr, "Erreur : impossible d'ouvrir le fichier %s\n", fichier);
        exit(1);
    }

    jpeg_stdio_src(&cinfo, file);
    jpeg_read_header(&cinfo, TRUE);

    if (cinfo.jpeg_color_space == JCS_GRAYSCALE) {
        fprintf(stdout, "Erreur : l'image doit être RGB\n");
        exit(1);
    }

    jpeg_start_decompress(&cinfo);

    while (cinfo.output_scanline < cinfo.output_height) {
        ligne = buffer + 3 * width * cinfo.output_scanline;
        jpeg_read_scanlines(&cinfo, &ligne, 1);
    }

    jpeg_finish_decompress(&cinfo);
    jpeg_destroy_decompress(&cinfo);
    fclose(file);
}

// ========== INITIALISATION DES TEXTURES ==========

void initTextures() {
    // Texture de l'arène
    chargerImageJpeg("arene.jpeg", imageArene, LARG_ARENE, HAUT_ARENE);
    glGenTextures(1, &textureArene);
    glBindTexture(GL_TEXTURE_2D, textureArene);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, LARG_ARENE, HAUT_ARENE, 0, GL_RGB, GL_UNSIGNED_BYTE, imageArene);
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

    // Texture de la tête de Pikachu
    chargerImageJpeg("pikachu_face.jpg", imageTete, LARG_TETE, HAUT_TETE);
    glGenTextures(1, &textureTete);
    glBindTexture(GL_TEXTURE_2D, textureTete);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, LARG_TETE, HAUT_TETE, 0, GL_RGB, GL_UNSIGNED_BYTE, imageTete);
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

    // Texture de la Pokéball
    chargerImageJpeg("Pokeball.jpeg", imagePokeball, LARG_POKEBALL, HAUT_POKEBALL);
    glGenTextures(1, &texturePokeball);
    glBindTexture(GL_TEXTURE_2D, texturePokeball);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, LARG_POKEBALL, HAUT_POKEBALL, 0, GL_RGB, GL_UNSIGNED_BYTE, imagePokeball);
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
}

// ========== INITIALISATION DES LUMIÈRES ==========

void initLumieres() {
    glEnable(GL_LIGHTING);
    glEnable(GL_NORMALIZE);

    // Lumière 0 : Directionnelle (Soleil)
    glEnable(GL_LIGHT0);
    GLfloat light0_position[] = { -1.0f, 2.0f, 1.0f, 0.0f };
    GLfloat light0_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    GLfloat light0_ambient[] = { 0.3f, 0.3f, 0.3f, 1.0f };
    GLfloat light0_specular[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    glLightfv(GL_LIGHT0, GL_POSITION, light0_position);
    glLightfv(GL_LIGHT0, GL_DIFFUSE, light0_diffuse);
    glLightfv(GL_LIGHT0, GL_AMBIENT, light0_ambient);
    glLightfv(GL_LIGHT0, GL_SPECULAR, light0_specular);

    // Lumière 1 : Ponctuelle (Spot sur Pikachu)
    glEnable(GL_LIGHT1);
    GLfloat light1_position[] = { 0.0f, 3.0f, 2.5f, 1.0f };
    GLfloat light1_diffuse[] = { 1.0f, 0.9f, 0.7f, 1.0f };
    GLfloat light1_ambient[] = { 0.1f, 0.1f, 0.05f, 1.0f };
    GLfloat light1_specular[] = { 1.0f, 1.0f, 0.8f, 1.0f };
    glLightfv(GL_LIGHT1, GL_POSITION, light1_position);
    glLightfv(GL_LIGHT1, GL_DIFFUSE, light1_diffuse);
    glLightfv(GL_LIGHT1, GL_AMBIENT, light1_ambient);
    glLightfv(GL_LIGHT1, GL_SPECULAR, light1_specular);
    glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, 1.0f);
    glLightf(GL_LIGHT1, GL_LINEAR_ATTENUATION, 0.1f);
    glLightf(GL_LIGHT1, GL_QUADRATIC_ATTENUATION, 0.05f);

    // Matériau par défaut
    GLfloat mat_ambient[] = { 0.7f, 0.7f, 0.7f, 1.0f };
    GLfloat mat_diffuse[] = { 0.8f, 0.8f, 0.8f, 1.0f };
    GLfloat mat_specular[] = { 0.3f, 0.3f, 0.3f, 1.0f };
    GLfloat mat_shininess[] = { 20.0f };
    glMaterialfv(GL_FRONT, GL_AMBIENT, mat_ambient);
    glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_diffuse);
    glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
    glMaterialfv(GL_FRONT, GL_SHININESS, mat_shininess);

    glEnable(GL_COLOR_MATERIAL);
    glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
}

// ========== CORPS DU PIKACHU ==========

void dessinerCorps(float R, float scaleY) {
    glPushMatrix();
        glScalef(1.0f, scaleY, 1.0f);
        glutSolidSphere(R, 20, 20);
    glPopMatrix();
}

// ========== TÊTE DU PIKACHU (SPHÈRE PARAMÉTRIQUE AVEC TEXTURE) ==========

void dessinerTete(float R, int slices, int stacks) {
    // Face avant avec texture (0° → 180°)
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, textureTete);
    glColor3f(1.0f, 1.0f, 1.0f);

    for (int i = 0; i < stacks; i++) {
        float phi1 = M_PI * i / stacks;
        float phi2 = M_PI * (i + 1) / stacks;

        glBegin(GL_TRIANGLE_STRIP);
        for (int j = 0; j <= slices; j++) {
            float theta = 0.0f + M_PI * (float)j / slices;

            float x1 = R * cos(theta) * sin(phi1);
            float y1 = R * cos(phi1);
            float z1 = R * sin(theta) * sin(phi1);
            float u1 = (float)j / slices;
            float v1 = (float)i / stacks;

            glTexCoord2f(u1, v1);
            glNormal3f(x1 / R, y1 / R, z1 / R);
            glVertex3f(x1, y1, z1);

            float x2 = R * cos(theta) * sin(phi2);
            float y2 = R * cos(phi2);
            float z2 = R * sin(theta) * sin(phi2);
            float u2 = (float)j / slices;
            float v2 = (float)(i + 1) / stacks;

            glTexCoord2f(u2, v2);
            glNormal3f(x2 / R, y2 / R, z2 / R);
            glVertex3f(x2, y2, z2);
        }
        glEnd();
    }

    glDisable(GL_TEXTURE_2D);

    // Face arrière en jaune uni (180° → 360°)
    glColor3f(1.0f, 1.0f, 0.0f);

    for (int i = 0; i < stacks; i++) {
        float phi1 = M_PI * i / stacks;
        float phi2 = M_PI * (i + 1) / stacks;

        glBegin(GL_TRIANGLE_STRIP);
        for (int j = 0; j <= slices; j++) {
            float theta = M_PI + M_PI * (float)j / slices;

            float x1 = R * cos(theta) * sin(phi1);
            float y1 = R * cos(phi1);
            float z1 = R * sin(theta) * sin(phi1);

            glNormal3f(x1 / R, y1 / R, z1 / R);
            glVertex3f(x1, y1, z1);

            float x2 = R * cos(theta) * sin(phi2);
            float y2 = R * cos(phi2);
            float z2 = R * sin(theta) * sin(phi2);

            glNormal3f(x2 / R, y2 / R, z2 / R);
            glVertex3f(x2, y2, z2);
        }
        glEnd();
    }
}

// ========== OREILLES ==========

void dessinerOreille(float height, float baseRadius) {
    glPushMatrix();
        glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        glColor3f(1.0f, 1.0f, 0.0f);
        glutSolidCone(baseRadius, height, 10, 10);
         // Bout noir
        glPushMatrix();
            glTranslatef(0.0f, 0.0f, height * 0.85f);
            glColor3f(0.2f, 0.1f, 0.0f);
            glutSolidCone(baseRadius * 0.3f, height * 0.15f, 10, 10);
        glPopMatrix();
    glPopMatrix();
}

// ========== BRAS ==========

void dessinerBras(float radius, float length) {
    int segments = 16;

    for (int i = 0; i < segments; i++) {
        float theta1 = 2.0f * M_PI * i / segments;
        float theta2 = 2.0f * M_PI * (i + 1) / segments;

        glBegin(GL_QUADS);
        float x1 = radius * cos(theta1);
        float z1 = radius * sin(theta1);
        float x2 = radius * cos(theta2);
        float z2 = radius * sin(theta2);

        float nx = (cos(theta1) + cos(theta2)) / 2.0f;
        float nz = (sin(theta1) + sin(theta2)) / 2.0f;
        glNormal3f(nx, 0.0f, nz);

        glVertex3f(x1, 0.0f, z1);
        glVertex3f(x1, -length, z1);
        glVertex3f(x2, -length, z2);
        glVertex3f(x2, 0.0f, z2);
        glEnd();
    }

    glPushMatrix();
        glTranslatef(0.0f, -length, 0.0f);
        glutSolidSphere(radius, 16, 16);
    glPopMatrix();
}

// ========== JAMBES ==========

void dessinerJambe(float radius, float length) {
    glPushMatrix();
        glTranslatef(0.0f, -length * 0.3f, 0.0f);
        glScalef(1.5f, 0.8f, 1.6f);
        glutSolidSphere(radius, 16, 16);
    glPopMatrix();
}

// ========== SEGMENT DE QUEUE ==========

void dessinerSegmentQueue() {
    float thickness = 0.04f;

    glBegin(GL_QUADS);
    glNormal3f(0.0f, 0.0f, 1.0f);
    glVertex3f(-0.18f, 0.0f, thickness/2);
    glVertex3f(0.12f, 0.0f, thickness/2);
    glVertex3f(0.08f, 0.5f, thickness/2);
    glVertex3f(-0.10f, 0.5f, thickness/2);
    glEnd();

    glBegin(GL_QUADS);
    glNormal3f(0.0f, 0.0f, -1.0f);
    glVertex3f(-0.10f, 0.5f, -thickness/2);
    glVertex3f(0.08f, 0.5f, -thickness/2);
    glVertex3f(0.12f, 0.0f, -thickness/2);
    glVertex3f(-0.18f, 0.0f, -thickness/2);
    glEnd();

    glBegin(GL_QUADS);
    glNormal3f(-1.0f, 0.0f, 0.0f);
    glVertex3f(-0.18f, 0.0f, -thickness/2);
    glVertex3f(-0.18f, 0.0f, thickness/2);
    glVertex3f(-0.10f, 0.5f, thickness/2);
    glVertex3f(-0.10f, 0.5f, -thickness/2);
    glEnd();

    glBegin(GL_QUADS);
    glNormal3f(1.0f, 0.0f, 0.0f);
    glVertex3f(0.12f, 0.0f, thickness/2);
    glVertex3f(0.12f, 0.0f, -thickness/2);
    glVertex3f(0.08f, 0.5f, -thickness/2);
    glVertex3f(0.08f, 0.5f, thickness/2);
    glEnd();

    glBegin(GL_QUADS);
    glNormal3f(0.0f, -1.0f, 0.0f);
    glVertex3f(-0.18f, 0.0f, -thickness/2);
    glVertex3f(0.12f, 0.0f, -thickness/2);
    glVertex3f(0.12f, 0.0f, thickness/2);
    glVertex3f(-0.18f, 0.0f, thickness/2);
    glEnd();

    glBegin(GL_QUADS);
    glNormal3f(0.0f, 1.0f, 0.0f);
    glVertex3f(-0.10f, 0.5f, thickness/2);
    glVertex3f(0.08f, 0.5f, thickness/2);
    glVertex3f(0.08f, 0.5f, -thickness/2);
    glVertex3f(-0.10f, 0.5f, -thickness/2);
    glEnd();
}

// ========== QUEUE COMPLÈTE ==========

void dessinerQueue() {
    glPushMatrix();
        glRotatef(85.0f, 0.0f, 0.0f, 1.0f);
        glColor3f(0.4f, 0.2f, 0.0f); // MARRON
        dessinerSegmentQueue();

        glPushMatrix();
            glTranslatef(-0.01f, 0.38f, 0.0f);
            glRotatef(85.0f, 0.0f, 0.0f, 1.0f);
            glColor3f(0.4f, 0.2f, 0.0f);
            dessinerSegmentQueue();

            glPushMatrix();
                glTranslatef(-0.02f, 0.32f, 0.0f);
                glRotatef(-80.0f, 0.0f, 0.0f, 1.0f);
                glColor3f(1.0f, 1.0f, 0.0f);
                dessinerSegmentQueue();

                glPushMatrix();
                    glTranslatef(-0.02f, 0.38f, 0.0f);
                    glRotatef(85.0f, 0.0f, 0.0f, 1.0f);
                    dessinerSegmentQueue();

                    glPushMatrix();
                        glTranslatef(-0.02f, 0.32f, 0.0f);
                        glRotatef(-80.0f, 0.0f, 0.0f, 1.0f);
                        dessinerSegmentQueue();

                        glPushMatrix();
                            glTranslatef(-0.02f, 0.38f, 0.0f);
                            glRotatef(85.0f, 0.0f, 0.0f, 1.0f);
                            dessinerSegmentQueue();

                            glPushMatrix();
                                glTranslatef(-0.02f, 0.32f, 0.0f);
                                glRotatef(-80.0f, 0.0f, 0.0f, 1.0f);
                                dessinerSegmentQueue();
                            glPopMatrix();
                        glPopMatrix();
                    glPopMatrix();
                glPopMatrix();
            glPopMatrix();
        glPopMatrix();
    glPopMatrix();
}

// ========== ARÈNE ==========

void dessinerArene() {
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, textureArene);
    glColor3f(1.0f, 1.0f, 1.0f);

    float largeur = 4.0f;
    float profondeur = 3.0f;

    glBegin(GL_QUADS);
        glNormal3f(0.0f, 1.0f, 0.0f);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3f(-largeur, 0.0f, -profondeur);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3f(largeur, 0.0f, -profondeur);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3f(largeur, 0.0f, profondeur);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3f(-largeur, 0.0f, profondeur);
    glEnd();

    glDisable(GL_TEXTURE_2D);
}

// ========== POKÉBALL ==========

void dessinerPokeball(float R) {
    int slices = 30;
    int stacks = 30;

    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, texturePokeball);
    glColor3f(1.0f, 1.0f, 1.0f);

    for (int i = 0; i < stacks; i++) {
        float phi1 = M_PI * i / stacks;
        float phi2 = M_PI * (i + 1) / stacks;

        glBegin(GL_TRIANGLE_STRIP);
        for (int j = 0; j <= slices; j++) {
            float theta = 2.0f * M_PI * j / slices;

            float x1 = R * cos(theta) * sin(phi1);
            float y1 = R * cos(phi1);
            float z1 = R * sin(theta) * sin(phi1);
            float u1 = (float)j / slices;
            float v1 = (float)i / stacks;

            glTexCoord2f(u1, v1);
            glNormal3f(x1 / R, y1 / R, z1 / R);
            glVertex3f(x1, y1, z1);

            float x2 = R * cos(theta) * sin(phi2);
            float y2 = R * cos(phi2);
            float z2 = R * sin(theta) * sin(phi2);
            float u2 = (float)j / slices;
            float v2 = (float)(i + 1) / stacks;

            glTexCoord2f(u2, v2);
            glNormal3f(x2 / R, y2 / R, z2 / R);
            glVertex3f(x2, y2, z2);
        }
        glEnd();
    }

    glDisable(GL_TEXTURE_2D);
}

// ========== MISE À JOUR ANIMATION POKÉBALL ==========

void mettreAJourAnimationPokeball() {
    if (etatAnimation == INACTIF) return;

    tempsAnimation += 0.012f;

    // Phase 1 : Lancement
    if (etatAnimation == LANCEMENT) {
        float t = tempsAnimation / 2.5f;

        rotationPokeball += 3.0f;
        if (rotationPokeball > 360.0f) rotationPokeball -= 360.0f;

        if (t <= 1.0f) {
            float start_x = 0.0f;
            float start_y = 2.0f;
            float start_z = 5.0f;

            float end_x = 0.0f;
            float end_y = 2.1f;
            float end_z = 0.0f;

            pokeballX = start_x + (end_x - start_x) * t;
            pokeballZ = start_z + (end_z - start_z) * t;

            float arc_height = 2.5f;
            pokeballY = start_y + arc_height * (1.0f - 4.0f * (t - 0.5f) * (t - 0.5f));
        } else {
            etatAnimation = IMPACT;
            tempsAnimation = 0.0f;
            pokeballX = 0.0f;
            pokeballY = 2.1f;
            pokeballZ = 0.0f;
        }
    }

    // Phase 2 : Rebond
    else if (etatAnimation == IMPACT) {
        float t = tempsAnimation / 1.2f;

        if (t <= 1.0f) {
            float impact_y = 2.1f;
            float ground_y = 0.2f;

            float bounce_height = 1.0f;
            float parabola = -4.0f * bounce_height * (t - 0.5f) * (t - 0.5f) + bounce_height;

            pokeballY = impact_y + parabola - (impact_y - ground_y) * t;

            float end_x = 1.2f;
            float end_z = 0.5f;

            pokeballX = end_x * t;
            pokeballZ = end_z * t;
        } else {
            etatAnimation = CAPTURE;
            tempsAnimation = 0.0f;
            afficherEclairs = true;
            timerEclairs = 0.0f;
            pokeballY = 0.2f;
            pokeballX = 1.2f;
            pokeballZ = 0.5f;
        }
    }

    // Phase 3 : Capture (Éclairs puis rétrécissement)
    else if (etatAnimation == CAPTURE) {
        pokeballY = 0.2f;
        pokeballX = 1.2f;
        pokeballZ = 0.5f;

        timerEclairs += 0.012f;

        if (timerEclairs < dureeEclairs) {
            echellePikachu = 1.0f;
        } else {
            afficherEclairs = false;

            float shrink_time = timerEclairs - dureeEclairs;
            float shrink_duration = 2.0f;

            echellePikachu = 1.0f - (shrink_time / shrink_duration);

            if (echellePikachu <= 0.05f) {
                pikachuVisible = false;
                etatAnimation = CAPTURE_FINIE;
                }
    }
    }
    // Phase 4 : Capture terminée
    else if (etatAnimation == CAPTURE_FINIE) {
        pokeballY = 0.2f;
        pokeballX = 1.2f;
        pokeballZ = 0.5f;
        afficherEclairs = false;
    }
}

// ========== ÉCLAIRS ==========

void dessinerEclairs() {
    if (!afficherEclairs) return;

    glDisable(GL_LIGHTING);
    glDisable(GL_LIGHT0);
    glDisable(GL_LIGHT1);
    glDisable(GL_TEXTURE_2D);

    glPushMatrix();
        glTranslatef(0.0f, 1.2f, 0.0f);

        glLineWidth(3.0f);
        glBegin(GL_LINES);

        int num_bolts = 15;

        for (int i = 0; i < num_bolts; i++) {
            float seed = timerEclairs * 10.0f + i * 1.7f;

            float angle = sin(seed * 2.3f) * M_PI * 2.0f;
            float radius = 0.4f + 0.4f * cos(seed * 1.5f);

            float start_x = radius * cos(angle) * 0.7f;
            float start_y = -0.3f + 0.6f * sin(seed * 3.1f);
            float start_z = radius * sin(angle) * 0.7f;

            float end_radius = radius + 0.3f;
            float end_x = end_radius * cos(angle + 0.2f * sin(seed));
            float end_y = start_y + 0.2f * cos(seed * 2.7f);
            float end_z = end_radius * sin(angle + 0.2f * sin(seed));

            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(start_x, start_y, start_z);

            float mid_x = (start_x + end_x) * 0.5f + 0.1f * sin(seed * 4.0f);
            float mid_y = (start_y + end_y) * 0.5f + 0.15f * cos(seed * 3.5f);
            float mid_z = (start_z + end_z) * 0.5f + 0.1f * cos(seed * 5.0f);

            glColor3f(1.0f, 1.0f, 0.5f);
            glVertex3f(mid_x, mid_y, mid_z);

            glVertex3f(mid_x, mid_y, mid_z);
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(end_x, end_y, end_z);
        }

        glEnd();
        glLineWidth(1.0f);
    glPopMatrix();

    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glEnable(GL_LIGHT1);
}

// ========== ANIMATION CONTINUE ==========

void animation() {
    // Animation de la queue
    angleQueue += vitesseQueue;
    if (angleQueue > 360.0f) {
        angleQueue -= 360.0f;
    }

    // Animation des oreilles
    angleOreilles += vitesseOreilles;
    if (angleOreilles > 360.0f) {
        angleOreilles -= 360.0f;
    }

    // Animation de la Pokéball
    mettreAJourAnimationPokeball();

    glutPostRedisplay();
}

// ========== PIKACHU COMPLET ==========

void drawPikachu() {
    if (!pikachuVisible) return;

    float bodyR = 0.5f;
    float bodyScaleY = 1.2f;
    float headR = 0.35f;

    glPushMatrix();
        // Échelle de rétrécissement
        glScalef(echellePikachu, echellePikachu, echellePikachu);

        // CORPS
        glTranslatef(0.0f, 0.8f, 0.0f);
        glColor3f(1.0f, 1.0f, 0.0f);
        dessinerCorps(bodyR, bodyScaleY);

        // TÊTE
        glPushMatrix();
            glTranslatef(0.0f, bodyR * bodyScaleY + headR * 0.7f, 0.0f);
            glColor3f(1.0f, 1.0f, 0.0f);
            dessinerTete(headR, 20, 20);

            // OREILLE GAUCHE
            glPushMatrix();
                glTranslatef(-headR * 0.5f, headR * 0.7f, 0.0f);
                float oscillationOreille = amplitudeOreilles * sin(angleOreilles * M_PI / 180.0f);
                glRotatef(15.0f + oscillationOreille, 0.0f, 0.0f, 1.0f);
                dessinerOreille(0.5f, 0.06f);
            glPopMatrix();

            // OREILLE DROITE
            glPushMatrix();
                glTranslatef(headR * 0.5f, headR * 0.7f, 0.0f);
                glRotatef(-15.0f-oscillationOreille, 0.0f, 0.0f, 1.0f);
                dessinerOreille(0.5f, 0.06f);
            glPopMatrix();
        glPopMatrix();

        // BRAS GAUCHE
        glPushMatrix();
            glTranslatef(-bodyR * 0.75f, bodyR * 0.4f, 0.0f);
            glRotatef(-25.0f, 0.0f, 0.0f, 1.0f);
            glColor3f(1.0f, 1.0f, 0.0f);
            dessinerBras(0.10f, 0.35f);
        glPopMatrix();

        // BRAS DROIT
        glPushMatrix();
            glTranslatef(bodyR * 0.75f, bodyR * 0.4f, 0.0f);
            glRotatef(25.0f, 0.0f, 0.0f, 1.0f);
            glColor3f(1.0f, 1.0f, 0.0f);
            dessinerBras(0.10f, 0.35f);
        glPopMatrix();

        // JAMBE GAUCHE
        glPushMatrix();
            glTranslatef(-bodyR * 0.25f, -bodyR * bodyScaleY, 0.0f);
            glColor3f(1.0f, 1.0f, 0.0f);
            dessinerJambe(0.15f, 0.25f);
        glPopMatrix();

        // JAMBE DROITE
        glPushMatrix();
            glTranslatef(bodyR * 0.25f, -bodyR * bodyScaleY, 0.0f);
            glColor3f(1.0f, 1.0f, 0.0f);
            dessinerJambe(0.15f, 0.25f);
        glPopMatrix();

        // QUEUE
        glPushMatrix();
            glTranslatef(0.0f, -bodyR * bodyScaleY * 0.2f, -bodyR * 1.0f);
            float oscillation = amplitudeQueue * sin(angleQueue * M_PI / 180.0f);
            glRotatef(offsetQueue - oscillation, 0.0f, 1.0f, 0.0f);
            glRotatef(160.0f, 1.0f, 0.0f, 0.0f);
            glScalef(0.6f, 0.6f, 0.6f);
            dessinerQueue();
        glPopMatrix();

    glPopMatrix();
}

// ========== AFFICHAGE ==========

void affichage() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();

    gluLookAt(0.0, 2.0, distanceCamera, 0.0, 0.6, 0.0, 0.0, 1.0, 0.0);

    glRotatef(angley, 1.0, 0.0, 0.0);
    glRotatef(anglex, 0.0, 1.0, 0.0);

    dessinerArene();
    drawPikachu();
    dessinerEclairs();

    if (pokeballVisible) {
        glPushMatrix();
            glTranslatef(pokeballX, pokeballY, pokeballZ);
            glRotatef(rotationPokeball, 0, 1, 0);
            dessinerPokeball(0.2f);
        glPopMatrix();
    }

    glutSwapBuffers();
}

// ========== CLAVIER ==========

void clavier(unsigned char touche, int x, int y) {
    switch (touche) {
        case 'p':
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            break;
        case 'f':
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            break;
        case 's':
            glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
            break;
        case 'z':
            distanceCamera -= 0.3f;
            break;
        case 'Z':
            distanceCamera += 0.3f;
            break;
        case 'q':
            exit(0);
            break;
        case 'b':
            // Lancer l'animation
            pokeballVisible = true;
            etatAnimation = LANCEMENT;
            tempsAnimation = 0.0f;
            echellePikachu = 1.0f;
            pikachuVisible = true;
            afficherEclairs = false;
            timerEclairs = 0.0f;
            break;
    }
    glutPostRedisplay();
}

// ========== TOUCHES SPÉCIALES ==========

void specialKeys(int key, int x, int y) {
    switch(key) {
        case GLUT_KEY_LEFT:
            anglex -= 5;
            break;
        case GLUT_KEY_RIGHT:
            anglex += 5;
            break;
        case GLUT_KEY_UP:
            angley -= 5;
            break;
        case GLUT_KEY_DOWN:
            angley += 5;
            break;
    }
    glutPostRedisplay();
}

// ========== REDIMENSIONNEMENT ==========

void reshape(int w, int h) {
    glViewport(0, 0, w, h);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(45.0, (float)w / (float)h, 1.0, 50.0);
    glMatrixMode(GL_MODELVIEW);
}

// ========== SOURIS ==========

void mouse(int button, int state, int x, int y) {
    if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {
        presse = 1;
        xold = x;
        yold = y;
    }
    if (button == GLUT_LEFT_BUTTON && state == GLUT_UP) {
        presse = 0;
    }
}

void mousemotion(int x, int y) {
    if (presse) {
        anglex += (x - xold);
        angley += (y - yold);
        glutPostRedisplay();
    }
    xold = x;
    yold = y;
}

// ========== MAIN ==========

int main(int argc, char **argv) {
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_RGB | GLUT_DOUBLE | GLUT_DEPTH);
    glutInitWindowSize(800, 600);
    glutCreateWindow("Pikachu OpenGL - Projet L3");

    initOpenGL();

    glutDisplayFunc(affichage);
    glutKeyboardFunc(clavier);
    glutReshapeFunc(reshape);
    glutMouseFunc(mouse);
    glutMotionFunc(mousemotion);
    glutSpecialFunc(specialKeys);
    glutIdleFunc(animation);

    glutMainLoop();
    return 0;
}
