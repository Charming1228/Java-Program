import java.util.*;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;


public class SmartCafeSystem {
    public static void main(String[] args) {
        // 1. 创建两个订单处理队列
        BlockingQueue<Order> makeQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Order> deliverQueue = new LinkedBlockingQueue<>();

        // 2. 启动咖啡师线程（从makeQueue取，处理后放入deliverQueue）
        Thread barista1 = new Barista("咖啡师1", makeQueue, deliverQueue);
        Thread barista2 = new Barista("咖啡师2", makeQueue, deliverQueue);
        barista1.start();
        barista2.start();

        // 3. 启动服务员线程（从deliverQueue取，配送）
        Thread waiter1 = new Waiter("服务员1", deliverQueue);
        Thread waiter2 = new Waiter("服务员2", deliverQueue);
        Thread waiter3 = new Waiter("服务员3", deliverQueue);
        waiter1.start();
        waiter2.start();
        waiter3.start();

        // 4. 模拟每5秒生成一个随机订单放入makeQueue
        List<Supplier<Beverage>> drinkSuppliers = Arrays.asList(
                Cappuccino::new,
                MilkTea::new,
                FruitTea::new);
        Random rand = new Random();

        while (true) {
            try {
                int drinkCount = rand.nextInt(3) + 1;
                List<Beverage> drinks = new ArrayList<>();
                for (int i = 0; i < drinkCount; i++) {
                    drinks.add(drinkSuppliers.get(rand.nextInt(drinkSuppliers.size())).get());
                }

                Order order = new Order(drinks);
                System.out.println("新订单：" + order);

                makeQueue.put(order); // 放入制作队列

                Thread.sleep(5000); // 每5秒生成一个新订单
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
