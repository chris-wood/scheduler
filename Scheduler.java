import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scheduler {
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition(); 

    private PriorityQueue<Task> heap;
    private ExecutorService threadPool;

    private Thread scheduleThread;
    private AtomicBoolean killSwitch;

    public Scheduler() {
        int numCores = Runtime.getRuntime().availableProcessors();
        threadPool = Executors.newFixedThreadPool(2); // numCores
        heap = new PriorityQueue<Task>();
        killSwitch = new AtomicBoolean(true);
        scheduleThread = new ScheduleThread(killSwitch, heap, lock, notEmpty, threadPool);
    }

    public void start() {
        scheduleThread.start();
    }

    public void stop() {
        killSwitch.set(false);
        synchronized(scheduleThread) {
            scheduleThread.notifyAll();
        }
        try {
            scheduleThread.join();
        } catch (Exception e) {} 
    }

    public synchronized void runAfter(long ms, Runnable r) {
        lock.lock();
        long time = System.currentTimeMillis() + (ms * 1000);
        heap.add(new Task(time, r));
        notEmpty.signal();
        lock.unlock();
    }
    public class ScheduleThread extends Thread {
        private AtomicBoolean killSwitch;
        private PriorityQueue<Task> heap;
        private Lock lock;
        private Condition notEmpty;
        private ExecutorService threadPool;

        public ScheduleThread(AtomicBoolean ks, PriorityQueue<Task> heap, Lock lock, Condition notEmpty, ExecutorService threadPool) {
            this.killSwitch = ks;
            this.heap = heap;
            this.lock = lock;
            this.notEmpty = notEmpty;
            this.threadPool = threadPool;
        }

        public void run() {
            while (killSwitch.get() || heap.size() > 0) {
                lock.lock();
                while (heap.size() == 0) {
                    try {
                        notEmpty.await();
                    } catch (Exception e) {}
                }
    
                Task t = heap.peek();
                if (t.getTimeToRun() < System.currentTimeMillis()) {
                    threadPool.submit(t.getRunnable());
                    heap.poll();
                } else {
                    long timeToSleep = t.getTimeToRun() - System.currentTimeMillis();
                    try {
                        notEmpty.awaitNanos(timeToSleep * 1000000);    
                    } catch (Exception e) {}
                }

                lock.unlock(); 
            }

            while (heap.size() > 0) {
                Task t = heap.poll();
                threadPool.submit(t.getRunnable());
            }
            threadPool.shutdown();
        }
    }
}
