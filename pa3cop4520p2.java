import java.util.Collections;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class pa3cop4520p2 {

    public static ExecutorService service;
    public static final int NUM_SENSORS = 8;
    public static final int NUM_HOURS = 3;
    public static final int MIN_TEMP = -100;
    public static final int MAX_TEMP = 70;
    public static CyclicBarrier barrier;
    public static AtomicInteger isRun;
    public static PriorityBlockingQueue<Integer> maxTemperatures;
    public static PriorityBlockingQueue<Integer> minTemperatures;
    public static ConcurrentLinkedList2 list;

    public static AtomicInteger numRunningThreads;

    public static void main(String[] args) {
        long timeStart = System.currentTimeMillis();

        isRun = new AtomicInteger(0);
        barrier = new CyclicBarrier(NUM_SENSORS);
        maxTemperatures = new PriorityBlockingQueue<Integer>(6);
        minTemperatures = new PriorityBlockingQueue<Integer>(6, Collections.reverseOrder());
        numRunningThreads = new AtomicInteger(0);
        list = new ConcurrentLinkedList2();

        service = Executors.newFixedThreadPool(NUM_SENSORS);
        for (int i = 0; i < NUM_SENSORS; i++) {
            service.submit(new Sensor(i));
        }

        while (numRunningThreads.get() > 0) {

        }

        service.shutdown();

        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);

        } catch (Throwable e) {
            System.out.println(e);
        }

        long timeEnd = System.currentTimeMillis();
        System.out.println("Execution time: " + ((timeEnd - timeStart)) + "ms");
    }

    static class Sensor extends pa3cop4520p1 implements Runnable {

        Random ran = new Random();
        int threadNum;

        public Sensor(int num) {
            this.threadNum = num;
        }

        public void run() {
            numRunningThreads.incrementAndGet();

            for (int i = 0; i < NUM_HOURS; i++) {

                for (int j = 0; j < 60; j++) {
                    int nextReading = ran.nextInt(MIN_TEMP, MAX_TEMP + 1);

                    // add to maxTemps
                    if(!maxTemperatures.contains(nextReading))
                        maxTemperatures.offer(nextReading);
                    if (maxTemperatures.size() > 5) {
                        maxTemperatures.poll();
                    }

                    // add to minTemps
                    if(!minTemperatures.contains(nextReading))
                        minTemperatures.offer(nextReading);
                    if (minTemperatures.size() > 5) {
                        minTemperatures.poll();
                    }

                    list.add(j, nextReading);

                }

                // every hour we do one report
                try {
                    barrier.await(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

                // make report and reset everything
                if (threadNum == 0) {
                    System.out.println("Report for hour " + (i + 1) + ":");
                    doReport();
                }

                // every hour we do one report
                try {
                    barrier.await(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

            }

            numRunningThreads.decrementAndGet();

        }

        public void doReport() {

            System.out.println();

            // print lowest 5 temperatures
            System.out.println("Lowest 5 temperatures: ");
            while (minTemperatures.size() > 0) {
                System.out.print(minTemperatures.poll() + " ");
            }
            System.out.println();

            System.out.println();

            // print highest 5 temperatures
            System.out.println("Highest 5 temperatures: ");
            while (maxTemperatures.size() > 0) {
                System.out.print(maxTemperatures.poll() + " ");
            }
            System.out.println();
            System.out.println();

            // print 10-minute interval of time when the largest temperature difference was
            // observed
            System.out.println("10 minute interval with largest temperature difference: ");
            list.findInterval();
            list = new ConcurrentLinkedList2();

            System.out.println("-------------------------------");
        }
    }
}
