package messages;

import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;

public class ResultsMessage<V extends Comparable<V>> implements Message{
    SpaceTimeSignal<Double, Box<V>> results;

    public ResultsMessage(SpaceTimeSignal<Double, Box<V>> results){
        this.results = results;
    }
}
