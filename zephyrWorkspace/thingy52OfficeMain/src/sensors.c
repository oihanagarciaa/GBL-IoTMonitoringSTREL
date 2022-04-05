#include "sensors.h"


const struct device *tempHumdev;
const struct device *airDev;

const struct Values{
	double temp;
	double hum;
	int co2;
	int tvoc;
};
struct Values myValues;

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
	double dtemp = sensor_value_to_double(&temp);
	myValues.temp = dtemp;
	printk("Temperature:%.1f C\n", dtemp);


	/* display humidity */
	double dhum = sensor_value_to_double(&hum);
	myValues.hum = dhum;
	printk("Relative Humidity:%.1f%%\n", dhum);
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

		int carbonodioxido = sensor_channel_get(dev, SENSOR_CHAN_CO2, &co2);
		int tvoc1234 = sensor_channel_get(dev, SENSOR_CHAN_VOC, &tvoc);

		myValues.co2 = co2.val1;
		myValues.tvoc = tvoc.val1;
		printk("CCS811: %d ppm eCO2\n        %d ppb eTVOC\n", co2.val1, tvoc.val1);
    }
}

static void hts221_handler(const struct device *dev, const struct sensor_trigger *trig)
{
	readTempHumValues(dev);
    readAirValues(dev);
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


static void setUpAirDev(const struct device *dev)
{
    static bool app_fw_2;
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

void init_sensors(){
    tempHumdev = device_get_binding("HTS221");
    setUpTempHumDev(tempHumdev);

    airDev = device_get_binding(DT_LABEL(DT_INST(0, ams_ccs811)));
    setUpAirDev(airDev);
}

char* getCurrentValues(){
  readTempHumValues(tempHumdev);
  readAirValues(airDev);
  static char values[300];

  unsigned long long time = 
    getRealTimeValue();

  sprintf(values, "%llu;%f;%f;%d;%d", 
    time, myValues.temp, myValues.hum, myValues.co2, myValues.tvoc);

  return values;
}