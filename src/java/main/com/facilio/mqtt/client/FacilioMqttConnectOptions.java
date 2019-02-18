package com.facilio.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;



public class FacilioMqttConnectOptions extends MqttConnectOptions {


    /**
     * Constructs a new <code>FacilioMqttConnectOptions</code> object using the
     * default values.
     * <p>The default options are specified in {@link MqttConnectOptions} class.
     * </p>
     *
     * More information about these values can be found in the setter methods.
     */

    public FacilioMqttConnectOptions() {
        super();
    }

    private String serverUri;
    private KeyStore keyStore;
    private String keyPair;

    /**
     * Returns the endpoint for the Mqtt server url
     * @return the endpoint of the Mqtt server url
     */
    public String getServerURI() {
        return serverUri;
    }

    /**
     * Sets the Mqtt server url
     * @param serverUri server endpoint
     */
    public void setServerURI(String serverUri) {
        this.serverUri = serverUri;
        if(serverUri != null && serverUri.endsWith("amazonaws.com")) {
            try {
                URI uri = new URI(serverUri);
                if(uri.getScheme() == null) {
                    serverUri = "ssl://" + serverUri;
                }
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }

        }
        setServerURIs(new String[]{serverUri});
    }

    /**
     * Returns the keystore value
     * @return the keystore object
     */
    public KeyStore getKeyStore() {
        return keyStore;
    }

    /**
     * Sets the KeyStore object
     * @param keyStore keystore object to connect
     */
    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    /**
     * Returns String value of the Key password
     * @return String value of the Key password
     */
    public String getKeyPair() {
        return keyPair;
    }

    /**
     * Sets the key password value in String
     * @param keyPair key password
     */
    public void setKeyPair(String keyPair) {
        this.keyPair = keyPair;
    }

}
