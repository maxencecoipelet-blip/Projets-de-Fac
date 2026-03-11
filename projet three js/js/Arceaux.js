var vecteurTranslationArceauX = 0;
var vecteurTranslationArceauY = 0;
var vecteurTranslationArceauZ = 0;

//place un arceau dans la MaScene selon des paramètres et vecteurTranslationArceau qui représente la translation de l'arceau et renvoie la liste des objets utilisés pour l'arceau
function placerArceau(MaScene, vecteurTranslationArceau, coulArceau, rayonCyl, hauteurCyl, hauteurCylSol,hauteurCylHorizontal, nbePtCbe, nbeptsRot, opacitePhong, brillancePhong, coulEmissivePhong , coulSpeculPhong) {

    vecteurTranslationArceauX = vecteurTranslationArceau.x;
    vecteurTranslationArceauY = vecteurTranslationArceau.y;
    vecteurTranslationArceauZ = vecteurTranslationArceau.z;

    let nbPtsGenera = 2;
    let bolOuvert = false;
    let theta0 = 0;
    let thetaLg = 2 * Math.PI;

    let CylConeGeomVertical = new THREE.CylinderGeometry(rayonCyl, rayonCyl, hauteurCyl, nbeptsRot, nbPtsGenera, bolOuvert, theta0, thetaLg);
    let cylindre = surfPhong(CylConeGeomVertical, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    cylindre.castShadow = true; // Permet à cet objet de projeter des ombres

    let cylindre2 = surfPhong(CylConeGeomVertical, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    cylindre2.castShadow = true;

    translationVecteurTranslationArceau(cylindre, new THREE.Vector3(0, 0, hauteurCyl / 2 + hauteurCylSol));
    cylindre.rotateX(Math.PI / 2);

    let P0 = new THREE.Vector2(rayonCyl, hauteurCylSol);
    let P1 = new THREE.Vector2(rayonCyl, hauteurCylSol / 4);
    let P2 = new THREE.Vector2(rayonCyl + 0.05, 0);
    let P3 = new THREE.Vector2(rayonCyl + 0.1, 0);

    let G1CylSol = latheBez3Arceau(nbePtCbe, nbeptsRot, P0, P1, P2, P3, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong)
    let G1CylSol2 = latheBez3Arceau(nbePtCbe, nbeptsRot, P0, P1, P2, P3, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong)
    translationVecteurTranslationArceau(G1CylSol, new THREE.Vector3(0, 0, 0));
    G1CylSol.rotateX(Math.PI / 2);

    let hauteurArrondiCyl = rayonCyl / 8;

    let points = ptsArcCercleFerme(parseInt(nbePtCbe/2), hauteurArrondiCyl, rayonCyl)
    let GeomG1CylLathe1 = new THREE.LatheGeometry(points, nbeptsRot, 0, 2 * Math.PI);
    let G1CylLathe1 = surfPhong(GeomG1CylLathe1, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    G1CylLathe1.castShadow = true;

    let G1CylLathe12 = surfPhong(GeomG1CylLathe1, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    G1CylLathe12.castShadow = true;

    translationVecteurTranslationArceau(G1CylLathe1, new THREE.Vector3(0, 0, hauteurCyl + hauteurCylSol));
    G1CylLathe1.rotateX(Math.PI / 2);

    let rayonTore = rayonCyl / 3
    let hauteurLathe1Tore = 0.1

    P0 = new THREE.Vector2(rayonTore, hauteurLathe1Tore);
    P1 = new THREE.Vector2(rayonTore, hauteurLathe1Tore / 4);
    P2 = new THREE.Vector2(rayonTore + 0.05, 0);
    P3 = new THREE.Vector2(rayonTore + 0.1, 0);

    let G1CylLathe2 = latheBez3Arceau(nbePtCbe, nbeptsRot, P0, P1, P2, P3, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    let G1CylLathe22 = latheBez3Arceau(nbePtCbe, nbeptsRot, P0, P1, P2, P3, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    translationVecteurTranslationArceau(G1CylLathe2, new THREE.Vector3(0, 0, hauteurCyl + hauteurCylSol + hauteurArrondiCyl));
    G1CylLathe2.rotateX(Math.PI / 2);

    let rayonMineur = rayonTore;
    let rayonMajeur = hauteurCyl * 1.3 + rayonCyl * 1.3;
    let toreGeometry = new THREE.TorusGeometry(rayonMajeur, rayonMineur, nbeptsRot, nbePtCbe, Math.PI / 2);
    let tore = surfPhong(toreGeometry, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    tore.castShadow = true;

    let tore2 = surfPhong(toreGeometry, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    tore2.castShadow = true;
    //tore2.receiveShadow = true;

    translationVecteurTranslationArceau(tore, new THREE.Vector3(0, -rayonMajeur, hauteurCyl + hauteurCylSol + hauteurArrondiCyl + hauteurLathe1Tore));
    tore.rotateX(Math.PI / 2);
    tore.rotateY(Math.PI / 2);

    let CylConeGeomHorizontal = new THREE.CylinderGeometry(rayonMineur, rayonMineur, hauteurCylHorizontal, nbeptsRot, nbPtsGenera, bolOuvert, theta0, thetaLg);
    let cylindreHorizontal = surfPhong(CylConeGeomHorizontal, coulArceau, opacitePhong, false, coulSpeculPhong, brillancePhong, coulEmissivePhong);
    cylindreHorizontal.castShadow = true;
    translationVecteurTranslationArceau(cylindreHorizontal, new THREE.Vector3(0, -rayonMajeur - hauteurCylHorizontal / 2, hauteurCyl + hauteurCylSol + hauteurArrondiCyl + hauteurLathe1Tore + rayonMajeur))

    let translationYSym = -2 * (rayonMajeur) - hauteurCylHorizontal

    translationVecteurTranslationArceau(cylindre2, new THREE.Vector3(0, translationYSym, hauteurCyl / 2 + hauteurCylSol));
    cylindre2.rotateX(Math.PI / 2);

    translationVecteurTranslationArceau(G1CylSol2, new THREE.Vector3(0, translationYSym, 0));
    G1CylSol2.rotateX(Math.PI / 2);

    translationVecteurTranslationArceau(G1CylLathe12, new THREE.Vector3(0, translationYSym, hauteurCyl + hauteurCylSol));
    G1CylLathe12.rotateX(Math.PI / 2);

    translationVecteurTranslationArceau(G1CylLathe22, new THREE.Vector3(0, translationYSym, hauteurCyl + hauteurCylSol + hauteurArrondiCyl));
    G1CylLathe22.rotateX(Math.PI / 2);

    translationVecteurTranslationArceau(tore2, new THREE.Vector3(0, translationYSym + rayonMajeur, hauteurCyl + hauteurCylSol + hauteurArrondiCyl + hauteurLathe1Tore));
    tore2.rotateX(Math.PI / 2);
    tore2.rotateY(-Math.PI / 2);

    MaScene.add(cylindre);
    MaScene.add(cylindre2);
    MaScene.add(G1CylSol);
    MaScene.add(G1CylSol2);
    MaScene.add(G1CylLathe1);
    MaScene.add(G1CylLathe12);
    MaScene.add(G1CylLathe2);
    MaScene.add(G1CylLathe22);
    MaScene.add(tore);
    MaScene.add(tore2);
    MaScene.add(cylindreHorizontal);

    return [cylindre, cylindre2, G1CylSol, G1CylSol2, G1CylLathe1, G1CylLathe12, G1CylLathe2, G1CylLathe22, tore, tore2, cylindreHorizontal]
}

//translate un objet par un vecteur + vecteurTranslationArceau
function translationVecteurTranslationArceau(objet, vecteur){
    let x1 = vecteur.x + vecteurTranslationArceauX;
    let y1 = vecteur.y + vecteurTranslationArceauY;
    let z1 = vecteur.z + vecteurTranslationArceauZ;
    let vecNormal = new THREE.Vector3(x1,y1,z1);
    vecNormal.normalize();
    let carreDistance = Math.pow(x1,2)+Math.pow(y1,2)+Math.pow(z1,2)
    objet.translateOnAxis(vecNormal,Math.sqrt(carreDistance));
}
//Lathe à partir d'une courbe de Bezier de degré 3 avec parametres de brillance et emissivite en plus
function latheBez3Arceau(nbePtCbe,nbePtRot,P0,P1,P2,P3,coul,opacite,bolTranspa,coulSpec,brillance, coulEmissif){
    //let geometry = new THREE.Geometry();
    let p0= new THREE.Vector2(P0.x,P0.y);
    let p1= new THREE.Vector2(P1.x,P1.y);
    let p2= new THREE.Vector2(P2.x,P2.y);
    let p3= new THREE.Vector2(P3.x,P3.y);
    let Cbe3 = new THREE.CubicBezierCurve(p0,p1,p2,p3);
    let points = Cbe3.getPoints(nbePtCbe);
    let latheGeometry = new THREE.LatheGeometry(points,nbePtRot,0,2*Math.PI);
    let lathe = surfPhong(latheGeometry,coul,opacite,bolTranspa,coulSpec,brillance, coulEmissif);
    return lathe;
}

//Créee les points pour la lathe représentant la partie arrondie du cylindre de rayonCyl pour un arrondi haut de rayon Arrondi
function ptsArcCercleFerme(nb,rayonArrondi,rayonCyl) {
    // tableau de points
    let points = new Array(nb + 2);

    for (let k = 0; k <= nb; k++) {
        let t2 = k / nb; // t2 varie de 0 à 1
        let angle = t2 * Math.PI/2; // Angle en radians, de 0 à π/2
        let x0 = rayonArrondi*Math.cos(angle) + rayonCyl - rayonArrondi;
        let y0 = rayonArrondi*Math.sin(angle);
        points[k] = new THREE.Vector2(x0, y0);
    }
    points[nb+1] = new THREE.Vector2(0,rayonArrondi);
    return points;
}

//*************************************
//
//  MENU GUI ARCEAU
//
//*************************************

function ajoutArceauGui(gui,menuGUI,MaScene,arceau1,arceau2,arceau3,positionArceau1,positionArceau2,positionArceau3,coulArceau,hauteurCylHorizontal) {
    let guiArceau = gui.addFolder("Arceaux");

    guiArceau.add(menuGUI, "choixCouleurArceau", {
        "Rouge": 1,
        "Bleu": 2,
        "Vert": 3,
        "Magenta": 4
    }).onChange(function () {
        retirerArceaux();
        if (menuGUI.choixCouleurArceau == 1) {
            coulArceau = "#ff0000";
        } else if (menuGUI.choixCouleurArceau == 2) {
            coulArceau = "#0000ff";
        } else if (menuGUI.choixCouleurArceau == 3) {
            coulArceau = "#00ff00";
        } else if (menuGUI.choixCouleurArceau == 4) {
            coulArceau = "#FF00FF";
        }
        placerArceauxGUI();
    });

    guiArceau.add(menuGUI, "nbPtsCourbe", 10, 300).onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });
    guiArceau.add(menuGUI, "nbPtsRotation", 10, 300).onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });
    guiArceau.add(menuGUI, "rayonCylindreBase", 0.1, 2).onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });
    guiArceau.add(menuGUI, "hauteurCylindreBase", 0.5, 4).onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });
    guiArceau.add(menuGUI, "hauteurCylindreBaseSol", 0.01, 0.2).onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });
    guiArceau.add(menuGUI, "opaciteArceau", 0, 1).onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });
    guiArceau.add(menuGUI, "brillanceArceau", 0, 100).onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });
    guiArceau.addColor(menuGUI, "coulEmissiveArceau").onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });
    guiArceau.addColor(menuGUI, "coulSpecArceau").onChange(function () {
        retirerArceaux();
        placerArceauxGUI();
    });

    function retirerArceaux() {
        for (let k = 0; k < arceau1.length; k++) {
            if (arceau1[k]) MaScene.remove(arceau1[k]);
            if (arceau2[k]) MaScene.remove(arceau2[k]);
            if (arceau3[k]) MaScene.remove(arceau3[k]);
        }
    }

    function placerArceauxGUI() {
        arceau1 = placerArceau(MaScene, positionArceau1, coulArceau, menuGUI.rayonCylindreBase, menuGUI.hauteurCylindreBase
            , menuGUI.hauteurCylindreBaseSol, hauteurCylHorizontal, menuGUI.nbPtsCourbe, menuGUI.nbPtsRotation
            , menuGUI.opaciteArceau, menuGUI.brillanceArceau, menuGUI.coulEmissiveArceau, menuGUI.coulSpecArceau);

        arceau2 = placerArceau(MaScene, positionArceau2, coulArceau, menuGUI.rayonCylindreBase, menuGUI.hauteurCylindreBase
            , menuGUI.hauteurCylindreBaseSol, hauteurCylHorizontal, menuGUI.nbPtsCourbe, menuGUI.nbPtsRotation
            , menuGUI.opaciteArceau, menuGUI.brillanceArceau, menuGUI.coulEmissiveArceau, menuGUI.coulSpecArceau);

        arceau3 = placerArceau(MaScene, positionArceau3, coulArceau, menuGUI.rayonCylindreBase, menuGUI.hauteurCylindreBase
            , menuGUI.hauteurCylindreBaseSol, hauteurCylHorizontal, menuGUI.nbPtsCourbe, menuGUI.nbPtsRotation
            , menuGUI.opaciteArceau, menuGUI.brillanceArceau, menuGUI.coulEmissiveArceau, menuGUI.coulSpecArceau);
    }
}

