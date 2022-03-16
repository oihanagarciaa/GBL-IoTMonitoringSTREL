package dataConverters;

import eu.quanticol.moonlight.io.MoonLightRecord;

public class DELETEMoonlightRecordConverter implements DataConverter
        <MoonLightRecord, String> {

    MoonLightRecord moonLightRecord;
    DELETEOfficeMoolightRecordParser converter;

    public DELETEMoonlightRecordConverter(){
        converter = new DELETEOfficeMoolightRecordParser();
    }

    @Override
    public MoonLightRecord fromMessageToData(String message) {
        moonLightRecord = converter.getAndConvertMoonLightRecords(message);
        return moonLightRecord;
    }

    //@Override
    //public void initDataConverter(int size) {
    //    moonLightRecords = converter.initDataConverter(size);
    //}
}
