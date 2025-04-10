import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class FileCopyGUI extends JFrame {
    private JProgressBar progressBar = new JProgressBar(0, 100);
    private JButton copyButton = new JButton("复制文件");

    public FileCopyGUI() {
        setTitle("文件复制器");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        copyButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("选择源文件");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File source = chooser.getSelectedFile();
                chooser.setDialogTitle("选择目标文件");
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File target = chooser.getSelectedFile();
                    copyFile(source.toPath(), target.toPath());
                }
            }
        });

        add(copyButton, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
    }

    private void copyFile(Path source, Path target) {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                long totalBytes = Files.size(source);
                try (InputStream in = Files.newInputStream(source);
                        OutputStream out = Files.newOutputStream(target)) {
                    byte[] buffer = new byte[4096];
                    long copied = 0;
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                        copied += len;
                        int progress = (int) (copied * 100 / totalBytes);
                        setProgress(progress);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(FileCopyGUI.this, "复制完成！");
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileCopyGUI().setVisible(true));
    }
}