//*************************************
//
//  FIN MENU GUI ARCEAU
//
//*************************************

function placerArceauxAvecVerification(scene, variationY, toleranceAlignement,coulArceau,rayonCyl,hauteurCyl,hauteurCylSol,hauteurCylHorizontal,nbePtsCbe,nbPtsRot,opaciteArceauPhong,brillanceArceauPhong,coulEmissiveArceauPhong,coulSpecArceauPhong) {
    // Génère une position aléatoire pour l'axe y dans la plage donnée
    function genererPositionY() {
        return (Math.random() * 2 - 1) * variationY; // Génère un nombre entre -variationY et +variationY
    }

    // Vérifie si trois points sont alignés à une constante près
    function sontAlignes(p1, p2, p3, tolerance) {
        // Calcul de l'aire du triangle formé par les trois points
        const aire = Math.abs(
            (p2.x - p1.x) * (p3.y - p1.y) -
            (p2.y - p1.y) * (p3.x - p1.x)
        );
        // Divise par la distance pour obtenir une approximation de la hauteur
        const distanceBase = p1.distanceTo(p3);
        const hauteur = aire / distanceBase;

        return hauteur < tolerance; // Si la hauteur est inférieure à la tolérance, les points sont presque alignés
    }

    let positionArceau1, positionArceau2, positionArceau3;
    let arceauxValides = false;

    while (!arceauxValides) {
        // Générer les positions des arceaux
        positionArceau1 = new THREE.Vector3(20, genererPositionY(), 0);
        positionArceau2 = new THREE.Vector3(40, genererPositionY(), 0);
        positionArceau3 = new THREE.Vector3(60, genererPositionY(), 0);

        // Vérifier que les trois arceaux ne sont pas alignés
        if (!sontAlignes(positionArceau1, positionArceau2, positionArceau3, toleranceAlignement)) {
            arceauxValides = true;
        }
    }

    // Placer les arceaux dans la scène
    let arceau1 = placerArceau(scene, positionArceau1,coulArceau,rayonCyl,hauteurCyl,hauteurCylSol,hauteurCylHorizontal,
        nbePtsCbe,nbPtsRot,opaciteArceauPhong,brillanceArceauPhong,coulEmissiveArceauPhong,coulSpecArceauPhong);

    let arceau2 = placerArceau(scene, positionArceau2,coulArceau,rayonCyl,hauteurCyl,hauteurCylSol,hauteurCylHorizontal,
        nbePtsCbe,nbPtsRot,opaciteArceauPhong,brillanceArceauPhong,coulEmissiveArceauPhong,coulSpecArceauPhong);

    let arceau3 = placerArceau(scene, positionArceau3,coulArceau,rayonCyl,hauteurCyl,hauteurCylSol,hauteurCylHorizontal,
        nbePtsCbe,nbPtsRot,opaciteArceauPhong,brillanceArceauPhong,coulEmissiveArceauPhong,coulSpecArceauPhong);

    return [arceau1, positionArceau1, arceau2, positionArceau2, arceau3, positionArceau3];
}
