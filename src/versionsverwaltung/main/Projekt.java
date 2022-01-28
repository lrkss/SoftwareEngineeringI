package versionsverwaltung.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class Projekt {

    private final Logger log = LoggerFactory.getLogger(Versionsverwaltung.class);

    private final Path LOKALERPFAD = Paths.get(System.getProperty("user.dir") + "/dateiablage");
    private Path SESSIONPFAD;

    public boolean neuesProjektAnlegen(String projektname) {
        Path ordnername = Paths.get(projektname);
        merkeDenProjektnamenFuerLaufendeSession(ordnername);
        return erstelleNeuenOrdnerMitProjektname(ordnername);
    }

    private boolean erstelleNeuenOrdnerMitProjektname(Path ordnername) {
        if (!Files.exists(ordnername)) {

            File file = new File(SESSIONPFAD.toString());
            file.mkdir();

            log.info(MessageFormat.format("Verzeichnis {0} erfolgreich erstellt", ordnername));
            return true;
        } else {
            log.error(MessageFormat.format("{0} existiert bereits", ordnername));
            return false;
        }
    }

    private void merkeDenProjektnamenFuerLaufendeSession(Path ordnername) {
        SESSIONPFAD = Paths.get(LOKALERPFAD.toString(), ordnername.toString());
    }
}
