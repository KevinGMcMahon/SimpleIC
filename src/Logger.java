package src;

import java.io.IOException;
import java.util.logging.*;

public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static Handler fileHandler;

    static {
        try {
            // Configure FileHandler with a single log file
            fileHandler = new FileHandler("Logger.log", true); // Appends to the same file on subsequent runs
            fileHandler.setFormatter(new SimpleFormatter()); // Optional: Use SimpleFormatter or a custom one
            logger.addHandler(fileHandler);

            // Optional: Add ConsoleHandler for console output
            logger.addHandler(new ConsoleHandler());

            // Set the logger level
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    public static void logWarning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void logSevere(String message) {
        logger.log(Level.SEVERE, message);
    }

    public static void logConfig(String message) {
        logger.log(Level.CONFIG, message);
    }

    public static void closeLogger() {
        // Close the file handler to release resources
        if (fileHandler != null) {
            fileHandler.close();
        }
    }
}
