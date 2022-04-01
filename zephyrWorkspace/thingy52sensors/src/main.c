#include <zephyr.h>
#include <device.h>
#include <drivers/sensor.h>
#include <stdio.h>
#include <sys/util.h>
#include <drivers/sensor/ccs811.h>
#include <data/json.h>

#include <bluetooth/bluetooth.h>
#include <bluetooth/conn.h>
#include <bluetooth/gap.h>
#include <bluetooth/gatt.h>
#include <bluetooth/hci.h>

#include <device.h>
#include <devicetree.h>
#include <drivers/gpio.h>

struct values {
    double temp;
    double hum;
    int co2;
    int tvoc;
};
static const struct json_obj_descr values_descr[] = {
    JSON_OBJ_DESCR_PRIM(struct values, temp, JSON_TOK_NUMBER),
    JSON_OBJ_DESCR_PRIM(struct values, hum, JSON_TOK_NUMBER),
    JSON_OBJ_DESCR_PRIM(struct values, co2, JSON_TOK_NUMBER),
    JSON_OBJ_DESCR_PRIM(struct values, tvoc, JSON_TOK_NUMBER),
};

struct values jsonValues;
char bytes [125];

const struct device *tempHumdev;
const struct device *airDev;
/////////////////////////////

#define DEVICE_NAME CONFIG_BT_DEVICE_NAME
#define DEVICE_NAME_LEN (sizeof(DEVICE_NAME) - 1)

struct bt_conn *connection_ref;

/* Set up advertising data */
static const struct bt_data advertisement[] = {
    BT_DATA_BYTES(BT_DATA_FLAGS, (BT_LE_AD_GENERAL | BT_LE_AD_NO_BREDR)),
    BT_DATA_BYTES(BT_DATA_UUID128_ALL,
                  BT_UUID_128_ENCODE(0x10362e64, 0x3e14, 0x11ec, 0x9bbc,
                                     0x0242ac130002))};



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


/////////////////////////////
static void readTempHumValues(const struct device *dev)
{
	struct sensor_value temp, hum;
	if (sensor_sample_fetch(dev) < 0) {
		printk("Sensor sample update error\n");
		return;
	}

	if (sensor_channel_get(dev, SENSOR_CHAN_AMBIENT_TEMP, &temp) < 0) {
		printk("Cannot read HTS221 temperature channel\n");
		return;
	}

	if (sensor_channel_get(dev, SENSOR_CHAN_HUMIDITY, &hum) < 0) {
		printk("Cannot read HTS221 humidity channel\n");
		return;
	}

	/* display temperature */
	jsonValues.temp = sensor_value_to_double(&temp);
	printk("Temperature:%.1f C\n", sensor_value_to_double(&temp));


	/* display humidity */
	jsonValues.hum = sensor_value_to_double(&hum);
	printk("Relative Humidity:%.1f%%\n", sensor_value_to_double(&hum));

	////////////////////////////////////////////

	//ccs811_envdata_update(dev, &temp, &humidity);

}

static void readAirValues(const struct device *dev)
{
	struct sensor_value co2, tvoc;
	int rc = 0;
	int baseline = -1;

	rc = ccs811_baseline_fetch(dev);
	if (rc >= 0) {
		baseline = rc;
		rc = 0;
	}

	if (rc == 0) {
		rc = sensor_sample_fetch(dev);
	}
	if (rc == 0) {
		const struct ccs811_result_type *rp = ccs811_result(dev);

		jsonValues.co2 = sensor_channel_get(dev, SENSOR_CHAN_CO2, &co2);
		jsonValues.tvoc = sensor_channel_get(dev, SENSOR_CHAN_VOC, &tvoc);

		printk("CCS811: %u ppm eCO2\n        %u ppb eTVOC\n", co2.val1, tvoc.val1);
        }
}

static void hts221_handler(const struct device *dev, const struct sensor_trigger *trig)
{
	readTempHumValues(dev);
}

