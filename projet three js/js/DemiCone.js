function creerDemiCone(MaScene, position, couleur, rayonBase, hauteur) {
  // Géométrie du cône entier
  const coneGeometry = new THREE.CylinderGeometry(
      rayonBase, // Rayon de la base
      0, // Rayon du haut
      hauteur, // Hauteur du cône
      32, // Segments radiaux
      1, // Segments en hauteur
      true, // Ouvrir le cône
      0, // Angle de départ
      Math.PI // On prend seulement la moitié de la circonférence pour faire un demi-cône
  );

  // Matériau pour le cône
  const coneMaterial = new THREE.MeshPhongMaterial({
    color: couleur,
    side: THREE.DoubleSide, // Visible des deux côtés
  });

  // Mesh du cône
  const cone = new THREE.Mesh(coneGeometry, coneMaterial);

  // Rotation pour coucher le cône horizontalement
  cone.rotation.x = Math.PI / 2;
  cone.rotation.z = Math.PI / 2;

  // Positionnement du cône
  cone.position.set(position.x, position.y, position.z);
  cone.castShadow = true; // Le cône projette des ombres
  cone.receiveShadow = true; // Le cône reçoit des ombres

  // Ajout du cône à la scène
  MaScene.add(cone);
}
