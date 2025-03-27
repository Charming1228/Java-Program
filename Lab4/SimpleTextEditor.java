import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SimpleTextEditor {
    private JFrame frame;
    private JTextArea textArea;

    public SimpleTextEditor() {
        frame = new JFrame("简单文本编辑器");
        textArea = new JTextArea();

        // 创建按钮
        JButton saveButton = new JButton("保存");
        JButton exitButton = new JButton("退出");

        // 监听保存按钮
        saveButton.addActionListener(e -> saveFile());

        // 监听退出按钮
        exitButton.addActionListener(e -> System.exit(0));

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenuItem saveMenuItem = new JMenuItem("保存");
        JMenuItem exitMenuItem = new JMenuItem("退出");

        saveMenuItem.addActionListener(e -> saveFile());
        exitMenuItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(exitButton);

        // 设置布局
        frame.setJMenuBar(menuBar);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void saveFile() {
        String fileName = JOptionPane.showInputDialog(frame, "请输入文件名:", "保存", JOptionPane.PLAIN_MESSAGE);
        if (fileName != null && !fileName.trim().isEmpty()) {
            File file = new File(fileName);
            if (file.exists()) {
                int choice = JOptionPane.showConfirmDialog(frame, "文件已存在，是否覆盖？", "确认", JOptionPane.YES_NO_OPTION);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textArea.getText());
                JOptionPane.showMessageDialog(frame, "文件已保存", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "保存失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleTextEditor::new);
    }
}