static void setUpTempHumDev(const struct device *tempHumdev)
{
	if (tempHumdev == NULL) {
		printk("Could not get HTS221 device\n");
		return;
	}

	if (IS_ENABLED(CONFIG_HTS221_TRIGGER)) {
		struct sensor_trigger trig = {
			.type = SENSOR_TRIG_DATA_READY,
			.chan = SENSOR_CHAN_ALL,
		};
		if (sensor_trigger_set(tempHumdev, &trig, hts221_handler) < 0) {
			printk("Cannot configure trigger\n");
			return;
		}
	}
}
static bool app_fw_2;

static void setUpAirDev(const struct device *dev)
{
	struct ccs811_configver_type cfgver;
        int rc;

        if (!dev) {
		printk("Failed to get device binding");
		return;
	}

	printk("device is %p, name is %s\n", dev, dev->name);

	rc = ccs811_configver_fetch(dev, &cfgver);
	if (rc == 0) {
		printk("HW %02x; FW Boot %04x App %04x ; mode %02x\n",
		       cfgver.hw_version, cfgver.fw_boot_version,
		       cfgver.fw_app_version, cfgver.mode);
		app_fw_2 = (cfgver.fw_app_version >> 8) > 0x11;
	}
}

//////////////////////////////////
/* Declarations of read and write callbacks */
static ssize_t read_callback(struct bt_conn *conn,
                             const struct bt_gatt_attr *attr, void *buf,
                             uint16_t len, uint16_t offset) {
  // increase our meaningful data, use helper method to write it
  readTempHumValues(tempHumdev);
  readAirValues(airDev);
  return bt_gatt_attr_read(conn, attr, buf, len, offset, &jsonValues, sizeof(jsonValues));
}
/* Write callback, currently, do nothing, also not used */
ssize_t write_callback(struct bt_conn *conn, const struct bt_gatt_attr *attr,
                       const void *buf, uint16_t len, uint16_t offset,
                       uint8_t flags) {
  return 0;
}

static struct bt_uuid_128 my_service_uuid = BT_UUID_INIT_128(
    BT_UUID_128_ENCODE(0x10362e64, 0x3e14, 0x11ec, 0x9bbc, 0x0242ac130002));

static struct bt_uuid_128 my_characteristics_uuid = BT_UUID_INIT_128(
    BT_UUID_128_ENCODE(0xa3bfe44d, 0x30c3, 0x4a29, 0xacf9, 0x3414fc8972d0));

BT_GATT_SERVICE_DEFINE(
    read_sensor_service, BT_GATT_PRIMARY_SERVICE(&my_service_uuid),
    BT_GATT_CHARACTERISTIC(
        &my_characteristics_uuid.uuid, // uuid
        BT_GATT_CHRC_READ,             // service-characteristics is readable
        BT_GATT_PERM_READ,             // read and write permissions
        &read_callback, &write_callback, (void *)1));

void main(void)
{
	tempHumdev = device_get_binding("HTS221");
	setUpTempHumDev(tempHumdev);

	airDev = device_get_binding(DT_LABEL(DT_INST(0, ams_ccs811)));
	setUpAirDev(airDev);
/////////////////////////////////
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

	  struct bt_le_adv_param adv_param = {
                .id = 'DE0364',
                .sid = 1,
                .secondary_max_skip = 0,
                .options = (BT_LE_ADV_OPT_CONNECTABLE | BT_LE_ADV_OPT_USE_NAME |
                            BT_LE_ADV_OPT_FORCE_NAME_IN_AD),
                .interval_min = BT_GAP_ADV_SLOW_INT_MIN,
                .interval_max = BT_GAP_ADV_SLOW_INT_MAX,
                .peer = NULL,
            };

            err = bt_le_adv_start(&adv_param, advertisement, ARRAY_SIZE(advertisement),
                                    NULL, 0);

        if (err) {
            printk("Advertising failed to start (err %d)\n", err);
            return err;
          } else {
            printk("Started advertising.\n");
          }
////////////////////////////////

}
