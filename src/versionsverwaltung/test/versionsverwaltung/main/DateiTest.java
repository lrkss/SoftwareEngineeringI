package versionsverwaltung.main;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

class DateiTest {

    Datei datei = new Datei();
    private static File file;
    String dateiname = "testDatei";
    static String ordnername = "testOrdner";


    @BeforeAll
    static void setupTest() {
        // Für das Testing-Setup wird ein Projektordner benötigt, der am Schluss leicht wieder entfernt werden kann
        Projektanlage projekt = new Projektanlage();
        file = new File(String.valueOf(projekt.neuesProjektAnlegen(ordnername)));
    }

    @Test
    void neueDateiImProjektAnlegenUndFinden() {
        // Zunächst gibt es noch keine Datei mit dem Namen "testDatei"
        Assertions.assertNull(datei.dateipfadFinden(file, dateiname, 1));

        // Nun wird eine entsprechende Datei mit dem Namen "testDatei" in den Ordner "testOrdner" angelegt
        datei.neueDateiAnlegen(file, dateiname);

        // Nun gibt es eine Date mit dem Namen "testDatei" im Projektordner "testOrdner
        Assertions.assertTrue(datei.dateipfadFinden(file, dateiname, 1).endsWith(dateiname + "_1.txt"));
    }

    @Test
    void isDateiSchonEnthalten(){
        datei.neueDateiAnlegen(file, dateiname);
        Assertions.assertTrue(datei.isDateiSchonEnthalten(file, "testDatei", 1));
    }

    @Test
    void bestehendeDateiOeffnen() {
        String dateipfad = datei.dateipfadFinden(file, "testDatei", 1);
        datei.dateiOeffnen(dateipfad);
        Assertions.assertFalse(new File(dateipfad).canWrite());
    }

    @Test
    void fileErmitteln() {
        String zielFile = new File(System.getProperty("user.dir") + "/dateiablage/" + ordnername + "/" + dateiname + "_1.txt").getAbsolutePath();
        Assertions.assertEquals(zielFile, datei.fileErmitteln(file, null, null, dateiname + "_1.txt").getAbsolutePath());
        Assertions.assertEquals(zielFile, datei.fileErmitteln(null, Paths.get(file.getAbsolutePath()), null, dateiname + "_1.txt").getAbsolutePath());
        Assertions.assertEquals(zielFile, datei.fileErmitteln(null, null, file.getAbsolutePath(), dateiname + "_1.txt").getAbsolutePath());
        Assertions.assertEquals(zielFile, datei.fileErmitteln(null, Paths.get(file.getAbsolutePath(), dateiname + "_1.txt"), null, null).getAbsolutePath());
        Assertions.assertEquals(zielFile, datei.fileErmitteln(null, null, file.getAbsolutePath() + "\\" + dateiname + "_1.txt", null).getAbsolutePath());
    }

    @Test
    void dateiVersionHochzaehlen(){
        datei.dateiVersionHochzaehlen(file);
        Assertions.assertEquals("testDatei_2.txt", file.getName());
    }

    @AfterAll
    static void aufraeumen() {
        // Abschließend alle angelegten Dateien inkl. des angelegten zugehörigen Ordners wieder entfernen
        for (File file : Objects.requireNonNull(file.listFiles())) {
            file.delete();
        }
        file.delete();
    }

}
