package versionsverwaltung.main;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Hier werden alle Interaktionen mit den Dateien eines Projektes gesteuert bzw. verwaltet.
 */
public class Datei {

    private final Logger log = LoggerFactory.getLogger(Versionsverwaltung.class);
    private final Scanner sc = new Scanner(System.in);

    private final File projektPfad;

    public Datei(File projektPfad) {
        this.projektPfad = projektPfad;
    }

    /**
     * Übergeordnete Funktion mit Nutzerabfrage zum gewünschten Vorgehen. Ruft sich abschließend selbst auf,
     * um nach dem schließen einer Datei weitermachen zu können, ohne dass das Programm neu starten zu müssen.
     * Das Programm wird also nur beeendet, wenn dies explizit vom Nutzer angegeben wird.
     */
    public void auslesen() {
        System.out.println("Wollen Sie in Ihrem Projekt eine vorhandene Datei öffnen, " +
                "eine neue erstellen oder Ihren laufenden Task beenden?");
        String eingabe = sc.nextLine();

        if (eingabe.contains("öffnen")) {
            oeffnen();

        } else if (eingabe.contains("erstellen") || eingabe.contains("neu")) {

            System.out.println("Wie soll Ihre neue Datei heißen? (Bitte weder Punkt noch Unterstrich verwenden)");
            String dateiname = sc.nextLine();
            String neuerDateiname = dateiname;

            if (dateiname.contains(".") || dateiname.contains("_")) {
                neuerDateiname = dateiname.replace(".", "").replace("_", "");

                System.out.println("Ihr Dateiname enthielt unerlaubte Zeichen und wurde geändert zu: " + neuerDateiname);
            }

            neuAnlegenMitForgendemNamen(neuerDateiname);

        } else if (eingabe.contains("beenden") || eingabe.contains("abbrechen")) {
            System.out.println("Die Anwendung wird nun beendet.");
            System.exit(0);
        }

        auslesen();
    }

    /**
     * Das Öffnen einer Datei unterscheidet zwischen zwei Fällen:
     * a) es soll eine Datei geöffnet werden, die bearbeitbar ist: Hier wird zunächst eine Kopie erstellt,
     * anschließend die alte Version für Bearbeitung gesperrt und die neue Version geöffnet.
     * b) es soll eine Datei geöffnet werden, die nicht bearbeitbar ist: Der Inhalt der Datei wird in der Konsole
     * ausgegeben und ist so einsehbar, kann aber nicht bearbeitet werden.
     */
    private void oeffnen() {
        File file = new File(Objects.requireNonNull(anhandVonNameUndVersionFinden()));

        if (file.canWrite()) {
            File kopie = null;
            try {
                // Datei kopieren (v2) -> dateiname + _ + versionsnummer+1 + .txt
                kopie = new File(String.valueOf(Paths.get(projektPfad.getCanonicalPath(), dateiVersionHochzaehlen(file))));
                Files.copy(file.toPath(), kopie.toPath());
            } catch (Exception e) {
                // Falls keine Schreibrechte vorhanden sind, oder der Pfad nicht existiert, loggen wir den Fehler
                log.error(e.getMessage());
            }

            // alte Datei sperren (v1)
            mitSperreBelegen(file);

            inEditorOeffnen(kopie);

        } else {
            // Read-Only Dateien auslesen in Konsole
            System.out.println("\n Inhalt der " + file.getName() + "-Datei:");
            try (BufferedReader br = new BufferedReader(new FileReader(file.getCanonicalPath()))) {
                String zeile = "";
                while ((zeile = br.readLine()) != null) {
                    System.out.println(zeile);
                }
            } catch (Exception e) {
                log.error("Ein Fehler ist beim Auslesen der Datei aufgetreten.");
            }
            System.out.println("");
        }
    }

    protected void mitSperreBelegen(File file) {
        boolean isReadOnly = file.setWritable(false);
        if (isReadOnly) {
            log.info("Die Datei wurde erfolgreich readonly auf: " + String.valueOf(isReadOnly) + "gesetzt.");
        } else {
            log.error("Die Datei wurde nicht erfolgreich readonly auf: " + String.valueOf(isReadOnly) + "gesetzt.");
        }
    }

    protected void sperreLoesen(File file) {
        boolean isReadOnly = file.setWritable(true);
        if (!isReadOnly) {
            log.info("Die Datei wurde erfolgreich entsperrt.");
        } else {
            log.error("Die Datei wurde nicht erfolgreich entsperrt.");
        }
    }

