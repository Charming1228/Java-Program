import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class FileClientGUI extends JFrame {
    private JTextField hostField = new JTextField("localhost", 15);
    private JTextField portField = new JTextField("12345", 5);
    private JButton connectBtn = new JButton("连接");
    private JButton uploadBtn = new JButton("上传");
    private JButton downloadBtn = new JButton("下载");
    private JProgressBar progressBar = new JProgressBar(0, 100);
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public FileClientGUI() {
        setTitle("文件客户端");
        setLayout(new FlowLayout());
        add(new JLabel("主机:"));
        add(hostField);
        add(new JLabel("端口:"));
        add(portField);
        add(connectBtn);
        add(uploadBtn);
        add(downloadBtn);
        add(progressBar);
        uploadBtn.setEnabled(false);
        downloadBtn.setEnabled(false);

        connectBtn.addActionListener(e -> connect());
        uploadBtn.addActionListener(e -> upload());
        downloadBtn.addActionListener(e -> download());
        setSize(400, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void connect() {
        try {
            socket = new Socket(hostField.getText(), Integer.parseInt(portField.getText()));
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            uploadBtn.setEnabled(true);
            downloadBtn.setEnabled(true);
            connectBtn.setEnabled(false);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "连接失败: " + e.getMessage());
        }
    }

    private void upload() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() throws Exception {
                    File file = fc.getSelectedFile();
                    out.writeUTF("UPLOAD");
                    out.writeUTF(file.getName());
                    out.writeLong(file.length());
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        long total = 0;
                        while ((len = fis.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                            total += len;
                            setProgress((int) (total * 100 / file.length()));
                        }
                    }
                    return null;
                }

                @Override
                protected void process(java.util.List<Integer> chunks) {
                    progressBar.setValue(chunks.get(chunks.size() - 1));
                }
            }.execute();
        }
    }

    private void download() {
        String fileName = JOptionPane.showInputDialog(this, "输入文件名:");
        if (fileName != null && !fileName.trim().isEmpty()) {
            new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() throws Exception {
                    out.writeUTF("DOWNLOAD");
                    out.writeUTF(fileName);
                    if (in.readBoolean()) {
                        long size = in.readLong();
                        JFileChooser fc = new JFileChooser();
                        fc.setSelectedFile(new File(fileName));
                        if (fc.showSaveDialog(FileClientGUI.this) == JFileChooser.APPROVE_OPTION) {
                            try (FileOutputStream fos = new FileOutputStream(fc.getSelectedFile())) {
                                byte[] buffer = new byte[8192];
                                int len;
                                long received = 0;
                                while (received < size && (len = in.read(buffer, 0,
                                        (int) Math.min(buffer.length, size - received))) != -1) {
                                    fos.write(buffer, 0, len);
                                    received += len;
                                    setProgress((int) (received * 100 / size));
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(FileClientGUI.this, "文件不存在");
                    }
                    return null;
                }

                @Override
                protected void process(java.util.List<Integer> chunks) {
                    progressBar.setValue(chunks.get(chunks.size() - 1));
                }
            }.execute();
        }
    }

    public static void main(String[] args) {
        new FileClientGUI();
    }
}