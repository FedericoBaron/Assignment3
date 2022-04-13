import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class pa3cop4520p1 {

    public static final int NUM_MINIONS = 4;
    public static final int NUM_PRESENTS = 500000;
    public static AtomicInteger minatourRequest = new AtomicInteger(-1);
    public static AtomicInteger count = new AtomicInteger(0);
    public static AtomicInteger count2 = new AtomicInteger(0);

    public static ArrayBlockingQueue<Integer> bag;
    public static ConcurrentLinkedList chain;
    public static ExecutorService service;


    public static void main(String[] args) {

        ArrayList<Integer> originalBag = new ArrayList<>();
        for (int i = 0; i < NUM_PRESENTS; i++) {
            originalBag.add(i);
        }

        Collections.shuffle(originalBag);

        bag = new ArrayBlockingQueue<Integer>(NUM_PRESENTS, false, originalBag);

        chain = new ConcurrentLinkedList();

        long timeStart = System.currentTimeMillis();

        service = Executors.newFixedThreadPool(NUM_MINIONS + 1);
        for (int i = 0; i < NUM_MINIONS; i++) {
            service.submit(new MinatoursMinion());
        }

        service.submit(new Minotaur());


        while(!bag.isEmpty() || chain.size.get() != 2){

        }

        service.shutdown();

        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);

        } catch (Throwable e) {
            System.out.println(e);
        }

        // System.out.println("bag: "+ bag.size());
        // System.out.println("chain: " + chain.size.get());
        long timeEnd = System.currentTimeMillis();
        System.out.println("We wrote " + count.get() + " thank you notes");
        System.out.println("We added " + count2.get() + " items from the bag to the chain");
        System.out.println("Execution time: " + ((timeEnd - timeStart)) + "ms");
        

    }

    static class MinatoursMinion extends pa3cop4520p1 implements Runnable {

        public int itemTag;

        public void run() {

            // while bag is not empty or list is not 2
            while (!bag.isEmpty() || chain.size.get() != 2) {

                // look up item if the minotaur requested it
                itemTag = minatourRequest.getAndSet(-1);
                if (itemTag != -1) {
                    lookup(itemTag);
                    // if(lookup(itemTag)){
                    //     // System.out.println("The chain has item " + itemTag + ".");
                    // }
                    // else{
                    //     // System.out.println("The chain does not have item " + itemTag + ".");
                    // }
                }

                // add to chain
                if (!bag.isEmpty()) {
                    addToChain(bag.poll());
                }

                // write thank you note
                if (chain.size.get() != 2) {
                    // System.out.println("bag size is: " + bag.size());
                    // WE HAVE A PROBLEM WITH REPEAT THANK YOU!
                    writeThankYou(chain.head.next.key);
                }
            }
        }

        // looks up
        public static boolean lookup(int item) {
            return chain.contains(item);
        }

        // add to chain
        public static void addToChain(int item){
            if(chain.add(item)){
                count2.getAndIncrement();
            }
        }

        // write thank you note
        public static void writeThankYou(int item){
            
            if(chain.remove(item)){
                count.getAndIncrement();
                // System.out.println("Thank you guest " + item);
            }
        }
    }

    static class Minotaur extends pa3cop4520p1 implements Runnable {
        Random ran = new Random();
        public void run() {
            
            while(!service.isShutdown()){
                // try {
                //     Thread.sleep(1);
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // } finally {
                //     if(minatourRequest.get() == -1)
                        minatourRequest.set(ran.nextInt(NUM_PRESENTS));
                // }
            }
        }   
    }

}
