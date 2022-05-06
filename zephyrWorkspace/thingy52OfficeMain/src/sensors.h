#include "realTimeClock.h"

#include <device.h>
#include <drivers/sensor/ccs811.h>
#include <stdio.h>
#include <stdlib.h>

void init_sensors();

char* getCurrentValues();