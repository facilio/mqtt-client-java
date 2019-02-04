package com.facilio.mqtt.client;

import com.facilio.mqtt.client.impl.AwsMqttClient;
import com.facilio.mqtt.util.FacilioProperties;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class AwsMqttClientTest {

    public static void main(String[] args) throws Exception{
        AwsMqttClient client = new AwsMqttClient();
        FacilioMqttConnectOptions connectOptions = new FacilioMqttConnectOptions();
        client.setCallback(new MqttCallback());
        client.connect();
        System.out.println(client.isConnected());
        client.publish(FacilioProperties.getProperty("topic"), "test", 1);
        Thread.sleep(5000);
        client.subscribe(FacilioProperties.getProperty("topic")+"/msgs");
        Thread.sleep(5000);
        client.disconnect();
        System.out.println(client.isConnected());
    }

    private static class MqttCallback implements FacilioMqttCallback {
        public void onSuccess() {
            System.out.println("success");
        }

        public void onFailure() {
            System.out.println("failure");
        }

        public void onTimeout(Throwable cause) {
            System.out.println("timeout");
        }

        public void onMessage(String topic, MqttMessage message) {
            System.out.println(message);
        }
    }
}
