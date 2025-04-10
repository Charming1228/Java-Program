public class DeadlockDemo2 {
    static class Resource {
        void methodA(Resource r) {
            synchronized (this) {
                System.out.println(Thread.currentThread().getName() + " acquired: " + this);
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                }
                synchronized (r) {
                    System.out.println(Thread.currentThread().getName() + " also acquired: " + r);
                }
            }
        }
    }

    public static void main(String[] args) {
        Resource r1 = new Resource();
        Resource r2 = new Resource();

        Thread t1 = new Thread(() -> r1.methodA(r2), "Thread-1");
        Thread t2 = new Thread(() -> r2.methodA(r1), "Thread-2");

        t1.start();
        t2.start();
    }
}
// This code demonstrates a deadlock situation where two threads are trying to acquire locks on two resources in opposite order.
// Thread 1 acquires the lock on Resource r1 and then tries to acquire the lock on Resource r2, while Thread 2 acquires the lock on Resource r2 and then tries to acquire the lock on Resource r1.
// This results in a deadlock where both threads are waiting for each other to release the locks, causing the program to hang.
// The output will show that both threads are waiting for each other to release the locks, leading to a deadlock situation.