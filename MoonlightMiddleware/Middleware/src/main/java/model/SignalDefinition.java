package model;

import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.EnumerationHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.SpatialTemporalSignal;
import eu.quanticol.moonlight.space.MoonLightRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SignalDefinition {
    int size, time;
    List<String> places;
    Random rand;
    RecordHandler factory;
    SpatialTemporalSignal<MoonLightRecord> citySignal;

    public SignalDefinition(int size){
        this.size = size;
        this.time = 0;
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
        rand = new Random();

        citySignal = new SpatialTemporalSignal<>(size);
        factory = new RecordHandler(new EnumerationHandler<>(String.class, places.toArray(new String[0])), DataHandler.INTEGER, DataHandler.INTEGER);
    }

    public SpatialTemporalSignal<MoonLightRecord> setUpSignal(List<Integer> noiseLevel){
        ArrayList<MoonLightRecord> signalSP = new ArrayList<MoonLightRecord>();
        for (int i = 0; i < size; i++) {
            signalSP.add(factory.fromObjectArray(places.get(i), noiseLevel.get(i), rand.nextInt(45)));
        }
        citySignal.add(time, signalSP);
        time++;

        return citySignal;
    }
}
