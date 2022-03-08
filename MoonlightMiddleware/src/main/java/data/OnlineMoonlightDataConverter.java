package data;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.Update;

import java.util.List;

//TODO: Maybe this needs to be to data converter to TimeChain
public class OnlineMoonlightDataConverter implements DataConverter
        <Update<Double, List<MoonLightRecord>>, String> {

    List<MoonLightRecord> moonLightRecords;
    StringToMoolightRecordParser converter;

    public OnlineMoonlightDataConverter(){
        converter = new StringToMoolightRecordParser();
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
