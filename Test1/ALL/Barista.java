import java.util.concurrent.BlockingQueue;


public class Barista extends Worker {
    private BlockingQueue<Order> deliverQueue;

    public Barista(String name, BlockingQueue<Order> makeQueue, BlockingQueue<Order> deliverQueue) {
        super(name, makeQueue);
        this.deliverQueue = deliverQueue;
    }

    @Override
    public void processOrder(Order order) {
        System.out.printf("[%s] 开始处理订单#%03d（需%d秒）\n",
                name, order.getOrderId(), order.getTotalPrepareTime());
        try {
            Thread.sleep(order.getTotalPrepareTime() * 1000L);
            System.out.printf("[%s] 完成订单#%03d\n", name, order.getOrderId());
            deliverQueue.put(order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
