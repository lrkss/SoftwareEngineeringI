package versionsverwaltung.main;

import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Replace;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class Projekt {

    private final Logger log = LoggerFactory.getLogger(Versionsverwaltung.class);

    public final Path LOKALERPFAD = Paths.get(System.getProperty("user.dir") + "/dateiablage");

    /**
     * Aus dem individuellen Projectpath und der Eingabe des Users wird ein Pfad erstellt,
     * in dem gesucht wird, ob es einen Ordner mit dem Namen schon gibt.
     * @param projektname
     * @return Path
     */
    public Path neuesProjekt(String projektname) {
        Path sessionpfad = Paths.get(LOKALERPFAD.toString(), projektname);
        return erstelleNeuenOrdnerMitProjektname(sessionpfad, projektname);
    }

    /**
     * Es wird ein neuer Ordner im Standardverzeichnis erstellt, mit dem entsprechenden Projektnamen.
     * @param sessionpfad
     * @param projektname
     * @return Path
     */
    private Path erstelleNeuenOrdnerMitProjektname(Path sessionpfad, String projektname) {
        File file = new File(sessionpfad.toString());
        if (file.mkdir()) {
            log.info(MessageFormat.format("Verzeichnis {0} erfolgreich erstellt", projektname));
        } else {
            log.info(MessageFormat.format("{0} existiert bereits", projektname));
        }
        return sessionpfad;
    }

    /**
     * Findet den Pfad eines bestehenden Projektes.
     * @param file
     * @param name
     * @return String
     */
    @Deprecated
    protected String projektpfadFinden(File file, String name) {
        File[] list = file.listFiles();
        if (list != null) {
            for (File fil : list) {
                if (fil.isDirectory() && name.equals(fil.getName())) {
                    return fil.getAbsolutePath();
                }
            }
        }
        return null;
    }

}
