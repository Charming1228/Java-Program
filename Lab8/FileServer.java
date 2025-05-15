import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class FileServer {
    private static final int PORT = 12345;
    private static final String UPLOAD_DIR = "uploads/";

    public static void main(String[] args) throws IOException {
        new File(UPLOAD_DIR).mkdirs();
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("服务器启动，端口：" + PORT);
            while (true) {
                Socket client = server.accept();
                pool.execute(new ClientHandler(client));
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket client;

        ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(client.getInputStream());
                 DataOutputStream out = new DataOutputStream(client.getOutputStream())) {

                String command = in.readUTF();
                if ("UPLOAD".equals(command)) {
                    String fileName = in.readUTF();
                    long size = in.readLong();
                    File file = new File(UPLOAD_DIR + fileName);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        long received = 0;
                        byte[] buffer = new byte[8192];
                        int len;
                        while (received < size && (len = in.read(buffer, 0, (int) Math.min(buffer.length, size - received))) != -1) {
                            fos.write(buffer, 0, len);
                            received += len;
                        }
                    }
                } else if ("DOWNLOAD".equals(command)) {
                    String fileName = in.readUTF();
                    File file = new File(UPLOAD_DIR + fileName);
                    if (file.exists()) {
                        out.writeBoolean(true);
                        out.writeLong(file.length());
                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = fis.read(buffer)) != -1) {
                                out.write(buffer, 0, len);
                            }
                        }
                    } else {
                        out.writeBoolean(false);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}