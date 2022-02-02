package versionsverwaltung.main;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjektTest {

    private static File projektFile;
    Projekt projekt = new Projekt();
    Path projektPfad;

    @BeforeAll
    static void setupTest() {

    }

    @Test
    void neuesProjektAnlegen() {
        projektPfad = projekt.neuesProjekt("test");
        projektFile = new File(String.valueOf(projektPfad));

        // I.d.R sollte ein Anlegen einer Datei möglich sein, da der Pfad individuell angepasst wird
        assertTrue(projektFile.exists());
        assertTrue(projektFile.isDirectory());
    }

    @AfterAll
    static void aufraeumen() {
        // Anschließend den angelegten Ordner wieder entfernen
        projektFile.delete();
    }

}