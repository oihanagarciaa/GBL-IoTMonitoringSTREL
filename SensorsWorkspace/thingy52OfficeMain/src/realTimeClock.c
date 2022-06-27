#include "realTimeClock.h"

/* RTC device */
#define MY_RTC DT_PATH(soc, rtc_4000b000)
/* global variables for the counter */
static int32_t channel = 0;
static const int64_t increment = 32000;

void timer_handler(int32_t id, uint64_t expire_time, void *user_data) {
  int64_t delay = expire_time % increment;
  z_nrf_rtc_timer_set(channel, expire_time + increment, &timer_handler, NULL);
  printk("timer elapsed with delay of %lld ticks\n", delay );
}


void initialize_realTimeClock() {
  printk("Starting main.\n");
  int err;

  /* allocate channel for timer */
  channel =  z_nrf_rtc_timer_chan_alloc();
  printk("Allocated channel %d for a timer\n", channel);

  /* disable interrupt */
  bool locked = z_nrf_rtc_timer_compare_int_lock(channel);
  printk("Lock state: %i\n", locked);
  /* set timer on channel 1, the target time is 1 sec, i.e., 32000 ticks,
  the handler for the interrupt is passed and no user-data is passed to the handler */
  err =  z_nrf_rtc_timer_set(channel, increment, &timer_handler, NULL);
  if(err) {
    printk("Problem when setting timer (err %i)\n", err);
  } else {
    uint32_t timer_val = z_nrf_rtc_timer_compare_read(channel);
    printk("Successfully set timer to %d ticks\n", timer_val);
  }
  /* enable interrupt after setting the timer */
  z_nrf_rtc_timer_compare_int_unlock(channel, locked);
  printk("Lock state: %i\n", locked);
}

unsigned long long getRealTimeValue(){
  uint64_t time;
  time = z_nrf_rtc_timer_read();
  return time / (increment / 100);
}
