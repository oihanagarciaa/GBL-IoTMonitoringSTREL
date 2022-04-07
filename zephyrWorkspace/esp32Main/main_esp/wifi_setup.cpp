#include "wifi_setup.h"
#include <WiFi.h>
#include <PubSubClient.h>
#include "esp_secrets.h"

// WiFi
const char *ssid = SECRET_SSID; // Enter your WiFi name
const char *password = SECRET_PASS;  // Enter WiFi password

// MQTT Broker
const char *mqtt_broker = "stefanschupp.de";
const char *topic = "institute/thingy/";
String stopic = "institute/thingy/";
const char *mqtt_username = "oihana";
const char *mqtt_password = "22oihana22";
const int mqtt_port = 1883;

WiFiClient espClient;
PubSubClient client(espClient);

void callback(char *topic, byte *payload, unsigned int length) {
 Serial.print("Message arrived in topic: ");
 Serial.println(topic);
 Serial.print("Message:");
 for (int i = 0; i < length; i++) {
     Serial.print((char) payload[i]);
 }
 Serial.println();
 Serial.println("-----------------------");
}

void init_wifi(){
 // connecting to a WiFi network
 WiFi.begin(ssid, password);
 while (WiFi.status() != WL_CONNECTED) {
     delay(500);
     Serial.println("Connecting to WiFi..");
 }
 Serial.println("Connected to the WiFi network");
 //connecting to a mqtt broker
 client.setServer(mqtt_broker, mqtt_port);
 client.setCallback(callback);
 while (!client.connected()) {
     String client_id = "esp32-client-";
     client_id += String(WiFi.macAddress());
     Serial.printf("The client %s connects to the public mqtt broker\n", client_id.c_str());
     if (client.connect(client_id.c_str(), mqtt_username, mqtt_password)) {
         Serial.println("Public emqx mqtt broker connected");
     } else {
         Serial.print("failed with state ");
         Serial.print(client.state());
         delay(2000);
     }
 }
 client.loop();
}

void publishESPMessage(char* id, char* message){
  String newTopic = stopic + id;
  // Length (with one extra character for the null terminator)
  int str_len = newTopic.length() + 1; 
  
  // Prepare the character array (the buffer) 
  char char_array[str_len];
  
  // Copy it over 
  newTopic.toCharArray(char_array, str_len);
    //char *newTopic;
    //strcpy(newTopic, topic);
    //strcat(newTopic, id);
    //char *newTopic = new char[topic.length() + id.length() + 1];
    //strcpy(newTopic, );
  client.publish(char_array, message);
}
