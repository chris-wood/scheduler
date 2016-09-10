public class SleepTask implements Runnable {

    private int timeToSleep;
    private int id;

    public SleepTask(int id, int n) {
        this.id = id;
        timeToSleep = n;
    }

    public void run() {
        System.out.println("Task " + id + " going to sleep...");
        try {
            Thread.sleep(timeToSleep * 1000);
        } catch (Exception e) {}
        System.out.println("Task " + id + " waking up...");
    }
}
