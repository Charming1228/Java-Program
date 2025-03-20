import java.util.ArrayList;
import java.util.List;

// 物品接口
interface Item {
    double getPrice(); // 获取物品价格

    String getName(); // 获取物品名称
}

// 书籍类（非食用）
class Book implements Item {
    private String title;
    private String author;
    private double price;

    public Book(String title, String author, double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getName() {
        return "书籍: " + title;
    }

    @Override
    public String toString() {
        return String.format("书籍 - 标题: %s, 作者: %s, 价格: %.2f 元", title, author, price);
    }
}

// 水果类（可食用）
class Fruit implements Item {
    private String name;
    private String flavor;
    private String unit;
    private double price;

    public Fruit(String name, String flavor, String unit, double price) {
        this.name = name;
        this.flavor = flavor;
        this.unit = unit;
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getName() {
        return "水果: " + name;
    }

    @Override
    public String toString() {
        return String.format("水果 - 名称: %s, 口味: %s, 单位: %s, 价格: %.2f 元", name, flavor, unit, price);
    }
}

// 购物篮类
class Basket {
    private List<Item> items; // 用 ArrayList 存储物品

    public Basket() {
        this.items = new ArrayList<>();
    }

    // 添加商品到篮子
    public void addItem(Item item) {
        items.add(item);
    }

    // 从篮子中移除商品
    public void removeItem(Item item) {
        items.remove(item);
    }

    // 统计并打印购物篮详情
    public void printSummary() {
        int bookCount = 0;
        int fruitCount = 0;
        double totalBookPrice = 0;
        double totalFruitPrice = 0;
        double totalPrice = 0;
        Item mostExpensiveItem = null;

        System.out.println("\n购物篮详情:");
        for (Item item : items) {
            System.out.println(item);
            totalPrice += item.getPrice();

            if (item instanceof Book) {
                bookCount++;
                totalBookPrice += item.getPrice();
            } else if (item instanceof Fruit) {
                fruitCount++;
                totalFruitPrice += item.getPrice();
            }

            if (mostExpensiveItem == null || item.getPrice() > mostExpensiveItem.getPrice()) {
                mostExpensiveItem = item;
            }
        }

        System.out.println("\n统计信息:");
        System.out.println("总物品数: " + items.size());
        System.out.println("书籍数量: " + bookCount + " 本, 总价: " + totalBookPrice + " 元");
        System.out.println("水果数量: " + fruitCount + " 个, 总价: " + totalFruitPrice + " 元");
        System.out.println("总价: " + totalPrice + " 元");

        if (mostExpensiveItem != null) {
            System.out
                    .println("\n最贵的物品: " + mostExpensiveItem.getName() + "，价格: " + mostExpensiveItem.getPrice() + " 元");
        }
    }
}

// 主程序
public class ShoppingCart {
    public static void main(String[] args) {
        Basket basket = new Basket();

        // 创建书籍和水果
        Book book1 = new Book("Java编程思想", "Bruce Eckel", 89.99);
        Book book2 = new Book("算法导论", "Thomas H. Cormen", 109.50);
        Fruit fruit1 = new Fruit("苹果", "甜", "斤", 10.00);
        Fruit fruit2 = new Fruit("香蕉", "香甜", "斤", 8.50);
        Fruit fruit3 = new Fruit("橙子", "酸甜", "斤", 12.00);

        // 添加到购物篮
        basket.addItem(book1);
        basket.addItem(book2);
        basket.addItem(fruit1);
        basket.addItem(fruit2);
        basket.addItem(fruit3);

        // 打印购物篮信息
        basket.printSummary();
    }
}
