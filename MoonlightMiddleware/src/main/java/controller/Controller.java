package controller;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.Update;
import model.DataConverter;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    DataConverter converter;
    /*
    /!\ If different threads are going to access here at the same time, I have to make it synchronized
     */
    List<MoonLightRecord> records;

    public Controller() {
        converter = new DataConverter();
        records = new ArrayList<>();
    }

    public void convertSignalForMoonlight(String message){
        MoonLightRecord m = converter.convertJSONtoMoonlightRecord(converter.convertStringtoJSONObject(message));
        /* I need some value to know witch sensor we are talking about
        Then, add the moonlightRecord in the adequate position of the list
        records.add(index, m);
         */
        records.add(m);
    }

    public Update<Double, List<MoonLightRecord>> getUpdate(){
        Update<Double, List<MoonLightRecord>> update = converter.createUpdate(records);
        records.clear();
        return update;
    }
}
