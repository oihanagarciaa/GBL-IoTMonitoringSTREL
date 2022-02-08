#include "leds.h"

int initialize_leds() {
  int err;
  // get binding for the led device based on the device tree label
  red_led_device = device_get_binding(LED0);
  green_led_device = device_get_binding(LED1);
  blue_led_device = device_get_binding(LED2);

  err = gpio_pin_configure(red_led_device, PIN0, GPIO_OUTPUT_INACTIVE | FLAGS0);
  if (err < 0) {
    return err;
  }
  err =
      gpio_pin_configure(green_led_device, PIN1, GPIO_OUTPUT_INACTIVE | FLAGS1);
  if (err < 0) {
    return err;
  }
  err =
      gpio_pin_configure(blue_led_device, PIN2, GPIO_OUTPUT_INACTIVE | FLAGS2);
  if (err < 0) {
    return err;
  }
  return err;
}

void setLeds(int red, int green, int blue) {
  gpio_pin_set(red_led_device, PIN0, red);
  gpio_pin_set(green_led_device, PIN1, green);
  gpio_pin_set(blue_led_device, PIN2, blue);
}