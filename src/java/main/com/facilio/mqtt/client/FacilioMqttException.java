package com.facilio.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttException;

public class FacilioMqttException extends MqttException {

    /**
     * Constructs a new <code>MqttException</code> with the specified code
     * as the underlying reason.
     *
     * @param reasonCode the reason code for the exception.
     */
    public FacilioMqttException(int reasonCode) {
        super(reasonCode);
    }

    /**
     * Constructs a new <code>MqttException</code> with the specified
     * <code>Throwable</code> as the underlying reason.
     *
     * @param cause the underlying cause of the exception.
     */
    public FacilioMqttException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new <code>MqttException</code> with the specified
     * <code>Throwable</code> as the underlying reason.
     *
     * @param reason the reason code for the exception.
     * @param cause  the underlying cause of the exception.
     */
    public FacilioMqttException(int reason, Throwable cause) {
        super(reason, cause);
    }
}
