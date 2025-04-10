public class DeadlockDemo3 {
    static class Lock {
        private final String name;

        public Lock(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        Lock a = new Lock("A");
        Lock b = new Lock("B");
        Lock c = new Lock("C");

        new Thread(() -> lock(a, b)).start();
        new Thread(() -> lock(b, c)).start();
        new Thread(() -> lock(c, a)).start();
    }

    static void lock(Lock first, Lock second) {
        synchronized (first) {
            System.out.println(Thread.currentThread().getName() + " locked " + first);
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
            synchronized (second) {
                System.out.println(Thread.currentThread().getName() + " locked " + second);
            }
        }
    }
}
// This code demonstrates a deadlock situation where three threads are trying to acquire locks on three resources in a circular manner.
// Thread 1 acquires the lock on Resource A and then tries to acquire the lock on Resource B, Thread 2 acquires the lock on Resource B and then tries to acquire the lock on Resource C, and Thread 3 acquires the lock on Resource C and then tries to acquire the lock on Resource A.
// This results in a deadlock where all three threads are waiting for each other to release the locks, causing the program to hang.
// The output will show that all three threads are waiting for each other to release the locks, leading to a deadlock situation.