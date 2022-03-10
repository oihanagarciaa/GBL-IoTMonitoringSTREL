package messages;

public abstract class Message<E> {
    int id;
    long time;
    E valueElement;

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public E getValueElement() {
        return valueElement;
    }

    abstract public void transformReceivedData(String topic, String message);
}
