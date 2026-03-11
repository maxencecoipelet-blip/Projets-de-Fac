// Fonction pour calculer le centre d'un arceau
function calculerCentreArceau(positionArceau, rayonCyl, hauteurCyl, hauteurCylHorizontal) {
    let ecartYCentre = hauteurCyl * 1.3 + rayonCyl * 1.3 + 0.5 * hauteurCylHorizontal;
    return new THREE.Vector3(positionArceau.x, positionArceau.y - ecartYCentre, positionArceau.z);
}

function testArceau1GaucheDroiteDeArceau2(positionArceau1,positionArceau2){
    let VecteurReference = new THREE.Vector3(1,0, 0); // vecteur unitaire sur l'axe Y

// Calculer le vecteur PQ
    let PQ = new THREE.Vector3();
    PQ.subVectors(positionArceau2, positionArceau1); // PQ = Q - P

// Calculer le produit vectoriel entre PQ et le vecteur de référence
    let crossProduct = new THREE.Vector3();
    crossProduct.crossVectors(PQ, VecteurReference);

// Vérifier le signe de la composante z du produit vectoriel
    if (crossProduct.z > 0) {
        //console.log("à gauche");
        return 1;
    }
    else {
        //console.log("à droite");
        return -1
    }
}

function ptsTrajectoire(MaScene,origineBalle,centreArceau1,centreArceau2,centreArceau3,positionCone, nbPoints,rayonCyl,hauteurCyl,hauteurCylHorizontal,bolDebutant, bolAffichePt){

    let points = []; // Liste des points pour la trajectoire

    // Coefficient pour ajuster les tangentes des jointures G1
    let coef = 4;

    //Erreur aléatoire si débutant

    let variationDifficulte = 0;
    let variationMaxDifficulte = 0;

    if(bolDebutant){
        let espaceArceaux = 2 * (hauteurCyl * 1.3 + rayonCyl * 1.3) + hauteurCylHorizontal - 2 * rayonCyl // Correspond à l'espace entre les deux cylindres d'un arceau (voir Arceau.js)
        variationMaxDifficulte = espaceArceaux * 1.5
    }

    // si origine Balle est à gauche de centreArceau1, renvoie 1 sinon -1
    let testGaucheDroite = testArceau1GaucheDroiteDeArceau2(origineBalle,centreArceau1);

    if(bolDebutant) variationDifficulte = (Math.random() * 2 - 1) * variationMaxDifficulte; //Tire un nombre entre + ou - variationMaxDifficulte

    // Points de contrôle pour la première courbe de Bezier de degré 2 (origineBalle -> arceau1)
    let P1 = new THREE.Vector3(
        centreArceau1.x / 2,
        origineBalle.y - testGaucheDroite * Math.abs((origineBalle.y - centreArceau1.y)/2),
        origineBalle.z
    );
    let P2 = new THREE.Vector3(
        centreArceau1.x,
        centreArceau1.y + variationDifficulte, // On ajoute variationDifficulte à chaque point de controle
                                                // des courbes de beziers au niveau du centre des arceaux
        origineBalle.z
    );

    // Courbe 1 : origineBalle -> Arceau 1
    points = points.concat(
        new THREE.QuadraticBezierCurve3(origineBalle, P1, P2).getPoints(nbPoints)
    );

    testGaucheDroite = testArceau1GaucheDroiteDeArceau2(centreArceau1,centreArceau2);

    if(bolDebutant) variationDifficulte = (Math.random() * 2 - 1) * variationMaxDifficulte; //Nouveau tirage

    //Rajoute un ecart vers l'exterieur de la courbe pour essayer de la rendr plus réaliste
    let ecart = 1;

    // Points de contrôle pour la deuxième courbe de Bezier de degré 4 (arceau1 -> arceau2)
    let M1 = new THREE.Vector3(
        centreArceau1.x + (centreArceau2.x - centreArceau1.x)/4,
        centreArceau1.y - testGaucheDroite * Math.abs((centreArceau1.y - centreArceau2.y)/4),
        origineBalle.z
    );
    let M2 = new THREE.Vector3(
        centreArceau1.x + (centreArceau2.x - centreArceau1.x)/2,
        centreArceau1.y - testGaucheDroite * Math.abs(ecart + (centreArceau1.y - centreArceau2.y)/2),
        origineBalle.z
    );
    let M3 = new THREE.Vector3(
        centreArceau1.x + 3 * (centreArceau2.x - centreArceau1.x)/4,
        centreArceau1.y - testGaucheDroite * Math.abs(ecart + 3 * (centreArceau1.y - centreArceau2.y)/4),
        origineBalle.z
    );
    let M4 = new THREE.Vector3(
        centreArceau2.x,
        centreArceau2.y + variationDifficulte,
        origineBalle.z
    );

    // Ajuster les tangentes
    let vP1P2 = new THREE.Vector3();
    let vTan2 = new THREE.Vector3();
    vP1P2.subVectors(P2, P1); // Vecteur centreArceau2 - centreArceau1
    vTan2.addScaledVector(vP1P2.normalize(), coef);
    M1.addVectors(P2, vTan2);

    let tabCourbeBezier = [P2, M1, M2, M3, M4];

    // Courbe 2 : Arceau 1 -> Arceau 2
    points = points.concat(ptsBezierTab(tabCourbeBezier,nbPoints));

    testGaucheDroite = testArceau1GaucheDroiteDeArceau2(centreArceau2,centreArceau3);

    if(bolDebutant) variationDifficulte = (Math.random() * 2 - 1) * variationMaxDifficulte; //Nouveau tirage

    // Points de contrôle pour la troisième courbe de Bezier de degré 4 (arceau2 -> arceau3)
    let N1 = new THREE.Vector3(
        centreArceau2.x + (centreArceau3.x - centreArceau2.x)/4,
        centreArceau2.y - testGaucheDroite * Math.abs((centreArceau2.y - centreArceau3.y)/4),
        origineBalle.z
    );
    let N2 = new THREE.Vector3(
        centreArceau2.x + (centreArceau3.x - centreArceau2.x)/2,
        centreArceau2.y - testGaucheDroite * Math.abs((centreArceau2.y - centreArceau3.y)/2),
        origineBalle.z
    );
    let N3 = new THREE.Vector3(
        centreArceau2.x + 3 * (centreArceau3.x - centreArceau2.x)/4,
        centreArceau2.y - testGaucheDroite * Math.abs( 3 * (centreArceau2.y - centreArceau3.y)/4),
        origineBalle.z
    );
    let N4 = new THREE.Vector3(
        centreArceau3.x,
        centreArceau3.y + variationDifficulte,
        origineBalle.z
    );

    // Ajuster les tangentes
    let vM3M4 = new THREE.Vector3();
    let vTan3 = new THREE.Vector3();
    vM3M4.subVectors(M4, M3);
    vTan3.addScaledVector(vM3M4.normalize(), coef);
    N1.addVectors(M4, vTan3);

    tabCourbeBezier = [M4, N1, N2, N3, N4];

    // Courbe 3 : Arceau 2 -> Arceau 3
    points = points.concat(ptsBezierTab(tabCourbeBezier,nbPoints));

    testGaucheDroite = testArceau1GaucheDroiteDeArceau2(centreArceau3,positionCone);

    // Points de contrôle pour la première courbe de Bezier de degré 2 (origineBalle -> arceau1)
    let K1 = new THREE.Vector3(
        centreArceau3.x + (positionCone.x - centreArceau3.x)/4,
        centreArceau3.y - testGaucheDroite * Math.abs((positionCone.y - centreArceau3.y)/2),
        origineBalle.z
    );
    let K2 = new THREE.Vector3(
        positionCone.x - origineBalle.x/2,
        positionCone.y,
        origineBalle.z
    );

    // Ajuster les tangentes
    let vN3N4 = new THREE.Vector3();
    let vTan4 = new THREE.Vector3();
    vN3N4.subVectors(N4, N3);
    vTan4.addScaledVector(vN3N4.normalize(), coef);
    K1.addVectors(N4, vTan4);

    // Courbe 4 : Arceau3 -> DemiCone de revolution
    points = points.concat(
        new THREE.QuadraticBezierCurve3(N4, K1, K2).getPoints(nbPoints)
    );

    if(bolAffichePt){
        let coulPt = "#ffffff";
        tracePt(MaScene,P1,coulPt,0.2,true);
        tracePt(MaScene,P2,coulPt,0.2,true);
        tracePt(MaScene,M1,coulPt,0.2,true);
        tracePt(MaScene,M2,coulPt,0.2,true);
        tracePt(MaScene,M3,coulPt,0.2,true);
        tracePt(MaScene,M4,coulPt,0.2,true);
        tracePt(MaScene,N1,coulPt,0.2,true);
        tracePt(MaScene,N2,coulPt,0.2,true);
        tracePt(MaScene,N3,coulPt,0.2,true);
        tracePt(MaScene,N4,coulPt,0.2,true);
        tracePt(MaScene,K1,coulPt,0.2,true);
        tracePt(MaScene,K2,coulPt,0.2,true);

    }

    return points;
}