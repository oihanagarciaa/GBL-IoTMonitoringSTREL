package dataconverter;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.EnumerationHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.online.Update;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import services.OnlineMoonlightService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnlineMoonlightBuffer implements DataConverter
        <Update<Double, List<MoonLightRecord>>, String> {

    List<MoonLightRecord> moonLightRecords;
    ConverterStringMoonlightRecord converter;

    public OnlineMoonlightBuffer(){
        converter = new ConverterStringMoonlightRecord();
    }

    @Override
    public Update<Double, List<MoonLightRecord>> fromMessageToMonitorData(String message) {
        moonLightRecords = converter.getMoonLightRecords(message);
        return getUpdate();
    }

    double t = 0;
    public Update<Double, List<MoonLightRecord>> getUpdate(){
        //TODO: think how to set the time
        Update<Double, List<MoonLightRecord>> update = new Update<>(t, ++t, moonLightRecords);

        return update;
    }

    @Override
    public void initDataConverter(int size) {
        moonLightRecords = converter.initDataConverter(size);
    }
}
