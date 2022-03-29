/*
 *  WIFI & WEBSERVER SETUP
 */
// Instead of using the header, you can also set your wifi credentials here
#include "credentials.h"
//const char* ssid = "MY_SSID";
//const char* password = "MY_SUPER_SECRET_PASSWORD";
#include <WiFi.h> // Wifi API
#include "BLEDevice.h" // BLE API
// Webserver set to port 80
WiFiServer server(80);
// Handle to the connected client
WiFiClient wclient;
// Variable to store the HTTP request
String header;
// Current time
unsigned long currentTime = millis();
// Previous time
unsigned long previousTime = 0; 
// Define timeout time in milliseconds (example: 2000ms = 2s)
const long timeoutTime = 20000;

/*
 *  BLE SETUP
 */
// The remote service we wish to connect to, set also in the peripheral
static BLEUUID serviceUUID("10362e64-3e14-11ec-9bbc-0242ac130002");
// The characteristic of the remote service we are interested in.
static BLEUUID    charUUID("a3bfe44d-30c3-4a29-acf9-3414fc8972d0");
// Some status flags
static boolean doConnect = false;
static boolean connected = false;
static boolean doScan = false;
// Variables to keep track of characteristics, devices and connections
static BLERemoteCharacteristic* pRemoteCharacteristic;
static BLEAdvertisedDevice* myDevice;
static BLEClient* client;
static String scan_results;
static int sensor_readings;

// Callback struct which holds callbacks for connection and connection termination (I think there
// are even more possible, but I have not spent too much time on this yet.
class MyClientCallback : public BLEClientCallbacks {
  // connection callback
  void onConnect(BLEClient* pclient) {
  }
  // connection-termination callback
  void onDisconnect(BLEClient* pclient) {
    Serial.println("On disconnect");
    connected = false;
  }
};

// Function which is used to initiate a connection
bool connectToServer() {
  if(!connected){
    Serial.print("Forming a connection to ");
    Serial.println(myDevice->getAddress().toString().c_str());
    
    client  = BLEDevice::createClient();
    Serial.println(" - Created client");

    client->setClientCallbacks(new MyClientCallback());

    // Connect to the remote BLE Server.
    client->connect(myDevice);  // if you pass BLEAdvertisedDevice instead of address, it will be recognized type of peer device address (public or private)
    Serial.println(" - Connected to server");

    // Obtain a reference to the service we are after in the remote BLE server.
    BLERemoteService* pRemoteService = client->getService(serviceUUID);
    if (pRemoteService == nullptr) {
      Serial.print("Failed to find our service UUID: ");
      Serial.println(serviceUUID.toString().c_str());
      client->disconnect();
      return false;
    }
    Serial.println(" - Found our service");


    // Obtain a reference to the characteristic in the service of the remote BLE server.
    pRemoteCharacteristic = pRemoteService->getCharacteristic(charUUID);
    if (pRemoteCharacteristic == nullptr) {
      Serial.print("Failed to find our characteristic UUID: ");
      Serial.println(charUUID.toString().c_str());
      client->disconnect();
      return false;
    }
    Serial.println(" - Found our characteristic");

    // Read the value of the characteristic.
    if(pRemoteCharacteristic->canRead()) {
      sensor_readings = pRemoteCharacteristic->readUInt8();
      Serial.print("The characteristic value was: ");
      Serial.println(sensor_readings);
    }

    connected = true;
  }
}
/**
 * Scan for BLE servers and find the first one that advertises the service we are looking for.
 */
class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
 /**
   * Called for each advertising BLE server.
   */
  void onResult(BLEAdvertisedDevice advertisedDevice) {
    Serial.print("BLE Advertised Device found: ");
    Serial.println(advertisedDevice.toString().c_str());
    scan_results = scan_results + "<tr>\n<td>\n";
    scan_results = scan_results + advertisedDevice.getAddress().toString().c_str();
    scan_results = scan_results + "</td>\n";
    

    // We have found a device, let us now see if it contains the service we are looking for.
    if (advertisedDevice.haveServiceUUID()) {
      scan_results = scan_results + "<td>\n";
      scan_results = scan_results + advertisedDevice.getServiceUUID().toString().c_str();
      scan_results = scan_results + "</td>\n</tr>\n";
      if(advertisedDevice.isAdvertisingService(serviceUUID) ) {
        BLEDevice::getScan()->stop();
        if(!connected){
          myDevice = new BLEAdvertisedDevice(advertisedDevice);
          doConnect = true;
          doScan = true;
        }
      } // Found our server 
    } else {
      scan_results = scan_results + "<td>\n</td>\n</tr>\n";
    }
  } // onResult
}; // MyAdvertisedDeviceCallbacks


void setup() {
  Serial.begin(115200);
  // Connect to Wi-Fi network with SSID and password
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  // Print local IP address and start web server
  Serial.println("WiFi connected.");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  server.begin();
  
  Serial.println("Starting Arduino BLE Client application...");
  BLEDevice::init("");

  scan_results = "<br><table>\
<tr>\
<th>Adress</th>\
<th>Services</th>\
</tr>";

  // Retrieve a Scanner and set the callback we want to use to be informed when we
  // have detected a new device.  Specify that we want active scanning and start the
  // scan to run for 5 seconds.
  BLEScan* pBLEScan = BLEDevice::getScan();
  pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
  pBLEScan->setInterval(1349);
  pBLEScan->setWindow(449);
  pBLEScan->setActiveScan(true);
  sensor_readings = 0;
  pBLEScan->start(4,false);
  if (doConnect == true) {
    if (connectToServer()) {
      Serial.println("We are now connected to the BLE Server.");
    } else {
      Serial.println("We have failed to connect to the server; there is nothing more we will do.");
    }
    doConnect = false;
  }
} // End of setup.


