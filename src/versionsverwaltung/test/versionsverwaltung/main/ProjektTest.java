package versionsverwaltung.main;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjektTest {

    Projekt projekt = new Projekt();
    private static File projektVerzeichnis;

    @BeforeAll
    static void setupTest() {

    }

    @Test
    void neuesProjektAnlegen() {
        projektVerzeichnis = projekt.neuesProjektAnlegen("test");

        // I.d.R sollte ein Anlegen einer Datei möglich sein, da der Pfad individuell angepasst wird
        assertTrue(projektVerzeichnis.exists());
        assertTrue(projektVerzeichnis.isDirectory());
    }

    @AfterAll
    static void aufraeumen() {
        // Anschließend den angelegten Ordner wieder entfernen
        projektVerzeichnis.delete();
    }

}