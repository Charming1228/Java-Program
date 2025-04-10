public class SyncExample {
    public synchronized void instanceMethod() {
        System.out.println(Thread.currentThread().getName() + " 正在执行实例方法...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println(Thread.currentThread().getName() + " 退出实例方法");
    }

    public static synchronized void staticMethod() {
        System.out.println(Thread.currentThread().getName() + " 正在执行静态方法...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println(Thread.currentThread().getName() + " 退出静态方法");
    }

    public static void main(String[] args) {
        SyncExample obj1 = new SyncExample();
        SyncExample obj2 = new SyncExample();

        Thread t1 = new Thread(() -> obj1.instanceMethod(), "线程A");
        Thread t2 = new Thread(() -> obj2.instanceMethod(), "线程B");
        Thread t3 = new Thread(SyncExample::staticMethod, "线程C");
        Thread t4 = new Thread(SyncExample::staticMethod, "线程D");

        t1.start(); // 不会阻塞 t2
        t2.start(); // 因为是不同实例

        // 静态同步方法使用的是类锁
        t3.start();
        t4.start(); // 会等待 t3 释放类锁
    }
}
