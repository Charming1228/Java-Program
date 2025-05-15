import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Supplier;

// ===== 抽象饮品类 =====
abstract class Beverage {
    protected String name;
    protected int prepareTime;

    public Beverage(String name, int prepareTime) {
        this.name = name;
        this.prepareTime = prepareTime;
    }

    public String getName() {
        return name;
    }

    public int getPrepareTime() {
        return prepareTime;
    }
}

// ===== 具体饮品类 =====
class Cappuccino extends Beverage {
    public Cappuccino() {
        super("卡布奇诺", 4);
    }
}

class MilkTea extends Beverage {
    private boolean hasPearl;

    public MilkTea() {
        this(false);
    }

    public MilkTea(boolean hasPearl) {
        super("奶茶" + (hasPearl ? "（加珍珠）" : ""), 3 + (hasPearl ? 1 : 0));
        this.hasPearl = hasPearl;
    }
}

class FruitTea extends Beverage {
    public FruitTea() {
        super("水果茶", 2);
    }
}

// ===== 订单类（支持VIP优先） =====
class Order implements Comparable<Order> {
    private static final AtomicInteger count = new AtomicInteger(1);
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    private final int orderId;
    private final List<Beverage> beverages;
    private final boolean isVIP;

    public Order(List<Beverage> beverages, boolean isVIP) {
        this.orderId = idGenerator.getAndIncrement();
        this.beverages = beverages;
        this.isVIP = isVIP;
    }

    public int getOrderId() {
        return orderId;
    }

    public List<Beverage> getBeverages() {
        return beverages;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public int getTotalPrepareTime() {
        return beverages.stream().mapToInt(Beverage::getPrepareTime).sum();
    }

    @Override
    public String toString() {
        StringBuilder names = new StringBuilder();
        for (Beverage b : beverages) {
            names.append(b.getName()).append(", ");
        }
        if (names.length() > 0)
            names.setLength(names.length() - 2);
        return String.format("订单#%03d [%s] 总需%d秒%s",
                orderId, names, getTotalPrepareTime(), isVIP ? "（VIP）" : "");
    }

    @Override
    public int compareTo(Order other) {
        return Boolean.compare(other.isVIP, this.isVIP); // VIP 优先
    }
}

// ===== 抽象工作人员类 =====
abstract class Worker extends Thread {
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

// ===== 咖啡师 =====
class Barista extends Worker {
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

// ===== 服务员 =====
class Waiter extends Worker {
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

// ===== 主程序（含扩展功能） =====
public class SmartCafeSystem {
    private static final Map<String, AtomicInteger> salesStats = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        // 使用优先队列实现 VIP 优先功能
        BlockingQueue<Order> makeQueue = new PriorityBlockingQueue<>();
        BlockingQueue<Order> deliverQueue = new LinkedBlockingQueue<>();

        new Barista("咖啡师1", makeQueue, deliverQueue).start();
        new Barista("咖啡师2", makeQueue, deliverQueue).start();

        new Waiter("服务员1", deliverQueue).start();
        new Waiter("服务员2", deliverQueue).start();
        new Waiter("服务员3", deliverQueue).start();

        List<Supplier<Beverage>> drinkSuppliers = Arrays.asList(
                Cappuccino::new,
                () -> new MilkTea(new Random().nextBoolean()), // 有时加珍珠
                FruitTea::new);

        Random rand = new Random();

        while (true) {
            try {
                int drinkCount = rand.nextInt(3) + 1;
                boolean isVIP = rand.nextDouble() < 0.3; // 30% VIP
                List<Beverage> drinks = new ArrayList<>();
                for (int i = 0; i < drinkCount; i++) {
                    Beverage b = drinkSuppliers.get(rand.nextInt(drinkSuppliers.size())).get();
                    drinks.add(b);
                    // 销量统计
                    salesStats.computeIfAbsent(b.getName(), k -> new AtomicInteger(0)).incrementAndGet();
                }

                Order order = new Order(drinks, isVIP);
                System.out.println("新订单：" + order);
                makeQueue.put(order);

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // 可在程序结尾输出饮品销量：
    // System.out.println("饮品销量统计：" + salesStats);
}
