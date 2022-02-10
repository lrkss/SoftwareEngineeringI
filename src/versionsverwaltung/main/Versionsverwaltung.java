package versionsverwaltung.main;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Versionsverwaltung {

    public static void main(String[] args) {

        Projektanlage projekt = new Projektanlage();
        projekt.begruessung();

        System.out.println("Welches Projekt möchten Sie bearbeiten? Geben Sie einen der obigen Projektnamen an oder " +
                "erstellen Sie ein neues Projekt, indem sie einen neuen Projektnamen eingeben.");
        System.out.println("Wir legen das Verzeichnis dann neu für Sie an.");

        Scanner sc = new Scanner(System.in);
        String projektname = sc.nextLine();

        File projektVerzeichnis = projekt.neuesProjektAnlegen(projektname);
        Datei datei = new Datei();

        if(projektVerzeichnis.exists() && projekt.hatBisherKeineDateien(projektVerzeichnis)){
            datei.ersteDateiInEinemLeerenVerzeichnisAnlegen(projektVerzeichnis);
        }

        while (true) {
            datei.dateiAuslesen(projektVerzeichnis);
            datei.dateiOeffnen(System.getProperty("user.dir") + "/dateiablage/test/test_1.txt"); // TODO: Später rausnehmen
        }
    }


}
