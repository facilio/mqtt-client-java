package com.facilio.mqtt.client.impl;

import com.facilio.mqtt.client.FacilioMqttCallback;
import com.facilio.mqtt.client.FacilioMqttClient;
import com.facilio.mqtt.client.FacilioMqttConnectOptions;
import com.facilio.mqtt.client.FacilioMqttException;
import com.facilio.mqtt.util.FacilioProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;


/**
 * Enables an application to communicate with an MQTT server.
 *
 * This implementation uses {@link MqttAsyncClient}. It provides applications a simple programming interface to all features of the MQTT version 3.1
 * specification including:
 * <ul>
 * <li>connect
 * <li>publish
 * <li>subscribe
 * <li>unsubscribe
 * <li>disconnect
 * </ul>
 *
 * <br>
 * <code>
 *  FacilioMqttClient client = new AsyncMqttClient();<br>
 *  client.setCallback(new FacilioMqttCallback());<br>
 *  client.connect();<br>
 *  client.subscribe("topic");<br>
 *  client.publish("topic", "test payload", 1);<br>
 * </code>
 */
public class AsyncMqttClient implements FacilioMqttClient {

    private MqttAsyncClient mqttClient;
    private FacilioMqttConnectOptions connectOptions;
    private FacilioMqttConnectOptions defaultConnectOptions;
    private FacilioMqttCallback mqttCallback;
    private String clientId;

    /**
     * Sets the connect options {@link FacilioMqttConnectOptions} that will be used to connect the Mqtt Server if it has not been already connected.
     * @param connectOptions properties
     */
    public void setConnectOptions(FacilioMqttConnectOptions connectOptions) {
        this.connectOptions = connectOptions;
    }


    /**
     * Returns {@link FacilioMqttConnectOptions} used to connect the Mqtt Server.
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
     * @param connectOptions properties used to connect.
     * @throws FacilioMqttException if any problem was encountered
     */
    public void connect(FacilioMqttConnectOptions connectOptions) throws FacilioMqttException {
        try {
            if(mqttClient != null) {
                mqttClient.setCallback(new MqttCallbackListener(getCallback()));
                mqttClient.connect(connectOptions);
                this.connectOptions = connectOptions;
            }
        } catch (MqttException e) {
            throw new FacilioMqttException(e.getReasonCode(), e.getCause());
        }
    }

    private FacilioMqttConnectOptions getDefaultConnectOptions() {
        if (defaultConnectOptions == null) {
            FacilioMqttConnectOptions mqttConnectOptions = new FacilioMqttConnectOptions();
            this.clientId = FacilioProperties.getProperty("clientId");
            mqttConnectOptions.setServerURI(FacilioProperties.getProperty("endpoint"));
            String user = null;
            if (FacilioProperties.getProperty("user") != null) {
                user = FacilioProperties.getProperty("user");
            }

            String password = null;
            if (FacilioProperties.getProperty("password") != null) {
                password = FacilioProperties.getProperty("password");
            }
            mqttConnectOptions.setUserName(user);
            if (password != null) {
                mqttConnectOptions.setPassword(password.toCharArray());
            }
            defaultConnectOptions = mqttConnectOptions;
        }

        return defaultConnectOptions;
    }

    /**
     * Connects to an MQTT server using default connectOptions if its not been set.
     *
     * Default option would try to load properties from config file located in user.home/facilio/facilio.config
     * and sets the properties such as serverUri, user, password, keystore and keypair password string
     * refer {@link FacilioProperties} for keys used to set them.
     *<br>
     * @throws FacilioMqttException if any problem was encountered
     */
    public void connect() throws FacilioMqttException {
        try {
            if( ! isConnected()) {
                if (connectOptions == null) {
                    connectOptions = getDefaultConnectOptions();
                }
                if (mqttClient == null) {
                    mqttClient = new MqttAsyncClient(connectOptions.getServerURI(), clientId);
                }
                mqttClient.setCallback(new MqttCallbackListener(getCallback()));
                mqttClient.connect(connectOptions);//, null, new MqttActionListener());
                int i = 0;
                while ( ! isConnected() && i < 10) {
                    try {
                        Thread.sleep(1000);
                        i++;
                    } catch (InterruptedException e) {
                        throw new FacilioMqttException(e.getCause());
                    }
                }
            }
        } catch (MqttException e) {
            throw new FacilioMqttException(e.getReasonCode(), e.getCause());
        }
    }

    /**
     * Returns the state of client and server connection.
     * @return true if Mqtt Server is connected, false otherwise
     */
    public boolean isConnected() {
        if(mqttClient != null) {
            return mqttClient.isConnected();
        }
        return false;
    }

    /**
     *  Reconnects to the server if the connection has been closed.
     *  @throws FacilioMqttException if any problem was encountered
     */
    public void reconnect() throws FacilioMqttException {
        try {
            if( ! isConnected()) {
                mqttClient.connect();
            }
        } catch (MqttException e) {
            throw new FacilioMqttException(e.getReasonCode(), e.getCause());
        }
    }

    /**
     * Disconnects the server.<br>
     * @throws FacilioMqttException if any problem was encountered
     */
    public void disconnect() throws FacilioMqttException {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            throw new FacilioMqttException(e.getReasonCode(), e.getCause());
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

    private void publish(String topic, MqttMessage message) throws FacilioMqttException {
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            throw new FacilioMqttException(e.getReasonCode(), e.getCause());
        }
    }

    /**
     * Publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param payload actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    public void publish(String topic, byte[] payload, int qos) throws FacilioMqttException {
        publish(topic, getMqttMessage(payload, qos));
    }

    /**
     * Publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param message actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    public void publish(String topic, JSONObject message, int qos) throws FacilioMqttException {
        publish(topic, message.toJSONString().getBytes(), qos);
    }

    /**
     * Publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param payload actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    public void publish(String topic, String payload, int qos) throws FacilioMqttException {
        publish(topic, payload.getBytes(), qos);
    }

    /**
     * Subscribe to the topic with QOS1.<br>
     * @param topic to subscribe
     * @throws FacilioMqttException if any problem was encountered
     */
    public void subscribe(String topic) throws FacilioMqttException {
        subscribe(topic, 1);
    }

    /**
     * Subscribe to the topic with the given QOS.<br>
     * @param topic to subscribe
     * @param qos for the topic
     * @throws FacilioMqttException if any problem was encountered
     */
    public void subscribe(String topic, int qos) throws FacilioMqttException {
        try {
            mqttClient.subscribe(topic, qos);
        } catch (MqttException e) {
            throw new FacilioMqttException(e.getReasonCode(), e.getCause());
        }
    }

    /**
     * Unsubscribe the topic.<br>
     * @param topic to unsubscribe
     * @throws FacilioMqttException if any problem was encountered
     */
    public void unsubscribe(String topic) throws FacilioMqttException {
        try {
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            throw  new FacilioMqttException(e.getReasonCode(), e.getCause());
        }
    }

    private MqttMessage getMqttMessage(byte[] payload, int qos) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload);
        mqttMessage.setQos(qos);
        return mqttMessage;
    }
}
