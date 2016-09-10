public class Task implements Comparable<Task> {
    private Runnable runner;
    private long scheduleTime;

    public Task(long timeToRun, Runnable runnable) {
        this.runner = runnable;
        this.scheduleTime = timeToRun;        
    }

    public int compareTo(Task other) {
        if (scheduleTime < other.getTimeToRun()) {
            return -1;
        } else if (scheduleTime == other.getTimeToRun()) {
            return 0;
        } else {
            return 1;
        }
    }

    public long getTimeToRun() {
        return scheduleTime;
    }

    public Runnable getRunnable() {
        return runner;
    }
}
