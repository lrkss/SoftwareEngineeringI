package loggingAufgabe2.kubik;

public class Heron {

	public double kubikwurzel(double zahl, double genauigkeit) {
		double xn = 1.0;
		while (Math.abs(zahl - xn*xn*xn) > genauigkeit) {
			xn = (2*xn + zahl/(xn*xn))/3.0;
		}
		return xn;
	}
}
