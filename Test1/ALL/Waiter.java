import java.util.concurrent.BlockingQueue;

public class Waiter extends Worker {
    public Waiter(String name, BlockingQueue<Order> deliverQueue) {
        super(name, deliverQueue);
    }

    @Override
    public void processOrder(Order order) {
        int time = order.getBeverages().size();
        System.out.printf("[%s] 正在配送订单#%03d（%d杯需%d秒）\n",
                name, order.getOrderId(), order.getBeverages().size(), time);
        try {
            Thread.sleep(time * 1000L);
            System.out.printf("[%s] 完成配送订单#%03d\n", name, order.getOrderId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
