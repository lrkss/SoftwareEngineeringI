package versionsverwaltung.main;

import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Die Test-Klasse kann vorerst nicht automatisiert werden, da eine Speichern-Aktion vom User benötigt wird.
 */
class DateiTest {

    private static Datei datei;
    private static File file;
    String dateiname = "testDatei";
    static String ordnername = "testOrdner";

    @BeforeAll
    static void setupTest() {
        // Für das Testing-Setup wird ein Projektordner benötigt, der am Schluss leicht wieder entfernt werden kann
        Projekt projekt = new Projekt();
        file = new File(String.valueOf(projekt.neuesProjektAnlegen(ordnername)));
        datei = new Datei(file);
    }

    @BeforeEach
        // Die Klasse kann nicht automatisiert werden, da ein manuelles Speichern gefordert wird
    void dateiErstellen() {
        datei.neueDateiAnlegen(dateiname);
    }

    @Test
    void neueDateiImProjektAnlegenUndFinden() {
        String methodenDateiname = "dateiAnlegenTest";
        // Zunächst gibt es noch keine Datei mit dem Namen "testDatei"
        Assertions.assertNull(datei.dateipfadFinden(file, methodenDateiname, 1));

        // Nun wird eine entsprechende Datei mit dem Namen "testDatei" in den Ordner "testOrdner" angelegt
        datei.neueDateiAnlegen(methodenDateiname);

        // Nun gibt es eine Date mit dem Namen "testDatei" im Projektordner "testOrdner
        Assertions.assertTrue(datei.dateipfadFinden(file, methodenDateiname, 1).endsWith(methodenDateiname + "_1.txt"));
    }

    @Test
    void isDateiSchonEnthalten() {
        // Legt die gesuchte Datei in BeforeEach an und findet sie
        Assertions.assertTrue(datei.isDateiSchonEnthalten(file, "testDatei", 1));
    }

    void bestehendeDateiOeffnen() {
        // Der Test dafür fiel wieder weg, da die komplette Methode bzw. Funktionalität nur mit expliziten User-Eingaben
        // getestet werden kann und das keinen Sinn ergibt.
        // Alle darin enthaltenen Methoden werden hier separat getestet.
    }

    @Test
    void sperreSetzen() {
        // Die in BeforeEach angelegte Datei beseitzt noch keine Sperren
        File methodenFile = new File(datei.dateipfadFinden(file, dateiname, 1));
        Assertions.assertTrue(methodenFile.canWrite());

        // Nach setzen der Sperre kann dies auch abgefragt werden
        datei.dateiMitSperreBelegen(methodenFile);
        Assertions.assertFalse(methodenFile.canWrite());
    }

    @Test
    void sperreLoesen() {
        // Die in BeforeEach angelegte Datei beseitzt noch keine Sperren
        File methodenFile = new File(datei.dateipfadFinden(file, dateiname, 1));
        Assertions.assertTrue(methodenFile.canWrite());

        // Nach setzen der Sperre kann dies auch abgefragt werden
        datei.dateiMitSperreBelegen(methodenFile);
        Assertions.assertFalse(methodenFile.canWrite());

        // Sperre ist wieder gelöst
        datei.sperreLoesen(methodenFile);
        Assertions.assertTrue(methodenFile.canWrite());
    }

    @Test
    void fileErmitteln() {
        String zielFile = new File(System.getProperty("user.dir") + "/dateiablage/" + ordnername + "/" + dateiname + "_1.txt").getAbsolutePath();
        Assertions.assertEquals(zielFile, datei.fileErmitteln(null, null, dateiname + "_1.txt").getAbsolutePath());
        Assertions.assertEquals(zielFile, datei.fileErmitteln(Paths.get(file.getAbsolutePath()), null, dateiname + "_1.txt").getAbsolutePath());
        Assertions.assertEquals(zielFile, datei.fileErmitteln(null, file.getAbsolutePath(), dateiname + "_1.txt").getAbsolutePath());
        Assertions.assertEquals(zielFile, datei.fileErmitteln(Paths.get(file.getAbsolutePath(), dateiname + "_1.txt"), null, null).getAbsolutePath());
        Assertions.assertEquals(zielFile, datei.fileErmitteln(null, file.getAbsolutePath() + "\\" + dateiname + "_1.txt", null).getAbsolutePath());
    }

    @Test
    void dateiVersionHochzaehlen() {
        datei.dateiVersionHochzaehlen(new File(datei.dateipfadFinden(file, dateiname, 1)));
        Assertions.assertEquals("testDatei_2.txt", new File(datei.fileErmitteln(Paths.get(file.getAbsolutePath()), null, dateiname + "_2.txt").getAbsolutePath()).getName());
    }

    @AfterEach
    void nachMethodeAufraumen() {
        // Nach jeder Methode die angelegten Dateien aus dem Test-Projekt entfernen
        for (File file : Objects.requireNonNull(file.listFiles())) {
            file.delete();
        }
    }

    @AfterAll
    static void aufraeumen() {
        // Abschließend den angelegten Projekt-Ordner ebenfalls entfernen
        file.delete();
    }
}
