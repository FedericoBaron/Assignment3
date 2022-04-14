import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// the textbook The Art of Multiprocessor Programming was used as reference for this linked list
public class ConcurrentLinkedList2 {

    public Node head;
    public AtomicInteger size;

    public ConcurrentLinkedList2(){
        head = new Node(-1, -1);
        head.next = new Node(Integer.MAX_VALUE, Integer.MAX_VALUE);
        size = new AtomicInteger(2);
    }

    public boolean add(int minute, int temperature) {
        int key = minute;
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            curr.lock();
            try {
                if (validate(pred, curr)) {
                    if (curr.key == key) {
                        curr.setMaxTemperature(temperature);
                        curr.setMinTemperature(temperature);
                        return false;
                    } else {
                        Node node = new Node(minute, temperature);
                        node.next = curr;
                        pred.next = node;
                        size.getAndIncrement();
                        return true;
                    }
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    public boolean remove(int item) {
        int key = item;
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            curr.lock();
            try {
                if (validate(pred, curr)) {
                    if (curr.key == key) {
                        pred.next = curr.next;
                        size.getAndDecrement();
                        return true;
                    } else {
                        return false;
                    }
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }
        }
    }

    public boolean contains(int item) {
        int key = item;
        while (true) {
            Node pred = this.head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            curr.lock();
            try {
                if (validate(pred, curr)) {
                    return (curr.key == key);
                }
            } finally {
                pred.unlock();
                curr.unlock();
            }

        }
    }

    public boolean validate(Node pred, Node curr){
        Node node = head;
        while(node.key <= pred.key){
            if(node == pred){
                return pred.next == curr;
            }
            node = node.next;
        }
        return false;
    }

    public void findInterval(){

        Node curr = head;
        int intervalIndex = -1;
        int maxDiff = -1;
        int minTempG = -1;
        int maxTempG = -1;

        for(int i = 0; i < 50; i++){
            int minTemp = Integer.MAX_VALUE;
            int maxTemp = Integer.MIN_VALUE;
            curr = curr.next;
            Node temp = curr;
            for(int j = 0; j < 10; j++){
                minTemp = Math.min(minTemp, temp.minTemperature);
                maxTemp = Math.max((maxTemp), temp.maxTemperature);
                temp = temp.next;
            }
            if(maxTemp - minTemp > maxDiff){
                intervalIndex = i;
                maxDiff = maxTemp - minTemp;
                minTempG = minTemp;
                maxTempG = maxTemp;
            }
        }

        System.out.println("The biggest difference is in the interval " + intervalIndex + "-" + (intervalIndex + 10) + " with a difference of " + maxDiff);
        System.out.println("The highest temperature in this interval is " + maxTempG + " and the lowest temperature is " + minTempG);
    }

}

class Node {
    public Lock lock = new ReentrantLock();

    int key; // minute
    int minTemperature;
    int maxTemperature;
    Node next;

    public Node(int key, int temperature) {
        this.key = key;
        this.minTemperature = temperature;
        this.maxTemperature = temperature;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public void setMinTemperature(int temperature){
        this.minTemperature = Math.min(this.minTemperature, temperature);
    }

    public void setMaxTemperature(int temperature){
        this.minTemperature = Math.max(this.maxTemperature, temperature);
    }
}