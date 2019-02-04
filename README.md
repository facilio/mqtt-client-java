# mqtt-client-java
MQTT client for RabbitMQ and AWS Iot. The SDK is built with AWS library and Paho MQTT Java Client library.

## Overview
This document provides instructions for installing and configuring the SDK for Java. 

## Install the SDK

### Build the SDK from the Source
You can build both the SDK and its sample applications from the source hosted at GitHub. 

```sh
$ git clone https://github.com/facilio/mqtt-client-java.git
$ cd mqtt-client-java
$ mvn clean install
```

## Use the SDK
The following section provides some basic examples.

### Configuration

The SDK requires a set of properties to run the program, following are the properties required. It should be in user.home/facilio/facilio.config

- endpoint = mqtt server url
- clientId = name of the client making connection
- certPath = Path of the AWS certificate
- privateKeyPath = Path of the AWS private key
- certName = name of the certificate file to look in user.home/facilio directory
- privateKeyName = name of the private file to look in user.home/facilio directory
- user = user name incase of username / password authentication
- password = password incase of username / password authentication

### Initialize the Client

```java
FacilioMqttClient client = new AsyncMqttClient();
client.connect();
```

### Publish and Subscribe
After the client is initialized and connected, you can publish messages to a topic and subscribe to topics.


```java

String topic = "myTopic";
String payload = "hello world";
client.setCallback(new MqttCallback());
client.publish(topic, payload, 1);
client.subscribe(topic+"/subs");

```


```java
public class MqttCallback implements FacilioMqttCallback {

   public void onSuccess() {
        // called when the message is published successfully 
   }

   public void onFailure() {
        // called when the message is failed to publish
   }

   public void onTimeout(Throwable cause) {
        // called when connection is terminated or method timedout
   }

   public void onMessage(String topic, MqttMessage message) {
        // called when a message is received for the subscribed topic
   }
}
```
## API Documentation

The API documentation for the SDK can be found [here](https://s3-us-west-2.amazonaws.com/faciliomirror/mqtt-client-java/index.html).

## RabbitMQ Configuration

### RabbitMQ Installation:

Install [RabbitMQ](https://www.rabbitmq.com/), if it is not installed already. Enable rabbitmq_management and rabbitmq_mqtt plugins.

### RabbitMQ Management Console:

After successful installation & running of RabbitMQ, login to RabbitMQ Management console by http://<rabbitMQHostName>:<portNumber>

* Click on the Queues tab
	* Create a Queue with the intended Org Domain name.
	* Name: <Queue Name>
	* Durability: Durable
	* Auto Delete: No
	* After filling the above, click Add queue button

* Click on the Admin tab
	* Add a new user
	* Username: <Queue Name>
	* Password: <password> (random password)
	* Confirm Password: <password>
	* After filling the above, click Add user button
	* Click on the above created user 

* Give the permission to Virtual Host under permissions.
	For example:
	* Virtual Host: /
	* Configure regexp: .*
	* Write regexp: .*
	* Read regexp: .*
	* Click on the Set permission button

* Give topic permission, for example
	* Select the same virtual Host which is used above
	* Exchange: amq.topic
	* Write regexp: <Queue Name>.*
	* Read regexp:<Queue Name> .*
	* Click on the Set topic permission button

* Click on the Exchanges tab
	* Inside Exchanges tab, click on the amq.topic
	* In the resulting page, move on to the Add binding from this exchange section
	* Provide the following values for example
	* To queue: <Queue Name>
	* Routing key: <Queue Name>
	* Click the Bind button

