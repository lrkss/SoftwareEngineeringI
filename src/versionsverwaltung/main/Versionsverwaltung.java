package versionsverwaltung.main;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

public class Versionsverwaltung {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("Guten Tag, welches Projekt suchen Sie? Geben Sie den den Namen eines bestehendes Projekts an. " +
                "Wenn es dieses noch nicht gibt, legen wir für Sie ein neues Projekt an.");
        String projektname = sc.nextLine();

        Projekt projekt = new Projekt();
        Path SESSIONPFAD = projekt.neuesProjekt(projektname);
        File projektPfad = new File(String.valueOf(SESSIONPFAD));

        Datei datei = new Datei();
        while (true) {
            datei.dateiAuslesen(projektPfad);
            datei.dateiOeffnen(System.getProperty("user.dir") + "/dateiablage/test/test_1.txt"); // TODO: Später rausnehmen
        }
    }


}
