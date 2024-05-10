package com.thoxia.limbosecure.logging;

// Velocity uses log4j and Bungee uses java.logging :(
public interface Logger {

    void info(String message);

    void warn(String message);

    void severe(String message);

}
