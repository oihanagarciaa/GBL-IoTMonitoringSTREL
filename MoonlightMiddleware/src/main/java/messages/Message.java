package messages;

public abstract class Message<E> {
    int id;
    long time;
    E valueElement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setValueElement(E valueElement) {
        this.valueElement = valueElement;
    }

    public long getTime() {
        return time;
    }

    public E getValueElement() {
        return valueElement;
    }

    abstract public void transformReceivedData(String topic, String message);
}
