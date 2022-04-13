import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentLinkedList {

    public Node head;
    public AtomicInteger size;

    public ConcurrentLinkedList(){
        head = new Node(-1);
        head.next = new Node(Integer.MAX_VALUE);
        size = new AtomicInteger(2);
    }

    public boolean add(int item) {
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
                        return false;
                    } else {
                        Node node = new Node(item);
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

}

class Node {
    public Lock lock = new ReentrantLock();

    int key;
    Node next;

    public Node(int key) {
        this.key = key;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}