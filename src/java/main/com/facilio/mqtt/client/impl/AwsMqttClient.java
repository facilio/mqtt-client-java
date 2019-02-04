package com.facilio.mqtt.client.impl;

import com.amazonaws.services.iot.client.*;
import com.facilio.mqtt.client.FacilioMqttCallback;
import com.facilio.mqtt.client.FacilioMqttClient;
import com.facilio.mqtt.client.FacilioMqttConnectOptions;
import com.facilio.mqtt.client.FacilioMqttException;
import com.facilio.mqtt.util.FacilioProperties;
import com.facilio.mqtt.util.SampleUtil;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;

/**
 * Enables an application to communicate with an MQTT server.
 *
 * This implementation uses {@link AWSIotMqttClient}. It provides applications a simple programming interface to all features of the MQTT version 3.1
 * specification including:
 * <ul>
 * <li>connect
 * <li>publish
 * <li>subscribe
 * <li>unsubscribe
 * <li>disconnect
 * </ul>
 *
 *  <code>
 *  FacilioMqttClient client = new AwsMqttClient();<br>
 *  client.setCallback(new FacilioMqttCallback());<br>
 *  client.connect();<br>
 *  client.subscribe("topic");<br>
 *  client.publish("topic", "test payload ", 1);<br>
 *
 *  </code>
 */

public class AwsMqttClient implements FacilioMqttClient {

    private AWSIotMqttClient client = null;
    private FacilioMqttConnectOptions connectOptions;
    private FacilioMqttConnectOptions defaultConnectOptions;
    private FacilioMqttCallback mqttCallback;
    private String clientId;

    private FacilioMqttConnectOptions getDefaultConnectOptions() {

        if (defaultConnectOptions == null) {
            FacilioMqttConnectOptions mqttConnectOptions = new FacilioMqttConnectOptions();
            this.clientId = FacilioProperties.getProperty("clientId");
            mqttConnectOptions.setServerURI(FacilioProperties.getProperty("endpoint"));
            String certPath = null;
            if (FacilioProperties.getProperty("certPath") != null) {
                certPath = FacilioProperties.getProperty("certPath");
            } else if (FacilioProperties.getProperty("certName") != null) {
                certPath = FacilioProperties.getFacilioHome() + FacilioProperties.getProperty("certName");
            }

            String privateKeyPath = null;
            if (FacilioProperties.getProperty("privateKeyPath") != null) {
                privateKeyPath = FacilioProperties.getProperty("privateKeyPath");
            } else if (FacilioProperties.getProperty("privateKeyName") != null) {
                privateKeyPath = FacilioProperties.getFacilioHome() + FacilioProperties.getProperty("privateKeyName");
            }

            SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certPath, privateKeyPath);
            if (pair != null) {
                mqttConnectOptions.setKeyStore(pair.keyStore);
                mqttConnectOptions.setKeyPair(pair.keyPassword);
            }
            defaultConnectOptions = mqttConnectOptions;
        }

