package uebungsaufgaben.loggingAufgabe2.tests.test1;

import uebungsaufgaben.loggingAufgabe2.kubik.Heron;
import uebungsaufgaben.loggingAufgabe2.quadrat.Newton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.LogManager;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class.getName());

	public static void main(String[] args) throws IOException {

		LogManager.getLogManager().readConfiguration(
				new FileInputStream("src/conf/test1.properties"));

		double genauigkeit = 0.00000001;
		double zahl;
		Scanner scanner = new Scanner(System.in);
		Newton newton = new Newton();
		Heron heron = new Heron();
		do {
			log.info("Basiszahl: ");
			zahl = scanner.nextDouble();
			try {
				log.info("Kubikwurzel aus {}: {}", zahl, heron.kubikwurzel(zahl,genauigkeit));
				log.info("Wurzel aus {}: {}", zahl, newton.wurzel(zahl,genauigkeit));
			}
			catch (Exception ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
			}
		}
		while (zahl != 0.0);
		log.info("Eingabe war 0. Schließe das Programm...");
		scanner.close();
	}
}
