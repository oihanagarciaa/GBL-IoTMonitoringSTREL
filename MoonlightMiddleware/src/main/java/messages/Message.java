package messages;

/**
 * Object oriented
 */
public abstract class Message<E> {
    private int id;
    private double time;
    private E valueElement;

    public void setMessageData(int id, double time, E element){
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

    public E getValueElement() {
        return valueElement;
    }

    abstract public void transformReceivedData(String topic, String message);

    abstract public E getDefaulValue();
}
