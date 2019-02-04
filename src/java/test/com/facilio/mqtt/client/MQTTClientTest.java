package com.facilio.mqtt.client;

import com.facilio.mqtt.client.impl.MQTTClient;
import com.facilio.mqtt.util.FacilioProperties;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTClientTest {

    static FacilioMqttClient client = new MQTTClient();
    public static void main(String[] args) throws Exception{

        client.setCallback(new MqttCallback());
        client.connect();
        Thread.sleep(10000);
        System.out.println(client.isConnected());
        client.subscribe(FacilioProperties.getProperty("topic")+"/msgs");
        client.publish(FacilioProperties.getProperty("topic"), "publishing", 1);
        System.out.println(client.isConnected());
    }

    private static class MqttCallback implements FacilioMqttCallback {
        public void onSuccess() {
            System.out.println("on success");
        }

        public void onFailure() {
            System.out.println("failure");
        }

        public void onTimeout(Throwable cause) {
            try {
                client.connect();
            } catch (FacilioMqttException e) {
                e.printStackTrace();
            }
            System.out.println("timeout");
        }

        public void onMessage(String topic, MqttMessage message) {
            System.out.println(message);
        }
    }
}
