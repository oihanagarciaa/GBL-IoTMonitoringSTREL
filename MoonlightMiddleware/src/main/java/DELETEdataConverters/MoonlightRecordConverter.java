package DELETEdataConverters;

import eu.quanticol.moonlight.io.MoonLightRecord;

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
