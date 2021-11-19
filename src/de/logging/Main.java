package de.logging;

import java.util.Scanner;
import de.logging.quadrat.Newton;

public class Main {

	public static void main(String[] args) {
		double genauigkeit = 0.00000001;
		double zahl;
		Scanner scanner = new Scanner(System.in);
		Newton newton = new Newton();
		do {
			System.out.println("Basiszahl: ");
			zahl = scanner.nextDouble();
			try {
				System.out.println("Wurzel aus " + zahl +": " + newton.wurzel(zahl,genauigkeit));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		while (zahl != 0.0);
		scanner.close();
	}
}
