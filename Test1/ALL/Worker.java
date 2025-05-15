import java.util.concurrent.BlockingQueue;

public abstract class Worker extends Thread {
    protected String name;
    protected BlockingQueue<Order> queue;

    public Worker(String name, BlockingQueue<Order> queue) {
        this.name = name;
        this.queue = queue;
    }

    public abstract void processOrder(Order order);

    @Override
    public void run() {
        while (true) {
            try {
                Order order = queue.take();
                processOrder(order);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
