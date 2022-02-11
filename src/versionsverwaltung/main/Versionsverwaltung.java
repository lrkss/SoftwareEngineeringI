package versionsverwaltung.main;

import java.io.File;
import java.util.Scanner;

/**
 * Main-Klasse des Versionsverwaltungsprogrammes
 */
public class Versionsverwaltung {

    public static void main(String[] args) {

        Projekt projekt = new Projekt();
        projekt.begruessung();

        System.out.println("Welches Projekt möchten Sie bearbeiten? Geben Sie einen der obigen Projektnamen an oder " +
                "erstellen Sie ein neues Projekt, indem sie einen neuen Projektnamen eingeben.");
        System.out.println("Wir legen das Verzeichnis dann neu für Sie an.");

        Scanner sc = new Scanner(System.in);
        String projektname = sc.nextLine();

        // Hier wird ein neues Projekt angelegt, wenn es noch keins gibt.
        File projektVerzeichnis = projekt.anlegenMitFolgendemNamen(projektname);

        // Um nun mit Dateien in dem Projekt arbeiten zu können, wird ein neues Datei-Objekt erstellt.
        Datei datei = new Datei(projektVerzeichnis);

        if (projektVerzeichnis.exists() && projekt.hatBisherKeineDateienIn(projektVerzeichnis)) {
            datei.inEinemLeerenVerzeichnisAnlegen();
        }

        // Ab hier wird mit den Dateien in einem ausgewählten Projekt gearbeitet.
        datei.auslesen();
    }
}
