package com.facilio.mqtt.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * FacilioProperties is used to load properties from user.home/facilio/facilio.config<br>
 *
 * Below are the properties looked for in facilio.config file.<br>
 *<ul>
 * <li><strong>clientId</strong>: name of the client used to connect the server.<br>
 * <li><strong>endpoint</strong> : mqtt server url<br>
 * <li><strong>topic</strong> : name of the topic to publish the messages to.<br>
 * <li><strong>certPath</strong> : certificate path<br>
 * <li><strong>certName</strong> : name of the certificate file to look in user.home/facilio directory, either certPath or certName should be there for AwsMqttClient<br>
 * <li><strong>privateKeyPath</strong> : private key path<br>
 * <li><strong>privateKeyName</strong> : name of the private key file to look in user.home/facilio directory, either privateKeyPath or privateKeyName should be there for AwsMqttClient<br>
 * <li><strong>user</strong> : user name<br>
 * <li><strong>password</strong> : password of the mentioned user<br>
 *</ul>
 */
public class FacilioProperties {

    private static final String FACILIO_HOME = System.getProperty("user.home")+System.getProperty("file.separator")+"facilio"+System.getProperty("file.separator");
    private static final String CONFIG_FILE = FACILIO_HOME + "facilio.config";
    private static final Properties PROPERTIES = new Properties();

    private static final Logger LOGGER = LogManager.getLogger(FacilioProperties.class.getName());

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            LOGGER.info("Loading properties");
            File configFile = new File(CONFIG_FILE);
            if(configFile.exists()) {
                PROPERTIES.load(new FileReader(configFile));
                PROPERTIES.forEach((k,v) -> PROPERTIES.put(k, v.toString().trim()));
                LOGGER.info("Loaded properties successfully");
            } else {
                LOGGER.info("facilio.config file is not present in " + FACILIO_HOME);
            }
        } catch (SecurityException | IOException e){
            LOGGER.info("Exception while loading config " +  e.getMessage());
        }
    }

    /**
     * Checks if the given param is null or empty
     * @param value param
     * @return true if the value is null or empty
     */
    private static boolean checkIfNullOrEmpty(String value) {
        return ( (value == null) || (value.isEmpty()) );
    }

    /**
     * Returns the value of the given key if it exists
     * @param key property key
     * @return value of the key if exists, null otherwise.
     */
    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }


    /**
     * Returns the long value of the given key if exists, default value otherwise
     * @param key property key
     * @param defaultValue default value
     * @return value in long if exists, defaultValue otherwise
     */
    public static long getLong(String key, Long defaultValue){
        Long value = defaultValue;
        if(PROPERTIES.containsKey(key)) {
            String propertyValue = getProperty(key);
            try {
                value = Long.parseLong(propertyValue);
            } catch (NumberFormatException e) {
                LOGGER.info("exception while parsing value " + propertyValue + " for key " + key);
            }
        } else {
            value = defaultValue;
        }
        return value;
    }


    /**
     * Returns the directory path where facilio.config file should be present
     * @return the directory path where facilio.config file should be present
     */
    public static String getFacilioHome() {
        return FACILIO_HOME;
    }

    /**
     * Updates the given property key with the given value and updates facilio.config file.
     * @param key key to update
     * @param value value to set to the key
     */
    public static void updateProperty(String key, String value) {
        PROPERTIES.put(key, value);
        final ArrayList<String> lines = new ArrayList<>();
        lines.add(System.lineSeparator());
        lines.add(key+"="+value);
        File pointsFile = new File(CONFIG_FILE);
        try {
            Path file = pointsFile.toPath();
            if (pointsFile.exists()) {
                Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } else {
                Files.write(file, lines, Charset.forName("UTF-8"));
            }
        } catch (SecurityException | IOException e){
            LOGGER.info("Exception while writing to file ", e);
        }
    }

}