    /**
     * Öffnet eine Datei im Editor.
     * Mittels Watcher wird überwacht, wann die Datei gespeichert wurde. Nach dem Speichern wird der Editor geschlossen.
     */
    private void inEditorOeffnen(File kopie) {
        System.out.println("Wir öffnen Ihre Datei im Editor. Sobald Sie ihre Änderungen speichern, schließen wir den " +
                "Editor für Sie und legen Ihre Änderungen mit einer neuen Versionsnummer ab.");
        System.out.println("Die neuste Version kann nun von allen Nutzern bearbeitet werden.");

        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String cmd = "rundll32 url.dll,FileProtocolHandler " + kopie.getCanonicalPath();
                // Kopie (v2) statt Datei (v1) öffnen
                Runtime.getRuntime().exec(cmd);
            } else {
                Desktop.getDesktop().edit(kopie);
            }

            // Dateipfad des aktuellen Projektes
            Path path = Paths.get(kopie.getParent());

            // Der Watcher war dafür gedacht, dass wir das Beenden der Dateibearbeitung tracken und die Sperre entsprechend lösen können
            // Unter Windows kann allerdings nur auf save, delete und create getrackt werden. Daher wird auf das Speichern gehorcht und
            // dann der Editor via kill-Befehl geschlossen.
            try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
                // Wenn Entry_Modify, dann Kopie mit neuer Versionsnummer speichern
                // Wenn nur schließen, dann Kopie löschen und Sperre des Originals entfernen
                final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                boolean passendeDateiGespeichert = false;
                while (!passendeDateiGespeichert) {

                    final WatchKey wk = watchService.take();

                    for (WatchEvent<?> event : wk.pollEvents()) {
                        final Path changed = (Path) event.context();

                        // Gibt den Dateipfad zurück, welche Datei gespeichert und demnach aktualisiert wurde
                        log.info("Änderung gespeichert als " + changed);
                        if (changed.endsWith(kopie.getName())) {
                            // hier hätte man die Sperre lösen können, wenn Windows da nicht zwischen gehen würde
                            // kopie.setWritable(true);
                            passendeDateiGespeichert = true;

                            // Datei automatisch schließen.
                            // Als schnellen fix für einen Bug, haben wir ein Timeout eingebaut. Der Kill-Prozess läuft leider nur unzuverlässig
                            TimeUnit.SECONDS.sleep(2);
                            Runtime.getRuntime().exec("taskkill /f /im notepad.exe");
                        }
                    }
                    // reset the key
                    boolean valid = wk.reset();
                    if (!valid) {
                        log.error("Key has been unregistered");
                    }
                }
            } catch (IOException | InterruptedException ex) {
                log.error(ex.getMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Gibt alle Dateien eines Projekts in der Konsole aus. Der Nutzer entscheidet, welche Datei geöffnet werden soll.
     * Falls vorhanden, wird der Pfad der ausgewählten Datei zurückgegeben. Falls nicht auffindbar, wird der
     * Datei-Auslese-Prozess erneut gestartet.
     *
     * @return Falls Datei gefunden wird, wird der Dateipfad zurückgegeben, ansonsten null.
     */
    private String anhandVonNameUndVersionFinden() {
        System.out.println("Im Folgenden finden Sie alle Dateien des aktuellen Projektes aufgeführt:");

        // Nur den wichtigen ersten Teil des Dateinamens anzeigen - einmalig, ohne Versionsnummer
        Arrays.stream(Objects.requireNonNull(projektPfad.list()))
                .map(d -> d.substring(0, d.indexOf("_")))
                .collect(Collectors.toSet())
                .forEach(datei -> System.out.println("  - " + datei));

        System.out.println("Bitte geben Sie an welche Datei Sie öffnen möchten.");
        String dateiname = sc.nextLine();

        int anzahl = (int) Arrays.stream(Objects.requireNonNull(projektPfad.list()))
                .filter(d -> d.contains(dateiname))
                .count();
        System.out.println("Für die Datei '" + dateiname + "' liegen " + anzahl + " Versionen vor.");
        System.out.println("1 ist die älteste Version. " + anzahl + " ist die neuste Version.");

        System.out.println("Bitte geben Sie an welche Version der Datei Sie öffnen möchten. (z. B. 1)");
        int version = sc.nextInt();

        String dateipfad = dateipfadFinden(projektPfad, dateiname, version);
        if (dateipfad != null) {
            return dateipfad;
        } else {
            System.out.println("Ihre Eingabe konnte leider keiner vorhandenen Datei zugeordnet werden." +
                    "Bitte treffen Sie Ihre Wahl erneut.");
            auslesen();
        }
        return null;
    }

    /**
     * Zählt die Version der Datei hoch.
     *
     * @param datei Dateiname
     * @return neuer Dateiname
     */
    protected String dateiVersionHochzaehlen(File datei) {
        String[] dateinameArray = datei.getName().split("[_.]");
        int i = Integer.parseInt(dateinameArray[1]) + 1;
        dateinameArray[1] = String.valueOf(i);
        return dateinameArray[0] + "_" + dateinameArray[1] + "." + dateinameArray[2];
    }

    /**
     * Für die initiale Dateianlage in einem neuen Projekt weichen die Auswahlfunktionen ein wenig vom regulären
     * Verhalten ab. Falls nach neuer Projektanlage keine neue Datei angelegt wird, wird das Programm automatisch beendet.
     */
    public void inEinemLeerenVerzeichnisAnlegen() {
        System.out.println("Ihr Projekt '" + projektPfad.getName() + "' enthält noch" +
                " keine Dateien. Möchten Sie jetzt eine neue Datei anlegen?");
        String sollNeueDateiAngelegtWerden = sc.nextLine();
        if (sollNeueDateiAngelegtWerden.contains("ja")) {
            System.out.println("Wie soll die Datei heißen?");
            String dateiName = sc.nextLine();
            Datei datei = new Datei(projektPfad);
            datei.neuAnlegenMitForgendemNamen(dateiName);

        } else if (sollNeueDateiAngelegtWerden.contains("nein")) {
            System.out.println("Es wurde keine neue Datei angelegt. Die Anwendung wird nun beendet.");
            System.exit(0);
        }
    }


    /**
     * Erstellt aus verschiedenen Parametern in Kombination eine neue Datei.
     * ACHTUNG: dateiname bitte korrekt angeben.
     *
     * @param dateiname: Dateinamen der neuen Datei
     */
    protected void neuAnlegenMitForgendemNamen(String dateiname) {
        int version = 1;
        File neueDatei = fileErmitteln(null, null, dateiname + "_" + version + ".txt");
        try {
            // Neue Datei anlegen, wenn diese noch nicht vorhanden ist
            if (isDateiSchonEnthalten(projektPfad, dateiname, version)) {
                log.info("Datei existiert bereits.");
                System.out.println("Eine Datei mit diesem Namen existiert bereits. Bitte wählen Sie einen anderen Namen.");
            } else {
                if (neueDatei.createNewFile()) {
                    neueDatei.setWritable(true);
                    neueDatei.setExecutable(true);
                    log.info("Datei wurde erfolgreich erstellt.");

                    // Erste Datei auch direkt öffnen
                    File file = new File(dateipfadFinden(projektPfad, dateiname, version));
                    inEditorOeffnen(file);
                }
            }
        } catch (Exception e) {
            // Falls keine Schreibrechte vorhanden sind, oder der Pfad nicht existiert, loggen wir den Fehler
            log.error(e.getMessage());
        }
    }

    /**
     * Erstellt aus verschiedenen Parametern in Kombination eine neue File, um mit dieser weiterzuarbeiten.
     * ACHTUNG: dateiname bitte korrekt angeben.
     *
     * @param path:      Ordnerpfad in dem gesucht werden soll
     * @param pfadname:  Name des Ordners
     * @param dateiname: Name der Datei
     * @return File: File-Objekt aus verschiedenen gegebenen Parametern bzw. Infos einer Datei
     */
    protected File fileErmitteln(Path path, String pfadname, String dateiname) {
        if (dateiname != null) {
            if (projektPfad != null) {
                return Paths.get(projektPfad.getAbsolutePath(), dateiname).toFile();
            } else if (path != null) {
                return Paths.get(path.toString(), dateiname).toFile();
            } else if (pfadname != null) {
                return Paths.get(pfadname, dateiname).toFile();
            }
        } else {
            if (path != null) {
                return path.toFile();
            } else if (pfadname != null) {
                return Paths.get(pfadname).toFile();
            }
        }
        return null;
    }

    /**
     * Gibt den Pfad zurück, wo die gesuchte Datei liegt.
     *
     * @param file:    Ordner in der, die Datei liegen sollte
     * @param name:    Name der Datei
     * @param version: Version der Datei
     * @return String: Dateipfad der Datei als String
     */
    protected String dateipfadFinden(File file, @NotNull String name, int version) {
        File[] list = file.listFiles();
        if (list != null) {
            for (File fil : list) {
                // Den Dateinamen auseinander splitten, um Name und Version einzeln zu checken
                String[] tokens = fil.getName().split("[_.]");
                if (fil.isDirectory()) {
                    dateipfadFinden(fil, name, version);
                } else if ((name).equals(tokens[0]) && version == Integer.parseInt(tokens[1])) {
                    return fil.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     * Prüft, ob in diesem Projekt bereits eine Datei mit dem @param name existiert.
     *
     * @param file: Ordner der zu suchenden Datei
     * @param name: Name der zu suchenden Datei @NotNull
     * @return boolean: Ist die Datei schon in dem Ordner enthalten?
     */
    protected boolean isDateiSchonEnthalten(File file, @NotNull String name, int version) {
        File[] list = file.listFiles();
        if (list != null) {
            for (File fil : list) {
                String[] tokens = fil.getName().split("[_.]");
                if (fil.isDirectory()) {
                    isDateiSchonEnthalten(fil, name, version);
                } else if (name.equals(tokens[0])) {
                    return true;
                }
            }
        }
        return false;
    }
}
