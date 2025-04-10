public class DeadlockDemo1 {
    private static final Object LockA = new Object();
    private static final Object LockB = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (LockA) {
                System.out.println("Thread 1: Holding LockA...");
                try { Thread.sleep(100); } catch (Exception e) {}
                synchronized (LockB) {
                    System.out.println("Thread 1: Acquired LockB!");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (LockB) {
                System.out.println("Thread 2: Holding LockB...");
                try { Thread.sleep(100); } catch (Exception e) {}
                synchronized (LockA) {
                    System.out.println("Thread 2: Acquired LockA!");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
// This code demonstrates a deadlock situation where two threads are trying to acquire locks on two objects in opposite order.
// Thread 1 acquires LockA and then tries to acquire LockB, while Thread 2 acquires LockB and then tries to acquire LockA.
// This results in a deadlock where both threads are waiting for each other to release the locks, causing the program to hang.
