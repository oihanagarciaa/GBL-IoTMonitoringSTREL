/*
 * Created by Stefan Schupp <stefan.schupp@tuwien.ac.at> on 16.09.21.
 */

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

/* Some custom service uuids */
static struct bt_uuid_128 my_service_uuid = BT_UUID_INIT_128(
    BT_UUID_128_ENCODE(0x10362e64, 0x3e14, 0x11ec, 0x9bbc, 0x0242ac130002));
static struct bt_uuid_128 my_characteristics_uuid = BT_UUID_INIT_128(
    BT_UUID_128_ENCODE(0xa3bfe44d, 0x30c3, 0x4a29, 0xacf9, 0x3414fc8972d0));
/* Statically define my custom service */
BT_GATT_SERVICE_DEFINE(
    read_sensor_service, BT_GATT_PRIMARY_SERVICE(&my_service_uuid),
    BT_GATT_CHARACTERISTIC(
        &my_characteristics_uuid.uuid, // uuid
        BT_GATT_CHRC_READ,             // service-characteristics is readable
        BT_GATT_PERM_READ,             // only read-permissions for value
        NULL, NULL, (void *)1));

/* Set up advertising data */
static const struct bt_data advertisement[] = {
    BT_DATA_BYTES(BT_DATA_FLAGS, (BT_LE_AD_GENERAL | BT_LE_AD_NO_BREDR)),
    BT_DATA_BYTES(BT_DATA_UUID128_ALL,
                  BT_UUID_128_ENCODE(0x10362e64, 0x3e14, 0x11ec, 0x9bbc,
                                     0x0242ac130002))};

void scan_callback(const bt_addr_le_t *addr, int8_t rssi, uint8_t type,
                   struct net_buf_simple *ad) {
  // skip non-connectable advertisements
  if (type != BT_GAP_ADV_TYPE_ADV_IND && type != BT_GAP_ADV_TYPE_ADV_IND) {
    return;
  }
  printk("Scan response callback called.\n");
}

/* Set Scan Response data */
static const struct bt_data scan_response[] = {
    BT_DATA(BT_DATA_NAME_COMPLETE, DEVICE_NAME, DEVICE_NAME_LEN),
};

static void connection_established(struct bt_conn *conn, uint8_t err) {
  char addr[BT_ADDR_LE_STR_LEN];

  if (err) {
    printk("Connection failed (err 0x%02x)\n", err);
    return;
  }

  bt_addr_le_to_str(bt_conn_get_dst(conn), addr, sizeof(addr));

  printk("Connected: %s\n", addr);
}

static void disconnecting(struct bt_conn *conn, uint8_t reason) {
  char addr[BT_ADDR_LE_STR_LEN];

  bt_addr_le_to_str(bt_conn_get_dst(conn), addr, sizeof(addr));

  printk("Disconnected %s (reason 0x%02x)\n", addr, reason);
}

/* struct collecting connection callbacks */
struct bt_conn_cb beacon_connection_callbacks = {
    .connected = connection_established, .disconnected = disconnecting};

int main() {

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

  /* Start advertising with GAP-name, connectable */
  // err = bt_le_adv_start(BT_LE_ADV_CONN_NAME_AD,
  // advertisement,ARRAY_SIZE(advertisement),
  // scan_response,ARRAY_SIZE(scan_response));

  /* Use own parameters, own advertisement, no scan-response callback */
  err = bt_le_adv_start(&adv_param, advertisement, ARRAY_SIZE(advertisement),
                        NULL, 0);

  /* Use default parameters, own advertisement, no scan-response callback */
  // err = bt_le_adv_start(BT_LE_ADV_CONN_NAME_AD,
  // advertisement,ARRAY_SIZE(advertisement), NULL,0);

  /* Use default parameters, non-connectable beacon, own advertisement, no
   * scan-response callback */
  // err = bt_le_adv_start(BT_LE_ADV_NCONN,
  // advertisement,ARRAY_SIZE(advertisement), NULL,0);

  if (err) {
    printk("Advertising failed to start (err %d)\n", err);
    return err;
  } else {
    printk("Started advertising.\n");
  }

  return 0;
}