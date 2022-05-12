#include "main_setup.h"


/*// The remote service we wish to connect to, set also in the peripheral
static BLEUUID serviceUUID("10362e64-3e14-11ec-9bbc-0242ac130002");
// The characteristic of the remote service we are interested in.
static BLEUUID    charUUID("a3bfe44d-30c3-4a29-acf9-3414fc8972d0");

static const int size = 5;
// Variables to keep track of characteristics, devices and connections
static BLERemoteCharacteristic* pRemoteCharacteristic[size];
static BLEAdvertisedDevice* myDevice[size];
static BLEClient* client[size];
static String devices[] = {"000", "001", "002", "003", "004"};


class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
  void onResult(BLEAdvertisedDevice advertisedDevice) {
    Serial.print("BLE Advertised Device found: ");
    Serial.println(advertisedDevice.toString().c_str());

    // We have found a device, let us now see if it contains the service we are looking for.
    if (advertisedDevice.haveServiceUUID() && advertisedDevice.isAdvertisingService(serviceUUID)) {
      delete myDevice;
      BLEDevice::getScan()->stop();
      myDevice = new BLEAdvertisedDevice(advertisedDevice);
      doConnect = true;
      doScan = true;

    } // Found our server
  } // onResult
}; // MyAdvertisedDeviceCallbacks
static MyClientCallback* myClientCallback = new MyClientCallback();

void init_BLEScanner(){
  Serial.println("Starting Arduino BLE Client application...");
  BLEDevice::init("");

  // Retrieve a Scanner and set the callback we want to use to be informed when we
  // have detected a new device.  Specify that we want active scanning and start the
  // scan to run for 5 seconds.
  BLEScan* pBLEScan = BLEDevice::getScan();
  MyAdvertisedDeviceCallbacks* myCallbacks = new MyAdvertisedDeviceCallbacks();
  pBLEScan->setAdvertisedDeviceCallbacks(myCallbacks);
  // a new call consumes a lot of memory
  //pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
  pBLEScan->setInterval(1349);
  pBLEScan->setWindow(449);
  pBLEScan->setActiveScan(true);
  pBLEScan->start(5000, false);
}

// Callback struct which holds callbacks for connection and connection termination
class MyClientCallback : public BLEClientCallbacks {
  // connection callback
  void onConnect(BLEClient* pclient) {
  }
  // connection-termination callback
  void onDisconnect(BLEClient* pclient) {
    Serial.println("onDisconnect");
  }
};

bool connectToServer(int i) {
  String espID;
  Serial.print("Forming a connection to ");
  if (myDevice[i]->haveName())
  {
    Serial.print("Device name: ");
    espID = myDevice[i]->getName().c_str();
    Serial.println(espID);
    Serial.println("");
  }
  
  if(devices[i]==espID){
    client[i]  = BLEDevice::createClient();
    Serial.println(" - Created client");
  
    client[i]->setClientCallbacks(myClientCallback);
    // Connect to the remote BLE Server.
  
    delay(1000);
  
    client[i]->connect(myDevice[i]);  // if you pass BLEAdvertisedDevice instead of address, it will be recognized type of peer device address (public or private)
    Serial.println(" - Connected to server");
    // Obtain a reference to the service we are after in the remote BLE server.
    delete pRemoteService;
    pRemoteService = client->getService(serviceUUID);
    if (pRemoteService == nullptr) {
      Serial.print("Failed to find our service UUID: ");
      Serial.println(serviceUUID.toString().c_str());
      client->disconnect();
      return false;
    }
    Serial.println(" - Found our service");
    delete pRemoteCharacteristic;
    // Obtain a reference to the characteristic in the service of the remote BLE server.
    pRemoteCharacteristic = pRemoteService->getCharacteristic(charUUID);
    if (pRemoteCharacteristic == nullptr) {
      Serial.print("Failed to find our characteristic UUID: ");
      Serial.println(charUUID.toString().c_str());
      client->disconnect();
      return false;
    }
    Serial.println(" - Found our characteristic");
    return true;
  }

  return false;
}

bool readDeviceValues(int i){
    String value; 
    if(pRemoteCharacteristic[i]->canRead()) {
      //int value = pRemoteCharacteristic->readUInt8();
      value = pRemoteCharacteristic[i]->readValue().c_str();
      Serial.print("The characteristic value was: ");
      Serial.println(value);
  
      delete cespID;
      cespID = new char[espID.length() + 1];
      strcpy(cespID, espID.c_str());
  
      delete jsonMessage;
      jsonMessage = convertToJson(espID, value);
      publishESPMessage(cespID, jsonMessage);
      return true;
    }
    return false;
}

char *c;
String g;
char * convertToJson(String espID, String value){
  g.remove(0);
  g = "{ 'id': " + espID + "," +
    "'time': " + getValue(value,';',0) + "," +
    "'temp': " + getValue(value,';',1) + "," +
    "'hum': "  + getValue(value,';',2) + "," +
    "'co2': "  + getValue(value,';',3) + "," +
    "'tvoc': " + getValue(value,';',4) + "}";

  Serial.print("JSON -> ");
  Serial.println(g);
  c = new char[g.length() + 1];
  strcpy(c, g.c_str());

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
}*/



