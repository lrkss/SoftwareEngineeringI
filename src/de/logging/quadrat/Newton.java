package de.logging.quadrat;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Newton {

    private final Logger log = Logger.getLogger((Newton.class.getName()));

    public Newton() {
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.FINEST);
        log.addHandler(handler);
        log.setLevel(Level.FINEST);
    }

    public double wurzel(double zahl, double genauigkeit) {
        log.info("Info");
        if (zahl < 0)
            throw new IllegalArgumentException("Wurzel aus Zahl < 0: " + zahl);
            log.severe("Exception");
        log.fine("Fine");
        double xn = 1.0;
        while (Math.abs(zahl - xn * xn) > genauigkeit) {
            log.finest("Finest");
            xn = (xn + zahl / xn) / 2.0;
        }
        return xn;
    }

//    private Logger getLog() {
//        if (log == null) {
//            log = LoggerFactory.getLogger(this.getClass());
//        }
//        return log;
//    }
}