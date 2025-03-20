import java.io.*;
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
    private List<Item> items;

    public Basket() {
        this.items = new ArrayList<>();
    }

    // 添加商品到篮子
    public void addItem(Item item) {
        items.add(item);
    }

    // 统计并写入购物篮详情到文件
    public void printSummaryToFile(String outputFile) {
        int bookCount = 0;
        int fruitCount = 0;
        double totalBookPrice = 0;
        double totalFruitPrice = 0;
        double totalPrice = 0;
        Item mostExpensiveItem = null;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("购物篮详情:\n");

            for (Item item : items) {
                writer.write(item.toString() + "\n");
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

            writer.write("\n统计信息:\n");
            writer.write("总物品数: " + items.size() + "\n");
            writer.write("书籍数量: " + bookCount + " 本, 总价: " + totalBookPrice + " 元\n");
            writer.write("水果数量: " + fruitCount + " 个, 总价: " + totalFruitPrice + " 元\n");
            writer.write("总价: " + totalPrice + " 元\n");

            if (mostExpensiveItem != null) {
                writer.write(
                        "\n最贵的物品: " + mostExpensiveItem.getName() + "，价格: " + mostExpensiveItem.getPrice() + " 元\n");
            }

            System.out.println("购物信息已写入 " + outputFile);

        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        }
    }
}

// 主程序
public class ShoppingCart {
    public static void main(String[] args) {
        String inputFile = "input.txt";
        String outputFile = "output.txt";
        Basket basket = new Basket();

        // 读取 input.txt 并解析商品信息
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equalsIgnoreCase("Book")) {
                    String title = parts[1];
                    String author = parts[2];
                    double price = Double.parseDouble(parts[3]);
                    basket.addItem(new Book(title, author, price));
                } else if (parts.length == 5 && parts[0].equalsIgnoreCase("Fruit")) {
                    String name = parts[1];
                    String flavor = parts[2];
                    String unit = parts[3];
                    double price = Double.parseDouble(parts[4]);
                    basket.addItem(new Fruit(name, flavor, unit, price));
                }
            }
        } catch (IOException e) {
            System.err.println("读取文件时出错: " + e.getMessage());
            return;
        }

        // 生成购物清单并写入 output.txt
        basket.printSummaryToFile(outputFile);
    }
}
