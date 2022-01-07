package model;

import eu.quanticol.moonlight.signal.MoonLightRecord;
import eu.quanticol.moonlight.signal.SpatialTemporalSignal;

import java.io.Serializable;

public class DataSerializable implements Serializable {
    final int SIZE = 6;
    SpatialTemporalSignal<MoonLightRecord> citySignal;

    public DataSerializable(){
        citySignal = new SpatialTemporalSignal<>(SIZE);
    }
}