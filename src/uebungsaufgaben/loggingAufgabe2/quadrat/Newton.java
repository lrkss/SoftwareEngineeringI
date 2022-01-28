package uebungsaufgaben.loggingAufgabe2.quadrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Newton {

    private static final Logger log = LoggerFactory.getLogger(Newton.class);

    public double wurzel(double zahl, double genauigkeit) {
        if (zahl < 0) {
            log.error("Wurzel aus Zahl < 0: {}", zahl);
            throw new IllegalArgumentException();
        }
        double xn = 1.0;
        while (Math.abs(zahl - xn * xn) > genauigkeit) {
            xn = (xn + zahl / xn) / 2.0;
        }
        return xn;
    }
}