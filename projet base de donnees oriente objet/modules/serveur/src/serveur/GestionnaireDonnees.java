package serveur;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GestionnaireDonnees {
    private final String directory;
    private final ConcurrentHashMap<String, ReadWriteLock> lockMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public GestionnaireDonnees(String directory) {
        this.directory = directory;
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Configuration de Jackson
        this.objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pour un JSON plus lisible

        // Parcours des fichiers .json existants et ajout des verrous dans lockMap
        File[] fichiers = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (fichiers != null) {
            for (File fichier : fichiers) {
                String type = fichier.getName().replace(".json", "");
                lockMap.put(type, new ReentrantReadWriteLock());
            }
        }
    }

    private File getFichier(String type) {
        return new File(directory + "/" + type + ".json");
    }

    private synchronized ReadWriteLock getLock(String type) {
        if (!lockMap.containsKey(type)) {
            lockMap.put(type, new ReentrantReadWriteLock());
        }
        return lockMap.get(type);
    }

    private void serialiser(File fichier, List<Map<String, Object>> objects) throws IOException {
        objectMapper.writeValue(fichier, objects);
    }

    private List<Map<String, Object>> deserialiser(File fichier) throws IOException {
        return objectMapper.readValue(fichier, new TypeReference<List<Map<String, Object>>>() {});
    }

    public boolean creer(String type, List<Map<String, Object>> objects) throws IOException {
        if (type == null) {
            type = "ObjetGenerique"; // Type par défaut si non spécifié
        }

        File fichier = getFichier(type);

        if (!fichier.exists()) {
            ReadWriteLock lock = getLock(type);
            lock.writeLock().lock();
            try {
                serialiser(fichier, objects);
                return true;
            } finally {
                lock.writeLock().unlock();
            }
        }
        return false;
    }

    public List<Map<String, Object>> lire(String type) throws IOException {
        File fichier = getFichier(type);
        ReadWriteLock lock = getLock(type);
        lock.readLock().lock();

        try {
            if (!fichier.exists()) {
                return new ArrayList<>();
            }
            return deserialiser(fichier);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean ajouter(String type, List<Map<String, Object>> nouveauxObjets) throws IOException {
        if (type == null) {
            type = "ObjetGenerique";
        }

        File fichier = getFichier(type);
        ReadWriteLock lock = getLock(type);

        if (fichier.exists()) {
            lock.writeLock().lock();
            try {
                List<Map<String, Object>> listeObjet = deserialiser(fichier);
                listeObjet.addAll(nouveauxObjets);
                serialiser(fichier, listeObjet);
                return true;
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            creer(type,nouveauxObjets);
            return false;
        }
    }

    public int supprimer(String type, List<Integer> ids) throws IOException {
        int nbSuppression = 0;
        File fichier = getFichier(type);
        ReadWriteLock lock = getLock(type);
        lock.writeLock().lock();

        try {
            if (fichier.exists()) {
                List<Map<String, Object>> listeObjets = deserialiser(fichier);

                for (Integer id : ids) {
                    if (id - 1 >= 0 && id - 1 < listeObjets.size()) {
                        listeObjets.remove((id - 1));
                        nbSuppression++;
                    }
                }
                serialiser(fichier, listeObjets);
            }
            return nbSuppression;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int supprimerListeObj(String type, List<Map<String, Object>> listeObjetsSupprimer) throws IOException {
        if (listeObjetsSupprimer == null || listeObjetsSupprimer.isEmpty()) {
            return 0;
        }

        if (type == null) {
            type = "ObjetGenerique";
        }

        File fichier = getFichier(type);
        ReadWriteLock lock = getLock(type);

        lock.writeLock().lock();
        try {
            int nbSuppression = 0;
            if (fichier.exists()) {
                List<Map<String, Object>> listeObjets = deserialiser(fichier);
                int tailleAvant = listeObjets.size();

                // Pour chaque objet à supprimer, on le recherche dans la liste
                for (Map<String, Object> objToRemove : listeObjetsSupprimer) {
                    listeObjets.removeIf(objToRemove::equals);
                }

                nbSuppression = tailleAvant - listeObjets.size();
                serialiser(fichier, listeObjets);
            }
            return nbSuppression;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String lireString(String type) throws IOException {
        List<Map<String, Object>> listeMapsObjets = lire(type);
        List<Map<String, Object>> listeIdObjets = new ArrayList<>();

        for (int i = 0; i < listeMapsObjets.size(); i++) {
            Map<String, Object> original = listeMapsObjets.get(i);
            LinkedHashMap<String, Object> mapObjets = new LinkedHashMap<>();
            mapObjets.put("id", i+1);

            // Ajouter les autres champs
            for (Map.Entry<String, Object> entry : original.entrySet()) {
                mapObjets.put(entry.getKey(), entry.getValue());
            }

            listeIdObjets.add(mapObjets);
        }
        return objectMapper.writeValueAsString(listeIdObjets);
    }

    public String listerTypes() {
        // Les données sont stockées dans un dossier "data" avec des fichiers [type].json
        File dossier = new File("data");
        File[] fichiers = dossier.listFiles((dir, name) -> name.endsWith(".json"));

        Set<String> types = new HashSet<>();
        if (fichiers != null) {
            for (File fichier : fichiers) {
                String nom = fichier.getName();
                types.add(nom.substring(0, nom.lastIndexOf('.')));
            }
        }
        String typesFormates = String.join(", ", types); // Conversion en String avec virgule entre chaque type
        return typesFormates;
    }
}