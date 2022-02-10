package versionsverwaltung.main;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Scanner;


public class Datei {

    private final Logger log = LoggerFactory.getLogger(Versionsverwaltung.class);
    private final Scanner sc = new Scanner(System.in);

    /**
     * Übergeordnete Funktion mit Nutzerabfrage zum gewünschten Vorgehen. Ruft sich abschließend selbst auf,
     * um nach dem schließen einer Datei weitermachen zu können, ohne dass das Programm neu starten zu müssen.
     * Das Programm wird also nur beeendet, wenn dies explizit vom Nutzer angegeben wird.
     */
    public void dateiAuslesen() {
        System.out.println("Wollen Sie in Ihrem Projekt eine vorhandene Datei öffnen, " +
                "eine neue erstellen oder Ihren laufenden Task beenden?");
        String eingabe = sc.nextLine();

        if (eingabe.contains("öffnen")) {
            dateiOeffnen(dateiAnhandVonNameUndVersionFinden(projektPfad));

        } else if (eingabe.contains("erstellen") || eingabe.contains("neu")) {

            System.out.println("Wie soll Ihre neue Datei heißen? (Bitte weder Punkt noch Unterstrich verwenden)");
            String dateiname = sc.nextLine();
            String neuerDateiname = dateiname;

            if (dateiname.contains(".") || dateiname.contains("_")) {
                neuerDateiname = dateiname.replace(".", "").replace("_", "");

                System.out.println("Ihr Dateiname enthielt unerlaubte Zeichen und wurde geändert zu: " + neuerDateiname);
            }

            neueDateiAnlegen(projektPfad, neuerDateiname);

        } else if (eingabe.contains("beenden") || eingabe.contains("abbrechen")) {
            System.exit(0);
        }
    }

    protected void dateiOeffnen(String dateiPfad) {
        File file = new File(dateiPfad);

        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
                Runtime.getRuntime().exec(cmd);
            } else {
                Desktop.getDesktop().edit(file);
//                file.setReadOnly();
            }
            // TODO: Desktop.getDesktop().open(file); -> Dateien die eine Sperre haben oder nicht die neuste Versionsnummer
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        //todo watcher
        // File.renameTo() -> Versionsnummer nach bearbeiten der Datei: bisherige datei kopieren und an ende zahl
        // z.B. 1.0 hochzählen -> 1.1
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();

//            Path dir = FileSystems.getDefault().getPath("khkh");
//            WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            Path path = Paths.get(System.getProperty("user.home"));

            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;

            while ((key = watcher.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    Object o = event.context();
                    if (o instanceof Path) {
                        System.out.println("Path altered: " + o);
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException ex) {
            log.error(ex.getMessage());
        }

    }

    private String dateiAnhandVonNameUndVersionFinden(File projektPfad) {
        System.out.println("Im Folgenden finden Sie alle Dateien des aktuellen Projektes aufgeführt:");

        // Nur den wichtigen ersten Teil des Dateinamens anzeigen - einmalig, ohne Versionsnummer
        Arrays.stream(Objects.requireNonNull(projektPfad.list()))
                .map(d -> d.substring(0, d.indexOf("_")))
                .collect(Collectors.toSet())
                .forEach(datei -> System.out.println("  - " + datei));

        System.out.println("Bitte geben Sie an welche Datei Sie öffnen möchten.");
        String dateiname = sc.nextLine();

        System.out.println("Bitte geben Sie auch an welche Version Sie von der Datei öffnen möchten. (z. B. 1)");
        int version = sc.nextInt();

        String dateipfad = dateipfadFinden(projektPfad, dateiname, version);
        if (dateipfad != null) {
            return dateipfad;
        } else {
            System.out.println("Ihre Eingabe konnte leider keiner vorhandenen Datei zugeordnet werden." +
                    "Bitte treffen Sie Ihre Wahl erneut.");
            dateiAnhandVonNameUndVersionFinden(projektPfad);
        }
        return null;
    }

    protected void dateiVersionHochzaehlen(File datei) {
        String[] dateinameArray = datei.getName().split("[_.]");
        int i = Integer.parseInt(dateinameArray[2]) + 1;
        dateinameArray[2] = String.valueOf(i);
        File neueDatei = new File(dateinameArray[0] + "_" + dateinameArray[1] + "." + dateinameArray[2]);
        datei.renameTo(neueDatei);
    }

    public void ersteDateiInEinemLeerenVerzeichnisAnlegen(File projektname){
        Scanner sc = new Scanner(System.in);
        System.out.println("Ihr Projekt '" + projektname.getName() + "' enthält noch" +
                " keine Dateien. Möchten Sie jetzt eine neue Datei anlegen?");
        String sollNeueDateiAngelegtWerden = sc.nextLine();
        if (sollNeueDateiAngelegtWerden.contains("ja")) {
            System.out.println("Wie soll die Datei heißen?");
            String dateiName = sc.nextLine();
            Datei datei = new Datei();
            datei.neueDateiAnlegen(projektname,dateiName);

        } else if(sollNeueDateiAngelegtWerden.contains("nein")){
            System.out.println("Es wurde keine neue Datei angelegt. Die Anwendung wird nun beendet.");
            System.exit(0);
        }
    }


    /**
     * Erstellt aus verschiedenen Parametern in Kombination eine neue Datei.
     * ACHTUNG: dateiname bitte korrekt angeben.
     *
     * @param dateiname
     * @param projektPfad
     * @return File
     */
    protected void neueDateiAnlegen(File projektPfad, String dateiname) {
        int version = 1;
        File neueDatei = fileErmitteln(projektPfad, null, null, dateiname + "_" + version + ".txt");
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
     * @param file
     * @param path
     * @param pfadname
     * @param dateiname
     * @return File
     */
    protected File fileErmitteln(File file, Path path, String pfadname, String dateiname) {
        if (dateiname != null) {
            if (file != null) {
                return Paths.get(file.getAbsolutePath(), dateiname).toFile();
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
     * @param file
     * @param name    @NotNull
     * @param version
     * @return String
     */
    protected String dateipfadFinden(File file, @NotNull String name, int version) {
        File[] list = file.listFiles();
        if (list != null) {
            for (File fil : list) {
                // Den Dateinamen auseinenader splitten, um Name und Version einzeln zu checken
                String[] tokens = fil.getName().split("[_.]");
                if (fil.isDirectory()) {
                    dateipfadFinden(fil, name, version);
//                } else if ((name + ".txt").equals(fil.getName())) {
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
     * @param file
     * @param name @NotNull
     * @return String
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
