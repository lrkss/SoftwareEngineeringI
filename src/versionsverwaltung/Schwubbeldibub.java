package versionsverwaltung;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

public class Schwubbeldibub {
    private final Path LOKALERPFAD = Path.of("/dateiablage");
//    private final Path LOKALERPFAD = Paths.get("C:/src/studium/SoftwareEngineeringI/dateiablage");
    private Path SESSIONPFAD;

    public static void main(String[] args){
        System.out.print(Paths.get(Paths.get("C:/src/studium/SoftwareEngineeringI/dateiablage").toString(),"ordner"));
    //C:\src\studium\SoftwareEngineeringI\dateiablage\ordner
//        File file = new File(String.valueOf(Paths.get(Paths.get("C:/src/studium/SoftwareEngineeringI/dateiablage").toString(),"ordner")));
        File file = new File(String.valueOf(System.getProperty("user.dir")), "ordner");
 //       Path dir = Files.createDirectory(Paths.get("/dateiablage"),"ordner"));
 //       Path copy = dir.resolve()
        file.mkdir();
        System.out.print(file.getAbsolutePath());
    }

    public boolean neuesProjektanlegen(String projektname){
        Path ordnername = Paths.get(projektname);
        erstelleNeuenOrdnerMitProjektname(ordnername);
        merkeDenProjektnamenFuerLaufendeSession(ordnername);
        return true;
    }

    private void erstelleNeuenOrdnerMitProjektname(Path ordnername) {
        if (!Files.exists(ordnername)) {

//            Files.createDirectory(Path.of("/src/studium/SoftwareEngineeringI/dateiablage"));

            File file = new File(SESSIONPFAD.toString());
            file.mkdir();

            System.out.println("Directory created");
        } else {

            System.out.println("Directory already exists");
        }
    }

    private void merkeDenProjektnamenFuerLaufendeSession(Path ordnername) {
        SESSIONPFAD = Paths.get(LOKALERPFAD.toString(),ordnername.toString());
    }


}
