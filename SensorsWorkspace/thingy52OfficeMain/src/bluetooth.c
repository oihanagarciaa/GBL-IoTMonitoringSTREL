#include "bluetooth.h"

/* Declarations of read and write callbacks */
static ssize_t read_callback(struct bt_conn *conn,
                             const struct bt_gatt_attr *attr, void *buf,
                             uint16_t len, uint16_t offset) {
  // increase our meaningful data, use helper method to write it
  const char *message = getCurrentValues();

  return bt_gatt_attr_read(conn, attr, buf, len, offset, message, strlen(message));
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
  bt_set_name("see prj.conf");
  bt_addr_le_to_str(bt_conn_get_dst(conn), addr, sizeof(addr));

  printk("Connected: %s\n", addr);

  if (!connection_ref) {
    connection_ref = bt_conn_ref(conn);
  }
}

static void disconnecting(struct bt_conn *conn, uint8_t reason) {
  char addr[BT_ADDR_LE_STR_LEN];

  bt_addr_le_to_str(bt_conn_get_dst(conn), addr, sizeof(addr));

  printk("Disconnected %s (reason 0x%02x)\n", addr, reason);

  bt_conn_unref(connection_ref);
  connection_ref = NULL;
}

/* struct collecting connection callbacks (see above) */
struct bt_conn_cb beacon_connection_callbacks = {
    .connected = connection_established, .disconnected = disconnecting};


int initialize_bluetooth(){
  int err;
  err = bt_enable(NULL);
  return err;
}

void register_callbacks(){
    /* Register callbacks for connection establishment */
    bt_conn_cb_register(&beacon_connection_callbacks);
}
  
int setup_advertisement_parameters(){
  printk("Set up advertising.");
  int err;
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
  
  return err;
}
