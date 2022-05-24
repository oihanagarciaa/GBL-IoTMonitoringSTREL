#include "main_setup.h"
#include "BLEDevice.h"

static const int size = 2; 

// The remote service we wish to connect to, set also in the peripheral
static BLEUUID serviceUUID("10362e64-3e14-11ec-9bbc-0242ac130002");
// The characteristic of the remote service we are interested in.
static BLEUUID    charUUID("a3bfe44d-30c3-4a29-acf9-3414fc8972d0");

// Variables to keep track of characteristics, devices and connections
static BLERemoteCharacteristic* pRemoteCharacteristic[size];
static BLEAdvertisedDevice* myDevice;
static BLEClient* client[size];
static String devices[] = {"000", "001"};

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
static MyClientCallback* myClientCallback = new MyClientCallback();
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
      delete myDevice;
      BLEDevice::getScan()->stop();
      myDevice = new BLEAdvertisedDevice(advertisedDevice);
//      doConnect = true;
//      doScan = true;

    } // Found our server
  } // onResult
}; // MyAdvertisedDeviceCallbacks


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


String espID[size];
bool connectToServer(int i) {
  
  Serial.print("Forming a connection to ");
  if (myDevice->haveName())
  {
    Serial.print("Device name: ");
    espID[i] = myDevice->getName().c_str();
  }
  
  if(devices[i]==espID[i]){
    client[i]  = BLEDevice::createClient();
    Serial.println(" - Created client");
  
    client[i]->setClientCallbacks(myClientCallback);
    // Connect to the remote BLE Server.
  
    delay(1000);
  
    client[i]->connect(myDevice);  // if you pass BLEAdvertisedDevice instead of address, it will be recognized type of peer device address (public or private)
    Serial.println(" - Connected to server");
    // Obtain a reference to the service we are after in the remote BLE server.
//    delete pRemoteService;
    BLERemoteService* pRemoteService[size];
    pRemoteService[i] = client[i]->getService(serviceUUID);
    if (pRemoteService[i] == nullptr) {
      Serial.print("Failed to find our service UUID: ");
      Serial.println(serviceUUID.toString().c_str());
      client[i]->disconnect();
      return false;
    }
    Serial.println(" - Found our service");
//    delete pRemoteCharacteristic;
    // Obtain a reference to the characteristic in the service of the remote BLE server.
    pRemoteCharacteristic[i] = pRemoteService[i]->getCharacteristic(charUUID);
    if (pRemoteCharacteristic[i] == nullptr) {
      Serial.print("Failed to find our characteristic UUID: ");
      Serial.println(charUUID.toString().c_str());
      client[i]->disconnect();
      return false;
    }
    Serial.println(" - Found our characteristic");
    return true;
  }else{
    //client[i]->disconnect();
    return false;
  }
}
char* cespID;
char* jsonMessage;
String value; 
bool readDeviceValues(int i){
    value.remove(0);
    if(pRemoteCharacteristic[i]->canRead()) {
      //int value = pRemoteCharacteristic->readUInt8();
      value = pRemoteCharacteristic[i]->readValue().c_str();
      Serial.print("The characteristic value was: ");
      Serial.println(value);
  
      cespID = new char[espID[i].length() + 1];
      strcpy(cespID, espID[i].c_str());
  
      delete jsonMessage;
      jsonMessage = convertToJson(espID[i], value);
      publishESPMessage(cespID, jsonMessage);
      Serial.print("-> ");
      Serial.print(cespID);
      Serial.print(", ");
      Serial.println(jsonMessage);
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
}

void setup() {
  Serial.begin(115200);
  int randomNumber = random(500);
  init_wifi(randomNumber);
  init_BLEScanner();

  for(int i = 0; i < size; i++){
    bool doContinue = false;
    while(!doContinue){
      doContinue = connectToServer(i);
      delay(2000);
      Serial.print("CONTINUE: ");
      Serial.println(doContinue);
      BLEDevice::getScan()->start(2, false); 
    }
  }
  
}

void loop() {
  for(int i = 0; i < size; i ++){
    delay(2000);
    readDeviceValues(i);
  }
}
