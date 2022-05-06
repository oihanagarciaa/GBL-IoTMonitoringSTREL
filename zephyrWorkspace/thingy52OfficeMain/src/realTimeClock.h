#include <device.h>
#include <devicetree.h>
#include <drivers/counter.h>
#include <drivers/timer/nrf_rtc_timer.h>

void initialize_realTimeClock();

unsigned long long getRealTimeValue();