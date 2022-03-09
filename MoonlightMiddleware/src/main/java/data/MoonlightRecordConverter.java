package data;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.Update;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.List;

//TODO: Maybe this needs to be to data converter to TimeChain
public class MoonlightRecordConverter implements DataConverter
        <MoonLightRecord, String> {

    MoonLightRecord moonLightRecord;
    OfficeMoolightRecordParser converter;

    public MoonlightRecordConverter(){
        converter = new OfficeMoolightRecordParser();
    }

    @Override
    public MoonLightRecord fromMessageToMonitorData(String message) {
        moonLightRecord = converter.getAndConvertMoonLightRecords(message);
        return moonLightRecord;
    }

    //@Override
    //public void initDataConverter(int size) {
    //    moonLightRecords = converter.initDataConverter(size);
    //}
}