        return defaultConnectOptions;
    }

    private void publish(AWSIotMessage message) throws FacilioMqttException {
        try {
            client.publish(message);
        } catch (AWSIotException e) {
            throw new FacilioMqttException(e.getCause());
        }
    }

    private void subscribe(AWSIotTopic topic, long timeout) throws FacilioMqttException {
        try {
            client.subscribe(topic, timeout);
        } catch (AWSIotException e) {
            throw new FacilioMqttException(e.getCause());
        }
    }

    /**
     * Sets the connect options {@link FacilioMqttConnectOptions} that will be used to connect the Mqtt Server if it has not been already connected.
     * @param connectOptions properties
     */
    public void setConnectOptions(FacilioMqttConnectOptions connectOptions) {
        this.connectOptions = connectOptions;
    }


    /**
     * Retruns FacilioMqttConnectOptions used to connect the Mqtt Server.
     * @return FacilioMqttConnectOptions
     */
    public FacilioMqttConnectOptions getConnectOptions() {
        return connectOptions;
    }

    /**
     * Sets the {@link FacilioMqttCallback} object that will be notified for events.
     * @param callback FacilioCallback object
     */
    public void setCallback(FacilioMqttCallback callback) {
        this.mqttCallback = callback;
    }

    /**
     * Returns the call back object {@link FacilioMqttCallback} used
     * @return FacilioMqttCallback
     */
    public FacilioMqttCallback getCallback() {
        return mqttCallback;
    }

    /**
     * Connects to an MQTT server with the given connect options {@link FacilioMqttConnectOptions}. <br>
     * @param connectOptions properties used to connect
     * @throws FacilioMqttException if any problem was encountered
     */
    public void connect(FacilioMqttConnectOptions connectOptions) throws FacilioMqttException {
        this.connectOptions = connectOptions;
        this.connect();
    }

    /**
     * Connects to an MQTT server using default connectOptions if its not been set.
     *
     * Default option would try to load properties from config file located in user.home/facilio/facilio.config
     * and sets the properties such as serverUri, user, password, keystore and keypair password String
     * refer {@link FacilioProperties} for keys used to set them.
     *<br>
     *     @throws FacilioMqttException if any problem was encountered
     */
    public void connect() throws FacilioMqttException {
        try {
            if( ! isConnected()) {
                if (connectOptions == null) {
                    connectOptions = getDefaultConnectOptions();
                }
                if (client == null) {
                    client = new AWSIotMqttClient(connectOptions.getServerURI(), clientId, connectOptions.getKeyStore(), connectOptions.getKeyPair());
                    if(connectOptions.getConnectionTimeout() > 0 ) {
                        client.setConnectionTimeout(connectOptions.getConnectionTimeout());
                    }
                    if(connectOptions.getKeepAliveInterval() > 0) {
                        client.setKeepAliveInterval(connectOptions.getKeepAliveInterval());
                    }
                }
                client.connect();
            }
        } catch (AWSIotException e) {
            throw new FacilioMqttException(e.getCause());
        }
    }

    /**
     * Returns the state of client and server connection.
     * @return true if Mqtt Server is connected, false otherwise
     */
    public boolean isConnected() {
        if(client != null) {
            return (client.getConnectionStatus() == AWSIotConnectionStatus.CONNECTED);
        } else {
            return false;
        }
    }

    /**
     *  Reconnects to the server if the connection has been closed.
     *  @throws FacilioMqttException if any problem was encountered
     */
    public void reconnect() throws FacilioMqttException {
        connect();
    }

    /**
     * Disconnects the server.<br>
     * @throws FacilioMqttException if any problem was encountered
     */
    public void disconnect() throws FacilioMqttException {
        try {
            client.disconnect();
        } catch (AWSIotException e) {
            throw new FacilioMqttException(e.getCause());
        }
    }

    /**
     *
     * @return client id used to make the connection
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the client id that will be used to connect the Mqtt Server.
     * @param clientId client id used to connect
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param payload actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    public void publish(String topic, byte[] payload, int qos) throws FacilioMqttException {
       publish(new AwsIotCallback(topic, AWSIotQos.valueOf(qos), payload));
    }

    /**
     * Publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param message actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    public void publish(String topic, JSONObject message, int qos) throws FacilioMqttException{
        publish(topic, message.toJSONString().getBytes(), qos);
    }

    /**
     * Publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param payload actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    public void publish(String topic, String payload, int qos) throws FacilioMqttException{
        publish(topic, payload.getBytes(), qos);
    }

    /**
     * Subscribe to the topic with QOS0.<br>
     * @param topic to subscribe
     * @throws FacilioMqttException if any problem was encountered
     */
    public void subscribe(String topic) throws FacilioMqttException{
        subscribe(topic, 0);
    }

    /**
     * Subscribe to the topic with the given QOS.<br>
     * @param topic to subscribe
     * @param qos for the topic
     * @throws FacilioMqttException if any problem was encountered
     */
    public void subscribe(String topic, int qos) throws FacilioMqttException {
        subscribe(new AwsIotTopic(topic, AWSIotQos.valueOf(qos)), 10000);
    }


    /**
     * Unsubscribe the topic.<br>
     * @param topic to unsubscribe
     * @throws FacilioMqttException if any problem was encountered
     */
    public void unsubscribe(String topic) throws FacilioMqttException {
        unsubscribe(topic, 10000);
    }

    private void unsubscribe(String topic, long timeout) throws FacilioMqttException {
        try {
            client.unsubscribe(topic, timeout);
        } catch (AWSIotException | AWSIotTimeoutException e) {
            throw new FacilioMqttException(e.getCause());
        }
    }

    private class AwsIotCallback extends AWSIotMessage {

        AwsIotCallback(String topic, AWSIotQos qos, byte[] payload) {
            super(topic, qos, payload);
        }

        public void onSuccess() {
            if(mqttCallback != null) {
                mqttCallback.onSuccess();
            }
        }

        public void onFailure() {
            if(mqttCallback != null) {
                mqttCallback.onFailure();
            }
        }

        public void onTimeout() {
            if(mqttCallback != null) {
                mqttCallback.onTimeout(new Throwable("unknown"));
            }
        }
    }

    private class AwsIotTopic extends AWSIotTopic {

        AwsIotTopic(String topic, AWSIotQos qos) {
            super(topic, qos);
        }

        public void onMessage(AWSIotMessage message) {
            MqttMessage mqttMessage = new MqttMessage(message.getPayload());
            mqttMessage.setQos(message.getQos().getValue());
            if(mqttCallback != null) {
                mqttCallback.onMessage(message.getTopic(), mqttMessage);
            }
        }
    }
}
