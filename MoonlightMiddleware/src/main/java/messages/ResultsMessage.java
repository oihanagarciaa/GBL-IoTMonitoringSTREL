package messages;

import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;

import java.util.List;

public class ResultsMessage<V extends Comparable<V>> implements Message{
    SpaceTimeSignal<Double, Box<V>> results;

    public ResultsMessage(SpaceTimeSignal<Double, Box<V>> results){
        this.results = results;
    }

    @Override
    public String toString() {
        List<String> listResults = results.getSegments().toList().stream()
                .map(Object::toString).toList();
        StringBuilder bld = new StringBuilder();
        for (int i = 0; i < listResults.size(); i++){
            bld.append("\t"+listResults.get(i)+"\n");
        }
        return bld.toString();
    }

    public SpaceTimeSignal<Double, Box<V>> getSpaceTimeSignal(){
        return results;
    }
}