/*
static MyClientCallback* myClientCallback = new MyClientCallback();
static BLERemoteService* pRemoteService;
char* cespID;
String value;
char* jsonMessage;

// Function which is used to initiate a connection
bool connectToServer(int i) {
    String espID;
    espID.remove(0);
    value.remove(0);
  
    Serial.print("Forming a connection to ");
    if (myDevice->haveName())
    {
      Serial.print("Device name: ");
      espID = myDevice->getName().c_str();
      Serial.println(espID);
      Serial.println("");
    }
    client[i]  = BLEDevice::createClient();
    Serial.println(" - Created client");
  
    client[i]->setClientCallbacks(myClientCallback);
    // Connect to the remote BLE Server.
  
    delay(5000);
  
    client[i]->connect(myDevice);  // if you pass BLEAdvertisedDevice instead of address, it will be recognized type of peer device address (public or private)
    Serial.println(" - Connected to server");
    // Obtain a reference to the service we are after in the remote BLE server.
    delete pRemoteService;
    pRemoteService = client->getService(serviceUUID);
    if (pRemoteService == nullptr) {
      Serial.print("Failed to find our service UUID: ");
      Serial.println(serviceUUID.toString().c_str());
      client->disconnect();
      return false;
    }
    Serial.println(" - Found our service");
    delete pRemoteCharacteristic;
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
      value = pRemoteCharacteristic->readValue().c_str();
      Serial.print("The characteristic value was: ");
      Serial.println(value);
  
      delete cespID;
      cespID = new char[espID.length() + 1];
      strcpy(cespID, espID.c_str());
  
      delete jsonMessage;
      jsonMessage = convertToJson(espID, value);
      publishESPMessage(cespID, jsonMessage);
    }
  
    connected = true;
}

class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {

  void onResult(BLEAdvertisedDevice advertisedDevice) {
    Serial.print("BLE Advertised Device found: ");
    Serial.println(advertisedDevice.toString().c_str());

    // We have found a device, let us now see if it contains the service we are looking for.
    if (advertisedDevice.haveServiceUUID() && advertisedDevice.isAdvertisingService(serviceUUID)) {
      delete myDevice;
      BLEDevice::getScan()->stop();
      myDevice = new BLEAdvertisedDevice(advertisedDevice);
      doConnect = true;
      doScan = true;

    } // Found our server
  } // onResult
}; // MyAdvertisedDeviceCallbacks


char *c;
String g;
char * convertToJson(String espID, String value){
  g.remove(0);
  g = "{ 'id': " + espID + "," +
    "'time': " + getValue(value,';',0) + "," +
    "'temp': " + getValue(value,';',1) + "," +
    "'hum': "  + getValue(value,';',2) + "," +
    "'co2': "  + getValue(value,';',3) + "," +
    "'tvoc': " + getValue(value,';',4) + "}";

  Serial.print("JSON -> ");
  Serial.println(g);
  c = new char[g.length() + 1];
  strcpy(c, g.c_str());

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
    delay(500);
    doConnect = false;
    connected = false;
    doScan = true;
    // disconnect
    client[i]->disconnect();
    
  }else if(doScan){
    BLEDevice::getScan()->start(5000, false);  
  }
  delay(500);
} // End of loop*/
