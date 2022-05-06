package dummy;


public class DummyCommonValues {

    private int time;
    private int cont;

    public DummyCommonValues(){
        time = 0;
        cont = 0;
    }

    synchronized int addAndGetTime(){
        time++;
        return time;
    }

    synchronized int addAndGetCont(){
        cont++;
        return cont;
    }
}
