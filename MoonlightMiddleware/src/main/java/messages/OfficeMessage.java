package messages;

import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.base.TupleType;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Arrays;
import java.util.List;

public class OfficeMessage implements Message {
    private TupleType tupleType;
    private final List<String> places;
    private int id;
    private double time;
    private Tuple value;

    public OfficeMessage(){
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
        tupleType = TupleType.of(String.class, Integer.class, Integer.class);
    }

    public void transformReceivedData(String topic, String message) {
        getIdFromTopic(topic);
        //TODO: Change the way the time is set
        JSONObject obj = (JSONObject) JSONValue.parse(message);
        convertJSONtoMoonlightRecord(obj);
        this.setMessageData(id, time, value);
    }

    private void getIdFromTopic(String topic) throws UnsupportedOperationException{
        int id = 0;
        try{
            String[] topics = topic.split("/");
            id = Integer.parseInt(topics[topics.length-1]);
        }catch (NumberFormatException e){
            throw new UnsupportedOperationException("Wrong topic format");
        }
        this.id = id;
    }

    private void convertJSONtoMoonlightRecord(JSONObject j) throws UnsupportedOperationException{
        //TODO: update converter
        Tuple valueElement;
        double time = 0;
        /*For the moment I suppose that the JSON file is like:
         {
            "place": 3
            "noise": 40
            "people": 30
            "time": 1.0
         }*/
        try{
            long place = (long) j.get("place");
            long noise = (long) j.get("noise");
            long people = (long) j.get("people");
            time = (double) j.get("time");
            valueElement = Tuple.of(tupleType, places.get(Math.toIntExact(place)),
                    Math.toIntExact(noise), Math.toIntExact(people));

        //TODO: make the try catch cleaner
        }catch (NullPointerException e){
            throw new UnsupportedOperationException("Invalid data (JSON) format");
        }
        this.value = valueElement;
        this.time = time;
    }

    private Tuple valueElement;

    public void setMessageData(int id, double time, Tuple element){
        this.id = id;
        this.time = time;
        this.valueElement = element;
    }

    public int getId() {
        return id;
    }

    public double getTime() {
        return time;
    }

    public Tuple getValueElement() {
        return valueElement;
    }
}
