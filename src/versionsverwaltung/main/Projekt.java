package versionsverwaltung.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

/**
 * Die Projekt-Klasse legt neue Projekte im angegebenen Dateipfad für Projekte an.
 */
public class Projekt {

    // Logger initialisieren
    private final Logger log = LoggerFactory.getLogger(Versionsverwaltung.class);

    // Lokalen Pfad deklarieren
    public Path LOKALERPFAD;

    /**
     * Im Konstruktor den Pfad setzen, in dem die Projekte liegen bzw. liegen sollen.
     * Dieser Pfad kann selbstständig in den application.properties gesetzt werden.
     * Property: dateipfad
     */
    public Projekt() {
        FileReader reader;
        Properties p;
        try {
            reader = new FileReader("src/versionsverwaltung/config/application.properties");
            p = new Properties();
            p.load(reader);
            if (!p.getProperty("dateipfad").isEmpty()) {
                LOKALERPFAD = Paths.get(p.getProperty("dateipfad"));
            } else {
                LOKALERPFAD = Paths.get(System.getProperty("user.dir") + "/dateiablage");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Begrüßung in der Konsolen-Ausgabe mit einer Auflistung aller aktuellen Projekte.
     */
    public void begruessung() {
        System.out.println("Herzlich Wilkommen in unserer Versionsverwaltung. Aktuell existieren die folgenden Projekte:");
        File alleProjekte = new File(LOKALERPFAD.toString());
        Arrays.stream(Objects.requireNonNull(alleProjekte.list())).toList().forEach(p -> System.out.println("  - " + p));
    }

    /**
     * Aus dem individuellen Projektpath und der Eingabe des Users wird ein Pfad erstellt,
     * in dem gesucht wird, ob es einen Ordner mit dem Namen schon gibt.
     *
     * @param projektname: Name für das neue Projekt
     * @return File: angelegter Ordner
     */
    public File neuesProjektAnlegen(String projektname) {
        Path dateipfad = Paths.get(LOKALERPFAD.toString(), projektname);
        return verzeichnisAnlegen(dateipfad, projektname);
    }

    /**
     * Es wird ein neuer Ordner im Standardverzeichnis erstellt, mit dem entsprechenden Projektnamen.
     *
     * @param dateipfad: Pfad, in dem die Projekte gesichert werden
     * @param projektname: Name für das neue Projekt
     * @return File: angelegter Ordner
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

    /**
     * Ist das aktuelle Projekt bisher noch leer?
     *
     * @param verzeichnis: Ordner des aktuellen Projektes
     * @return boolean: leer, ja oder nein
     */
    public boolean hatBisherKeineDateien(File verzeichnis) {
        return Objects.requireNonNull(verzeichnis.list()).length == 0;
    }

}
