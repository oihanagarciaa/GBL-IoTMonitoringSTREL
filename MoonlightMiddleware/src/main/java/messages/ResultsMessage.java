package messages;

import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;

import java.util.List;
import java.util.stream.Collectors;

public class ResultsMessage<V extends Comparable<V>> implements Message{
    SpaceTimeSignal<Double, Box<V>> results;

    public ResultsMessage(SpaceTimeSignal<Double, Box<V>> results){
        this.results = results;
    }

    @Override
    public String toString() {
        List<String> listResults = results.getSegments().toList().stream()
                .map(Object::toString).collect(Collectors.toList());
        String r = "";
        for (int i = 0; i < listResults.size(); i++){
            r += "\t"+listResults.get(i)+"\n";
        }
        return r;
    }
}
