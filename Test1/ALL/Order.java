import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    private static final AtomicInteger count = new AtomicInteger(1);
    private final int orderId;
    private final List<Beverage> beverages;

    public Order(List<Beverage> beverages) {
        this.orderId = count.getAndIncrement();
        this.beverages = beverages;
    }

    public int getOrderId() {
        return orderId;
    }

    public List<Beverage> getBeverages() {
        return beverages;
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
        return String.format("订单#%03d [%s] 总需%d秒", orderId, names, getTotalPrepareTime());
    }
}
