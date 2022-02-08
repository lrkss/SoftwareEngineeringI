package versionsverwaltung.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;

public class Projektanlage {

    private final Logger log = LoggerFactory.getLogger(Versionsverwaltung.class);

    public final Path LOKALERPFAD = Paths.get(System.getProperty("user.dir") + "/dateiablage");


    public void begruessung(){
        System.out.println("Herzlich Wilkommen in unserer Versionsverwaltung. Aktuell existieren die folgenden Projekte:");
        File alleProjekte = new File(LOKALERPFAD.toString());
        Arrays.stream(alleProjekte.list()).toList().forEach(p -> System.out.println("  - " + p));
    }

    /**
     * Aus dem individuellen Projectpath und der Eingabe des Users wird ein Pfad erstellt,
     * in dem gesucht wird, ob es einen Ordner mit dem Namen schon gibt.
     * @param projektname
     * @return Path
     */
    public File neuesProjektAnlegen(String projektname) {
        Path dateipfad = Paths.get(LOKALERPFAD.toString(), projektname);
        return verzeichnisAnlegen(dateipfad, projektname);
    }

    /**
     * Es wird ein neuer Ordner im Standardverzeichnis erstellt, mit dem entsprechenden Projektnamen.
     * @param dateipfad
     * @param projektname
     * @return File
     */
    private File verzeichnisAnlegen(Path dateipfad, String projektname) {
        File datei = new File(dateipfad.toString());
        if (datei.mkdir()) {
            log.info(MessageFormat.format("Das Projekt {0} konnte erfolgreich angelegt werden.", projektname));
        } else {
            log.info(MessageFormat.format("Das Projekt {0} konnte lokalisiert werden.", projektname));
        }
        return datei;
    }

    public boolean hatBisherKeineDateien(File verzeichnis){
        return verzeichnis.list().length == 0;
    }

}
