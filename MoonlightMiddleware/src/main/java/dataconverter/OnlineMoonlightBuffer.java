package dataconverter;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.Update;

import java.util.List;

public class OnlineMoonlightBuffer implements DataConverter
        <Update<Double, List<MoonLightRecord>>, String> {

    List<MoonLightRecord> moonLightRecords;
    ConverterStringMoonlightRecord converter;

    public OnlineMoonlightBuffer(){
        converter = new ConverterStringMoonlightRecord();
    }

    @Override
    public Update<Double, List<MoonLightRecord>> fromMessageToMonitorData(int id, String message) {
        moonLightRecords = converter.getMoonLightRecords(id, message);
        return getUpdate();
    }

    double t = 0;
    public Update<Double, List<MoonLightRecord>> getUpdate(){
        //TODO: change the time

        return new Update<>(t, ++t, moonLightRecords);
    }

    @Override
    public void initDataConverter(int size) {
        moonLightRecords = converter.initDataConverter(size);
    }
}
