package com.facilio.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *  FacilioMqttCallback will be used to register for actions in the client.
 */
public interface FacilioMqttCallback {

    /**
     * Will be called when the publish action is success
     */
    void onSuccess();

    /**
     *  Will be called when the publish action is failed
     */
    void onFailure();

    /**
     * Will be called on timeout
     * @param cause if the cause is known it will be passed.
     */
    void onTimeout(Throwable cause);

    /**
     * Will be called when the client receives a message.
     * @param topic topic name
     * @param message Mqtt message object received from the server.
     */
    void onMessage(String topic, MqttMessage message);

}
