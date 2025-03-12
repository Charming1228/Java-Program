import java.io.*;
import java.util.*;

// 抽象类 Staff
abstract class Staff {
    protected String name; // 员工姓名

    public Staff(String name) {
        this.name = name;
    }

    // 抽象方法，子类必须实现
    public abstract double calculateSalary();

    // 获取员工姓名
    public String getName() {
        return name;
    }
}

// 全职雇员类 FullTimeStaff
class FullTimeStaff extends Staff {
    private double dailySalary;  // 每天的基本工资
    private double overtimePay;  // 超时工作每小时收入
    private int workDays;        // 工作天数
    private int overtimeHours;   // 加班小时数

    public FullTimeStaff(String name, double dailySalary, double overtimePay, int workDays, int overtimeHours) {
        super(name);
        this.dailySalary = dailySalary;
        this.overtimePay = overtimePay;
        this.workDays = workDays;
        this.overtimeHours = overtimeHours;
    }

    @Override
    public double calculateSalary() {
        return (dailySalary * workDays) + (overtimePay * overtimeHours);
    }
}

// 兼职雇员类 PartTimeStaff
class PartTimeStaff extends Staff {
    private double hourlySalary; // 每小时工资
    private int workHours;       // 总工作小时数

    public PartTimeStaff(String name, double hourlySalary, int workHours) {
        super(name);
        this.hourlySalary = hourlySalary;
        this.workHours = workHours;
    }

    @Override
    public double calculateSalary() {
        return hourlySalary * workHours;
    }
}

// 工资系统主类
public class PayrollSystem {
    public static void main(String[] args) {
        // 检查参数
        if (args.length != 2) {
            System.out.println("使用方法: java PayrollSystem <文件名> <员工类型 (fulltime/parttime)>");
            return;
        }

        String fileName = args[0];
        String employeeType = args[1];

        List<Staff> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int count = Integer.parseInt(br.readLine().trim()); // 第一行是员工数量

            for (int i = 0; i < count; i++) {
                String line = br.readLine();
                if (line == null) continue;
                String[] data = line.split("\\|");

                if (employeeType.equalsIgnoreCase("fulltime") && data.length == 5) {
                    String name = data[0];
                    double dailySalary = Double.parseDouble(data[1]);
                    double overtimePay = Double.parseDouble(data[2]);
                    int workDays = Integer.parseInt(data[3]);
                    int overtimeHours = Integer.parseInt(data[4]);
                    employees.add(new FullTimeStaff(name, dailySalary, overtimePay, workDays, overtimeHours));
                } else if (employeeType.equalsIgnoreCase("parttime") && data.length == 3) {
                    String name = data[0];
                    double hourlySalary = Double.parseDouble(data[1]);
                    int workHours = Integer.parseInt(data[2]);
                    employees.add(new PartTimeStaff(name, hourlySalary, workHours));
                }
            }
        } catch (IOException e) {
            System.out.println("文件读取错误: " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.out.println("文件格式错误: " + e.getMessage());
            return;
        }

        // 输出工资信息
        for (Staff staff : employees) {
            System.out.printf("员工: %s, 类型: %s, 工资: %.2f 美元%n",
                    staff.getName(),
                    (staff instanceof FullTimeStaff) ? "全职" : "兼职",
                    staff.calculateSalary());
        }
    }
}
