#include "sensors.h"

#include <bluetooth/bluetooth.h>
#include <bluetooth/conn.h>
#include <bluetooth/gap.h>
#include <bluetooth/gatt.h>
#include <bluetooth/hci.h>


int initialize_bluetooth();
void register_callbacks();
int setup_advertisement_parameters();
