package com.liaoyu.process;

import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.liaou.po.FileItem;

/**
 * @author  liaoyu
 * @created Apr 28, 2015
 */
public class FileChooser implements ActionListener {

    private JFrame frame = new JFrame("Parse External File");
    private JTabbedPane panel = new JTabbedPane();
    private Container container = new Container();
    private JLabel label = new JLabel("File Path : ");
    private JTextField text = new JTextField();
    private JButton chooseBtn = new JButton("Choose");
    private JFileChooser jfc = new JFileChooser();
    private JButton generateBtn = new JButton("Parse");
    private JLabel fileTypeLabel = new JLabel("Extfile Name:");
    private JComboBox<FileItem> jSelect = new JComboBox<FileItem>();
    private List<FileItem> fileList = new ArrayList<FileItem>();

    private File file = null;

    public FileChooser() {
        jfc.setCurrentDirectory(new File("d://"));
        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));
        frame.setSize(400, 260);
        frame.setLayout(null);

        fileList = ResourceFactory.getSington().getFileItemList();
        for (FileItem item : fileList) {
            jSelect.addItem(item);
        }

        fileTypeLabel.setBounds(10, 20, 90, 30);
        jSelect.setBounds(100, 20, 250, 30);
        label.setBounds(10, 75, 90, 30);
        text.setBounds(100, 75, 150, 30);
        chooseBtn.setBounds(270, 75, 80, 30);
        generateBtn.setBounds(10, 120, 80, 30);

        jSelect.addActionListener(this);
        chooseBtn.addActionListener(this);
        generateBtn.addActionListener(this);

        container.add(fileTypeLabel);
        container.add(jSelect);
        container.add(label);
        container.add(text);
        container.add(chooseBtn);
        container.add(generateBtn);

        panel.add(container);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        new FileChooser();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(chooseBtn)) {
            jfc.setFileSelectionMode(0);
            int state = jfc.showOpenDialog(null);
            if (state == 1) {
                return;
            } else {
                file = jfc.getSelectedFile();
                text.setText(file.getAbsolutePath());
            }
        }

        if (e.getSource().equals(generateBtn)) {

            FileItem fileItem = (FileItem) jSelect.getSelectedItem();
            String targetFileName = fileItem.getKey() + ".properties";

            if (file == null) {
                JOptionPane.showMessageDialog(null, "Please choose a file to process.");
            } else {
                GenerateHtml.process(file, targetFileName);
                JOptionPane.showMessageDialog(null, "Generate html file successfully.");
            }

        }

    }

}
