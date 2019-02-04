package com.facilio.mqtt.client;

import org.json.simple.JSONObject;


/**
 * Enables an application to communicate with an MQTT server.
 *
 * It provides applications a simple programming interface to all features of the MQTT version 3.1
 * specification including:
 * <ul>
 * <li>connect
 * <li>publish
 * <li>subscribe
 * <li>unsubscribe
 * <li>disconnect
 * </ul>
 *
 * <code>
 *  FacilioMqttClient client = new FacilioMqttClient();<br>
 *  client.setCallback(new FacilioMqttCallback());<br>
 *  client.connect();<br>
 *  client.subscribe("topic");<br>
 *  client.publish("topic", "test payload ", 1);<br>
 * </code>
 */
public interface FacilioMqttClient {

    /**
     * Sets the connect options that will be used to connect the Mqtt Server if it has not been already connected.
     * @param connectOptions properties
     */
    void setConnectOptions(FacilioMqttConnectOptions connectOptions);

    /**
     * Retruns FacilioMqttConnectOptions used to connect the Mqtt Server.
     * @return FacilioMqttConnectOptions
     */
    FacilioMqttConnectOptions getConnectOptions();


    /**
     * Sets the FacilioMqttCallback object that will be notified for events.
     * @param callback FacilioCallback object
     */
    void setCallback (FacilioMqttCallback callback);

    /**
     * Returns the call back object used
     * @return FacilioMqttCallback
     */
    FacilioMqttCallback getCallback();

    /**
     * Connects to an MQTT server using the default options.
     * <p>The default options are specified in {@link FacilioMqttConnectOptions} class.
     * </p>
     *
     * @param connectOptions properties used to connect
     * @throws FacilioMqttException if any problem was encountered
     */
    void connect(FacilioMqttConnectOptions connectOptions) throws FacilioMqttException;

    /**
     * Connects to an MQTT server using default connectOptions if its not been set.
     *
     * Default option would try to load config file in user.home/facilio/facilio.config
     * and sets the properties such as serverUri, user, password, keystore and keypair password String
     *
     * @throws FacilioMqttException if any problem was encountered
     */
    void connect() throws FacilioMqttException;

    /**
     * Returns the state of client and server connection.
     * @return true if Mqtt Server is connected, false otherwise
     */
    boolean isConnected();

    /**
     *  Reconnects to the server if the connection has been closed.
     *  @throws FacilioMqttException if any problem was encountered
     */
    void reconnect() throws FacilioMqttException;

    /**
     * Disconnects the server.
     * @throws FacilioMqttException if any problem was encountereds
     */
    void disconnect() throws FacilioMqttException;

    /**
     *
     * @return client id used to make the connection
     */
    String getClientId();

    /**
     * Sets the client id that will be used to connect the Mqtt Server.
     * @param clientId client id used to connect
     */
    void setClientId(String clientId);


    /**
     * publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param payload actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    void publish(String topic, byte[] payload, int qos) throws FacilioMqttException;

    /**
     * publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param message actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    void publish(String topic, JSONObject message, int qos) throws FacilioMqttException;

    /**
     * publish payload to the mentioned topic with the given QOS
     * @param topic name to publish the message
     * @param payload actual message
     * @param qos for this message
     * @throws FacilioMqttException if any problem was encountered
     */
    void publish(String topic, String payload, int qos) throws FacilioMqttException;

    /**
     * Subscribe to the topic.
     * @param topic to subscribe
     * @throws FacilioMqttException if any problem was encountered
     */
    void subscribe(String topic) throws FacilioMqttException;

    /**
     * Subscribed to the topic with the given QOS
     * @param topic to subscribe
     * @param qos for the topic
     * @throws FacilioMqttException if any problem was encountered
     */
    void subscribe(String topic, int qos) throws FacilioMqttException;

    /**
     * Unsubscribe the topic
     * @param topic to unsubscribe
     * @throws FacilioMqttException if any problem was encountered
     */
    void unsubscribe(String topic) throws FacilioMqttException;
}
