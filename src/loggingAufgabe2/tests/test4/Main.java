package loggingAufgabe2.tests.test4;

import loggingAufgabe2.kubik.Heron;
import loggingAufgabe2.quadrat.Newton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class.getName());

	public static void main(String[] args) {

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
