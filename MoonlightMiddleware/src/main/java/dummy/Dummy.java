package dummy;


public class Dummy {

    public static void main(String[] args) {
        DummyCommonValues dummyCommonValues = new DummyCommonValues();
        Thread t1 = new Thread(new DummyTask(0, dummyCommonValues));
        Thread t2 = new Thread(new DummyTask(1, dummyCommonValues));
        Thread t3 = new Thread(new DummyTask(2, dummyCommonValues));

        long startingTime = System.currentTimeMillis();
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join(60000);
            t2.join(60000);
            t3.join(60000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long endingTime = System.currentTimeMillis();
        float duration = (float) ((endingTime - startingTime) / 1000.0);
        System.out.println("DURATION: "+duration);
    }
}
