function creerMarteau(scene, decalageMarteau, rayonBalle, teteLargeur, angleMarteau, pointsCbeBez, rayonCyl, hauteurCyl,hauteurCylHorizontal, positionCylGaucheArceau1, positionCylGaucheArceau2, positionCylGaucheArceau3) {
    // Calcule les positions des centres des cylindres de gauche des arceaux
    let ecartCylindres = 2 * (hauteurCyl * 1.3 + rayonCyl * 1.3) + hauteurCylHorizontal;

    let positionCylDroiteArceau1 = new THREE.Vector3(positionCylGaucheArceau1.x, positionCylGaucheArceau1.y - ecartCylindres, positionCylGaucheArceau1.z);
    let positionCylDroiteArceau2 = new THREE.Vector3(positionCylGaucheArceau2.x, positionCylGaucheArceau2.y - ecartCylindres, positionCylGaucheArceau2.z);
    let positionCylDroiteArceau3 = new THREE.Vector3(positionCylGaucheArceau3.x, positionCylGaucheArceau3.y - ecartCylindres, positionCylGaucheArceau3.z);

    // Création du marteau et de la balle
    const mancheHauteur = 5;
    const mancheRayon = 0.2;
    const mancheSegments = 32;

    const mancheGeometry = new THREE.CylinderGeometry(mancheRayon, mancheRayon, mancheHauteur, mancheSegments);
    const mancheMaterial = new THREE.MeshStandardMaterial({ color: 0x008b7e });
    const manche = new THREE.Mesh(mancheGeometry, mancheMaterial);
    manche.position.y = mancheHauteur / 2;
    manche.castShadow = true;
    manche.receiveShadow = true;

    const teteHauteur = 1;
    const teteProfondeur = 1;
    const teteGeometry = new THREE.BoxGeometry(teteLargeur, teteHauteur, teteProfondeur);
    const teteMaterial = new THREE.MeshStandardMaterial({ color: 0x008b7e });
    const tete = new THREE.Mesh(teteGeometry, teteMaterial);
    tete.position.y = mancheHauteur + (teteHauteur / 2);
    tete.castShadow = true;
    tete.receiveShadow = true;

    const marteau = new THREE.Group();
    marteau.add(manche);
    marteau.add(tete);
    marteau.position.set(0, decalageMarteau, 6);
    scene.add(marteau);
    marteau.rotation.x = -Math.PI / 2;

    if (decalageMarteau > 0) { marteau.rotation.y = Math.PI + angleMarteau; }
    else { marteau.rotation.y = Math.PI - angleMarteau; }

    marteau.position.x = (teteLargeur / 2 + rayonBalle) * (1 - Math.sin(Math.PI / 2 - angleMarteau));
    marteau.position.y = decalageMarteau + (teteLargeur / 2 + rayonBalle) * Math.sign(decalageMarteau) * Math.cos(Math.PI / 2 - angleMarteau);

    const balle = creerBalle(scene, teteLargeur / 2 + rayonBalle, decalageMarteau, rayonBalle, rayonBalle);

    let perduAffiche;

    animerMarteauEtBalle(scene, marteau, balle, pointsCbeBez, [positionCylDroiteArceau1, positionCylDroiteArceau2, positionCylDroiteArceau3, positionCylGaucheArceau1, positionCylGaucheArceau2, positionCylGaucheArceau3], rayonCyl, hauteurCyl);

    function animerMarteauEtBalle(scene, marteau, balle, pointsCbeBez, positionsCylindres, rayonCyl, hauteurCyl) {
        let marteauEnMouvement = true;
        const angleMax = -Math.PI / 4;
        const angleMin = 0;
        const vitesseRotation = 0.006;
        let vitesseBalle = 0.003;
        const vitesseReduction = 0.0000045;
        let balleEnMouvement = false;
        let t = 0;
        let phase = "aller"; // Phase initiale du marteau

        const courbeBalle = new THREE.CatmullRomCurve3(pointsCbeBez);

        perduAffiche = false; // Booleen pour éviter de réafficher "Perdu"
        let horsZone;

        function detecterCollision(positionBalle) {
            let collisionDetectee = false;

            // Vérifier les collisions avec les cylindres des arceaux
            for (let cylindre of positionsCylindres) {
                const distanceXY = Math.sqrt((positionBalle.x - cylindre.x) ** 2 + (positionBalle.y - cylindre.y) ** 2);

                if (distanceXY <= rayonBalle + rayonCyl) {
                    console.log("Collision détectée avec un cylindre !");
                    collisionDetectee = true;
                }
            }

            // Si aucune collision, vérifier si la balle est en dehors des arceaux
            if (!perduAffiche) {
                horsZone = false;

                // Vérifier pour chaque arceau
                if (
                    positionBalle.x >= (positionCylGaucheArceau1.x - rayonCyl) &&
                    positionBalle.x <= (positionCylGaucheArceau1.x + rayonCyl) &&
                    (positionBalle.y > (positionCylGaucheArceau1.y + rayonCyl) ||
                        positionBalle.y < (positionCylDroiteArceau1.y - rayonCyl))
                ) {
                    horsZone = true;
                } else if (
                    positionBalle.x >= (positionCylGaucheArceau2.x - rayonCyl) &&
                    positionBalle.x <= (positionCylGaucheArceau2.x + rayonCyl) &&
                    (positionBalle.y > (positionCylGaucheArceau2.y + rayonCyl) ||
                        positionBalle.y < (positionCylDroiteArceau2.y - rayonCyl))
                ) {
                    horsZone = true;
                } else if (
                    positionBalle.x >= (positionCylGaucheArceau3.x - rayonCyl) &&
                    positionBalle.x <= (positionCylGaucheArceau3.x + rayonCyl) &&
                    (positionBalle.y > (positionCylGaucheArceau3.y + rayonCyl) ||
                        positionBalle.y < (positionCylDroiteArceau3.y - rayonCyl))
                ) {
                    horsZone = true;
                }

                // Si la balle est hors des limites
                if (horsZone) {
                    console.log("Perdu !");
                    perduAffiche = true; // Empêche d'afficher plusieurs fois "Perdu"
                }
            }

            return collisionDetectee;
        }




        function animate() {
            requestAnimationFrame(animate);

            if (marteauEnMouvement) {
                if (phase === "aller") {
                    if (marteau.rotation.z > angleMax) {
                        marteau.rotation.z -= vitesseRotation;
                    } else {
                        phase = "retour";
                    }
                } else if (phase === "retour") {
                    if (marteau.rotation.z < angleMin) {
                        marteau.rotation.z += vitesseRotation;
                    } else {
                        marteauEnMouvement = false;
                        balleEnMouvement = true;
                    }
                }
            }

            if (balleEnMouvement) {
                t += vitesseBalle;
                if (t >= 1 || detecterCollision(courbeBalle.getPointAt(t))) {
                    if(!perduAffiche && horsZone) {
                        perduAffiche= true;
                        console.log("Perdu!");}
                    balleEnMouvement = false;
                } else {
                    balle.position.copy(courbeBalle.getPointAt(t));
                }
                vitesseBalle = Math.max(0, vitesseBalle - vitesseReduction);
            }
        }
        animate();
    }


    function creerBalle(scene, positionX, positionY, positionZ, rayon, couleur = 0xe49400) {
        const geometry = new THREE.SphereGeometry(rayon, 32, 32);
        const material = new THREE.MeshPhongMaterial({ color: couleur, shininess: 100 });
        const balle = new THREE.Mesh(geometry, material);
        balle.position.set(positionX, positionY, positionZ);
        balle.castShadow = true;
        scene.add(balle);
        return balle;
    }

    return [balle, marteau];
}
