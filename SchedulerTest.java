import java.util.Random;

public class SchedulerTest {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("usage: java SchedulerTask <num tasks> <num time>");
        }

        int numTasks = Integer.parseInt(args[0]);       
        int sleepTime = Integer.parseInt(args[1]);
        Random rng = new Random();

        Scheduler scheduler = new Scheduler();
        scheduler.start();

        for (int i = 0; i < numTasks; i++) {
            int timeToSleep = rng.nextInt(sleepTime) + 1;
            System.out.println("Run " + i + " after " + timeToSleep);
            scheduler.runAfter(i + 10, new SleepTask(i, sleepTime));
        }

        scheduler.stop();
        System.out.println("Done.");
    }
}