#include "wifi_setup.h"

#include "json11/json11.hpp"
#include "BLEDevice.h"

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

// Callback struct which holds callbacks for connection and connection termination
class MyClientCallback : public BLEClientCallbacks {
  // connection callback
  void onConnect(BLEClient* pclient) {
  }
  // connection-termination callback
  void onDisconnect(BLEClient* pclient) {
    connected = false;
    Serial.println("onDisconnect");
  }
};

// Function which is used to initiate a connection
bool connectToServer() {
    String espID = "";
    Serial.print("Forming a connection to ");
    if (myDevice->haveName())
      {
        Serial.print("Device name: ");
        espID = myDevice->getName().c_str();
        Serial.println(espID);
        Serial.println("");
      }
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
      //int value = pRemoteCharacteristic->readUInt8();
      String value = pRemoteCharacteristic->readValue().c_str();
      Serial.print("The characteristic value was: ");
      Serial.println(value);
      
      char *cespID = new char[espID.length() + 1];
      strcpy(cespID, espID.c_str());
      /*char *cvalue = new char[value.length() + 1];
      strcpy(cvalue, value.c_str());*/
      
      char* jsonMessage = convertToJson(espID, value);
      publishESPMessage(cespID, jsonMessage);
    }

    connected = true;
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

    // We have found a device, let us now see if it contains the service we are looking for.
    if (advertisedDevice.haveServiceUUID() && advertisedDevice.isAdvertisingService(serviceUUID)) {

      BLEDevice::getScan()->stop();
      myDevice = new BLEAdvertisedDevice(advertisedDevice);
      doConnect = true;
      doScan = true;

    } // Found our server
  } // onResult
}; // MyAdvertisedDeviceCallbacks



char * convertToJson(String espID, String value){
    
  String g = "{ 'id': " + espID + "," +
    "'time': " + getValue(value,';',0) + "," +
    "'temp': " + getValue(value,';',1) + "," +
    "'hum': " + getValue(value,';',2) + "," +
    "'co2': " + getValue(value,';',3) + "," +
    "'tvoc': " + getValue(value,';',4) + "}";

  Serial.print("JSON -> ");
  Serial.println(g);
  char c[g.length()]; 
  g.toCharArray(c, g.length());

  return c;
}
String getValue(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}









void setup() {
  Serial.begin(115200);
  init_wifi();
  
  Serial.println("Starting Arduino BLE Client application...");
  BLEDevice::init("");

  // Retrieve a Scanner and set the callback we want to use to be informed when we
  // have detected a new device.  Specify that we want active scanning and start the
  // scan to run for 5 seconds.
  BLEScan* pBLEScan = BLEDevice::getScan();
  pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
  pBLEScan->setInterval(1349);
  pBLEScan->setWindow(449);
  pBLEScan->setActiveScan(true);
  pBLEScan->start(5, false);
} // End of setup.


// This is the Arduino main loop function.
void loop() {

  // If the flag "doConnect" is true then we have scanned for and found the desired
  // BLE Server with which we wish to connect.  Now we connect to it.  Once we are 
  // connected we set the connected flag to be true.
  if (doConnect == true) {
    if (connectToServer()) {
      Serial.println("We are now connected to the BLE Server.");
    } else {
      Serial.println("We have failed to connect to the server; there is nothing more we will do.");
    }
    doConnect = false;
  }

  // If we are connected to a peer BLE Server, update the characteristic each time we are reached
  // with the current time since boot.
  if (connected) {
    // if everything went right, wait a bit and then disconnect
    delay(1000);
    doConnect = false;
    connected = false;
    doScan = true;
    // disconnect
    client->disconnect();
    
  }else if(doScan){
    BLEDevice::getScan()->start(10, false);  
  }
  
  delay(500);
} // End of loop
