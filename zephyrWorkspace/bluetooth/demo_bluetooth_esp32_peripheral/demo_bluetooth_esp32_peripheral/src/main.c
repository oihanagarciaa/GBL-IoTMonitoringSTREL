#include "leds.h"
#include "sys/printk.h"
#include <bluetooth/bluetooth.h>
#include <bluetooth/conn.h>
#include <bluetooth/gap.h>
#include <bluetooth/gatt.h>
#include <bluetooth/hci.h>
#include <device.h>
#include <devicetree.h>
#include <drivers/gpio.h>
#include <zephyr.h>
#define DEVICE_NAME CONFIG_BT_DEVICE_NAME
#define DEVICE_NAME_LEN (sizeof(DEVICE_NAME) - 1)

/* Global variables */
// acts as meaningful data that is stored in an attribute, gets incremented on
// every read
int count = 0;

/* Declarations of read and write callbacks */
static ssize_t read_callback(struct bt_conn *conn,
                             const struct bt_gatt_attr *attr, void *buf,
                             uint16_t len, uint16_t offset) {
  // increase our meaningful data, use helper method to write it
  count++;
  return bt_gatt_attr_read(conn, attr, buf, len, offset, &count, sizeof(count));
}
/* Write callback, currently, do nothing, also not used */
ssize_t write_callback(struct bt_conn *conn, const struct bt_gatt_attr *attr,
                       const void *buf, uint16_t len, uint16_t offset,
                       uint8_t flags) {
  return 0;
}

/* Some custom service uuids */
static struct bt_uuid_128 my_service_uuid = BT_UUID_INIT_128(
    BT_UUID_128_ENCODE(0x10362e64, 0x3e14, 0x11ec, 0x9bbc, 0x0242ac130002));
/* Custom characteristic*/
static struct bt_uuid_128 my_characteristics_uuid = BT_UUID_INIT_128(
    BT_UUID_128_ENCODE(0xa3bfe44d, 0x30c3, 0x4a29, 0xacf9, 0x3414fc8972d0));
/* Statically define my custom service with our custom characteristic, which is
 * readable */
BT_GATT_SERVICE_DEFINE(
    read_sensor_service, BT_GATT_PRIMARY_SERVICE(&my_service_uuid),
    BT_GATT_CHARACTERISTIC(
        &my_characteristics_uuid.uuid, // uuid
        BT_GATT_CHRC_READ,             // service-characteristics is readable
        BT_GATT_PERM_READ,             // read and write permissions
        &read_callback, &write_callback, (void *)1));

/* Connection reference counting object, is accessed when connecting and
 * disconnecting */
struct bt_conn *connection_ref;

/* Set up advertising data, we announce the custom service */
static const struct bt_data advertisement[] = {
    BT_DATA_BYTES(BT_DATA_FLAGS, (BT_LE_AD_GENERAL | BT_LE_AD_NO_BREDR)),
    BT_DATA_BYTES(BT_DATA_UUID128_ALL,
                  BT_UUID_128_ENCODE(0x10362e64, 0x3e14, 0x11ec, 0x9bbc,
                                     0x0242ac130002))};

/**
 * @brief Function which is called when a connection is established.
 * @details Increments the refcount on the connection-object, enables the green
 * led and prints the address of the peer.
 *
 * @param conn
 * @param err
 */
static void connection_established(struct bt_conn *conn, uint8_t err) {
  char addr[BT_ADDR_LE_STR_LEN];

  if (err) {
    printk("Connection failed (err 0x%02x)\n", err);
    if (connection_ref) {
      bt_conn_unref(connection_ref);
      connection_ref = NULL;
    }
    return;
  }
  setLeds(0, 1, 0);
  bt_addr_le_to_str(bt_conn_get_dst(conn), addr, sizeof(addr));

  printk("Connected: %s\n", addr);

  if (!connection_ref) {
    connection_ref = bt_conn_ref(conn);
  }
}

/**
 * @brief Is called when a connection is terminated.
 * @details Decreases the refcount on the connection-object, switches to the
 * blue led, prints some message.
 *
 * @param conn
 * @param reason
 */
static void disconnecting(struct bt_conn *conn, uint8_t reason) {
  char addr[BT_ADDR_LE_STR_LEN];

  bt_addr_le_to_str(bt_conn_get_dst(conn), addr, sizeof(addr));

  printk("Disconnected %s (reason 0x%02x)\n", addr, reason);

  bt_conn_unref(connection_ref);
  connection_ref = NULL;
  setLeds(0, 0, 1);
}

/* struct collecting connection callbacks (see above) */
struct bt_conn_cb beacon_connection_callbacks = {
    .connected = connection_established, .disconnected = disconnecting};

/* The main function */
int main() {
  // led-stuff, initially enable red led until advertising is started
  initialize_leds();
  setLeds(1, 0, 0);

  int err;

  /* Initialize the Bluetooth Subsystem, no callback passed here */
  err = bt_enable(NULL);
  if (err) {
    printk("Bluetooth init failed (err %d)\n", err);
    return err;
  }

  /* Register callbacks for connection establishment */
  bt_conn_cb_register(&beacon_connection_callbacks);

  printk("Set up advertising.");

  /* Set up advertisement parameters the complicated way - you can also use the
   * provided macros in bluetooth.h, e.g., BT_LE_ADV_CONN_NAME_AD*/
  struct bt_le_adv_param adv_param = {
      .id = BT_ID_DEFAULT,
      .sid = 0,
      .secondary_max_skip = 0,
      .options = (BT_LE_ADV_OPT_CONNECTABLE | BT_LE_ADV_OPT_USE_NAME |
                  BT_LE_ADV_OPT_FORCE_NAME_IN_AD),
      .interval_min = 0x0200, /* 200 ms, you can also use predefined values such
                                 as BT_GAP_ADV_FAST_INT_MIN_2 in gap.h */
      .interval_max = 0x0200, /* 200 ms */
      .peer = NULL,           /* undirected */
  };

  /* Use own parameters, own advertisement, no scan-response callback */
  err = bt_le_adv_start(&adv_param, advertisement, ARRAY_SIZE(advertisement),
                        NULL, 0);
  /* Blue led indicates that the advertising is running */
  setLeds(0, 0, 1);

  if (err) {
    printk("Advertising failed to start (err %d)\n", err);
    return err;
  } else {
    printk("Started advertising.\n");
  }

  return 0;
}