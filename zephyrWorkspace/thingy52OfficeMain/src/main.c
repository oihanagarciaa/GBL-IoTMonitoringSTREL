/** Runtime Verification of spatio/temporal propperties over IoT networks
 */
#include "bluetooth.h"
#include "sensors.h"
#include "realTimeClock.h"

#define DEVICE_NAME CONFIG_BT_DEVICE_NAME
#define DEVICE_NAME_LEN (sizeof(DEVICE_NAME) - 1)

/* The main function */
int main() {
  init_sensors();
  initialize_realTimeClock();

  int err;
  
  err = initialize_bluetooth();
  if(err){
      printk("Bluetooth init failed (err %d)\n", err);
      return err;
  }

  register_callbacks();

  err = setup_advertisement_parameters();
  if (err) {
    printk("Advertising failed to start (err %d)\n", err);
    return err;
  } else {
    printk("Started advertising.\n");
  }

  /*while(true){
      getCurrentValues();
      k_sleep(K_MSEC(5000));
  }*/

  return 0;
}