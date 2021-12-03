package de.logging.quadrat;

import java.util.logging.*;

public class Newton {

    //    private final Logger log = Logger.getLogger((Newton.class.getName()));
    private final Logger log = Logger.getLogger("log");


    public Newton() {
//        Handler handler = new ConsoleHandler();
//        Formatter formatter = new XMLFormatter();
//        handler.setFormatter(formatter);
//        handler.setLevel(Level.FINE);
//        log.addHandler(handler);
//        log.setLevel(Level.FINE);
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