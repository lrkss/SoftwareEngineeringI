package de.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.*;

public class Logging {
    public Logging() {
        final LogManager logManager = LogManager.getLogManager();
        try {
            logManager.readConfiguration(new FileInputStream("./conf/log4j.properties"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);
        consoleHandler.setFormatter(new XMLFormatter());
        final Logger log = Logger.getLogger("log");
        log.setLevel(Level.FINEST);
        log.addHandler(consoleHandler);
    }
}
