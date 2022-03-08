package data;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.*;
import eu.quanticol.moonlight.signal.online.TimeChain;

import javax.sound.midi.Soundbank;

//TODO: DELETE this class
public class Provisional {
    TimeChain<Double, MoonLightRecord> myTimeChain;
    Signal<MoonLightRecord> mySignal;
    RecordHandler factory;

    public Provisional(){
        factory = new RecordHandler(DataHandler.INTEGER, DataHandler.INTEGER);
        setMyTimeChain();
    }

    public void setMyTimeChain(){
        MoonLightRecord moonLightRecord = factory.fromObjectArray(4, 5);
        myTimeChain = new TimeChain<Double, MoonLightRecord>(60.0);
        myTimeChain.add(new Segment<>(1.0, moonLightRecord));
        myTimeChain.add(new Segment<>(3.0, moonLightRecord));
        MoonLightRecord moonLightRecord2 = factory.fromObjectArray(6, 10);
        myTimeChain.add(new Segment<>(5.0, moonLightRecord2));

        System.out.println(myTimeChain.toUpdates().toString());
        System.out.println("TIME 4: "+myTimeChain.toUpdates().get(2));
        System.out.println(myTimeChain.subChain(0, 1, 3.0).toString());
        System.out.println(myTimeChain.get(2));
        myTimeChain.toUpdates().stream().forEach(u -> {
            System.out.println("Start: "+u.getStart() + " End: "+u.getEnd());
            if(u.getStart() <= 4.0 && 4.0 < u.getEnd()){
                System.out.println("V: "+u.getValue());
            }
        });
        myTimeChain.toUpdates().stream().forEach(u -> {
            if(u.getStart() == 4.0 && u.getEnd() == 5.0){
                System.out.println("V2: "+u.getValue());
            }
        });
    }

    //TODO: TIME CHAIN Questions
    // 1- Time chain can't change previous values?
    // 2- A time chain for each sensor?
    public static void main(String[] args) {
        Provisional provisional = new Provisional();
    }
}