// This is the Arduino main loop function.
void loop() {
  wclient = server.available();   // Listen for incoming clients

  if (doConnect == true) {
    if (connectToServer()) {
      Serial.println("We are connected to the BLE Server.");
    } else {
      Serial.println("We have failed to connect to the server; there is nothing more we will do.");
    }
  }

  if (wclient) {                             // If a new client connects,
    currentTime = millis();
    previousTime = currentTime;
    Serial.println("New Client.");          // print a message out in the serial port
    String currentLine = "";                // make a String to hold incoming data from the client
    while (wclient.connected() && currentTime - previousTime <= timeoutTime) {  // loop while the client's connected
      currentTime = millis();
      if (wclient.available()) {             // if there's bytes to read from the client,
        char c = wclient.read();             // read a byte, then
        Serial.write(c);                    // print it out the serial monitor
        header += c;
        if (c == '\n') {                    // if the byte is a newline character
          // if the current line is blank, you got two newline characters in a row.
          // that's the end of the client HTTP request, so send a response:
          if (currentLine.length() == 0) {
            // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
            // and a content-type so the client knows what's coming, then a blank line:
            wclient.println("HTTP/1.1 200 OK");
            wclient.println("Content-type:text/html");
            wclient.println("Connection: close");
            wclient.println();
            
            // handle requests
            if (header.indexOf("GET /ble/scan") >= 0) {
              // to refresh, overwrite old scan results
              scan_results = "<br><table>\
<tr>\
<th>Adress</th>\
<th>Services</th>\
</tr>";

              // Retrieve a Scanner and set the callback we want to use to be informed when we
              // have detected a new device.  Specify that we want active scanning and start the
              // scan to run for 5 seconds.
              BLEScan* pBLEScan = BLEDevice::getScan();
              pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
              pBLEScan->setInterval(1349);
              pBLEScan->setWindow(449);
              pBLEScan->setActiveScan(true);
              sensor_readings = 0;
              pBLEScan->start(2,false);
            } else if (header.indexOf("GET /ble/read_value" >= 0)) {
              if(connected) {
                // Read the value of the characteristic.
                if(pRemoteCharacteristic != nullptr && pRemoteCharacteristic->canRead()) {
                  sensor_readings = pRemoteCharacteristic->readUInt8();
                  Serial.print("The characteristic value was: ");
                  Serial.println(sensor_readings);
                }
              }
            }
            
            // Display the HTML web page
            wclient.println("<!DOCTYPE html><html>");
            wclient.println("<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            wclient.println("<link rel=\"icon\" href=\"data:,\">");
            // CSS to style the on/off buttons 
            // Feel free to change the background-color and font-size attributes to fit your preferences
            wclient.println("<style>html { font-family: Helvetica; display: inline-block; margin: 0px auto; text-align: center;}");
            wclient.println(".button { background-color: #4CAF50; border: none; color: white; padding: 16px 40px;");
            wclient.println("text-decoration: none; font-size: 30px; margin: 2px; cursor: pointer;}");
            wclient.println(".button2 {background-color: #555555;}</style></head>");
            
            // Web Page Heading
            wclient.println("<body><h1>ESP32 Web Server</h1>");

            // Button to trigger scanning
            wclient.println("<p><a href=\"/ble/scan\"><button class=\"button\">Scan 2 seconds</button></a></p>");
            // Table with scan results
            wclient.println(scan_results);
            wclient.println("</table>");

            // Button to initiate connection
            if(connected){
              wclient.println("<p><a href=\"/ble/read_value\"><button class=\"button\">Read</button></a></p>");  
            } else {
              wclient.println("<p><a href=\"\"><button class=\"button2\">Read</button></a></p>");
            }
            // Button to terminate connection
            // TBA
            // Sensor readings
            wclient.print("<p>Characterstic value: ");
            wclient.print(sensor_readings);
            wclient.println("</p>\n");
            
            
            /*
            // Display current state, and ON/OFF buttons for GPIO 26  
            wclient.println("<p>GPIO 26 - State " + output26State + "</p>");
            // If the output26State is off, it displays the ON button       
            if (output26State=="off") {
              wclient.println("<p><a href=\"/26/on\"><button class=\"button\">ON</button></a></p>");
            } else {
              wclient.println("<p><a href=\"/26/off\"><button class=\"button button2\">OFF</button></a></p>");
            } 
               
            // Display current state, and ON/OFF buttons for GPIO 27  
            wclient.println("<p>GPIO 27 - State " + output27State + "</p>");
            // If the output27State is off, it displays the ON button       
            if (output27State=="off") {
              wclient.println("<p><a href=\"/27/on\"><button class=\"button\">ON</button></a></p>");
            } else {
              wclient.println("<p><a href=\"/27/off\"><button class=\"button button2\">OFF</button></a></p>");
            }
            */
            wclient.println("</body></html>");
            
            // The HTTP response ends with another blank line
            wclient.println();
            // Break out of the while loop
            break;
          } else { // if you got a newline, then clear currentLine
            currentLine = "";
          }
        } else if (c != '\r') {  // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }
      }
    }
    // Clear the header variable
    header = "";
    // Close the connection
    wclient.stop();
    Serial.println("Client disconnected.");
  }
} // End of loop
