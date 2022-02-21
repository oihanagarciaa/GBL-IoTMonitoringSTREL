package dataconverter;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.Update;
import mycontrollers.MyDataConverter;

import java.util.List;

public class OnlineMoonlightDataConverter implements DataConverter
        <Update<Double, List<MoonLightRecord>>, String> {


    @Override
    public Update<Double, List<MoonLightRecord>> fromMessageToMonitorData(String message) {
        MyDataConverter dc = new MyDataConverter();
        //Update<Double, List<MoonLightRecord>> update = dc.convertJSONtoMoonlightRecord(dc.convertStringtoJSONObject(message));
        //TODO: convert to update
        return null;
    }
}
