#include <device.h>
#include <devicetree.h>
#include <drivers/gpio.h>

#define LED_NODE DT_ALIAS(led0)
#define LED_GREEN_NODE DT_NODELABEL(led1)
#define LED_BLUE_NODE DT_NODELABEL(led2)

#if DT_NODE_HAS_STATUS(LED_NODE, okay)
#define LED0 DT_GPIO_LABEL(LED_NODE, gpios)
#define PIN0 DT_GPIO_PIN(LED_NODE, gpios)
#define FLAGS0 DT_GPIO_FLAGS(LED_NODE, gpios)
#else
#error "The alias in the devicetree is not defined"
#define LED0 ""
#define PIN0 0
#define FLAGS0 0
#endif

#if DT_NODE_HAS_STATUS(LED_GREEN_NODE, okay)
#define LED1 DT_GPIO_LABEL(LED_GREEN_NODE, gpios)
#define PIN1 DT_GPIO_PIN(LED_GREEN_NODE, gpios)
#define FLAGS1 DT_GPIO_FLAGS_BY_IDX(LED_GREEN_NODE, gpios, 0)
#else
#error "The node in the devicetree is not defined"
#define LED1 ""
#define PIN1 0
#define FLAGS1 0
#endif

#if DT_NODE_HAS_STATUS(LED_BLUE_NODE, okay)
#define LED2 DT_GPIO_LABEL(LED_BLUE_NODE, gpios)
#define PIN2 DT_GPIO_PIN(LED_BLUE_NODE, gpios)
#define FLAGS2 DT_GPIO_FLAGS_BY_IDX(LED_BLUE_NODE, gpios, 0)
#else
#error "The node in the devicetree is not defined"
#define LED2 ""
#define PIN2 0
#define FLAGS2 0
#endif

/* LEDS */
static const struct device *red_led_device;
static const struct device *green_led_device;
static const struct device *blue_led_device;

int initialize_leds();
void setLeds(int red, int green, int blue);
