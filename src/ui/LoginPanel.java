package ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * 登录/注册页面 — 从 Main.java 搬过来的
 */
public class LoginPanel extends JPanel {

    private static final String USER_FILE = System.getProperty("user.dir") + File.separator + "user.txt";

    private JTextField accountField;
    private JTextField passwordField;
    private JTextField catNameField;
    private GameFrame parent;

    public LoginPanel(GameFrame parent) {
        this.parent = parent;
        setLayout(null);
        setBackground(new Color(0x5c4a3a));
        setSize(parent.getWidth(), parent.getHeight());

        // 账号输入框
        accountField = new JTextField();
        accountField.setText("请输入账号");
        accountField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (accountField.getText().equals("请输入账号")) {
                    accountField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (accountField.getText().isEmpty()) {
                    accountField.setText("请输入账号");
                }
            }
        });
        accountField.setSize(170, 35);
        accountField.setLocation(110, 100);
        accountField.setOpaque(false);
        add(accountField);

        // 密码输入框
        passwordField = new JTextField();
        passwordField.setText("请输入密码");
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (passwordField.getText().equals("请输入密码")) {
                    passwordField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (passwordField.getText().isEmpty()) {
                    passwordField.setText("请输入密码");
                }
            }
        });
        passwordField.setSize(170, 35);
        passwordField.setLocation(110, 140);
        passwordField.setOpaque(false);
        add(passwordField);

        // 小猫名字
        catNameField = new JTextField("Mimi");
        catNameField.setSize(170, 35);
        catNameField.setLocation(110, 180);
        add(catNameField);

        // 登录按钮
        JButton loginBtn = new JButton("登录");
        loginBtn.setLocation(90, 225);
        loginBtn.setSize(80, 30);
        loginBtn.setOpaque(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(true);
        add(loginBtn);

        // 注册按钮
        JButton registerBtn = new JButton("注册");
        registerBtn.setLocation(180, 225);
        registerBtn.setSize(80, 30);
        registerBtn.setOpaque(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(true);
        add(registerBtn);

        // 注册事件
        registerBtn.addActionListener(e -> {
            String username = accountField.getText();
            String password = passwordField.getText();
            if (username.equals("请输入账号") || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入账号！");
                return;
            }
            if (password.equals("请输入密码") || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入密码！");
                return;
            }
            if (checkUserExists(username)) {
                JOptionPane.showMessageDialog(this, "用户已存在！");
                return;
            }
            if (writeUserToFile(username, password)) {
                JOptionPane.showMessageDialog(this, "注册成功！");
                accountField.setText("请输入账号");
                passwordField.setText("请输入密码");
            } else {
                JOptionPane.showMessageDialog(this, "注册失败！");
            }
        });

        // 登录事件
        loginBtn.addActionListener(e -> {
            String username = accountField.getText();
            String password = passwordField.getText();
            if (validateUser(username, password)) {
                // 登录成功 → 选难度 → 进游戏
                String[] options = {"简单模式", "困难模式"};
                int choice = JOptionPane.showOptionDialog(this,
                        "选择游戏难度", "连连看",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
                boolean isHardMode = (choice == 1);
                parent.startGame(username, isHardMode);
            } else {
                JOptionPane.showMessageDialog(this, "账号或密码错误！");
            }
        });

        // 背景图
        String bgPath = System.getProperty("user.dir") + File.separator + "resource" + File.separator + "background.png";
        File bgFile = new File(bgPath);
        if (!bgFile.exists()) {
            bgPath = "D:" + File.separator + "game-lianliankan" + File.separator + "resource" + File.separator + "background.png";
            bgFile = new File(bgPath);
        }
        if (bgFile.exists()) {
            JLabel bgLabel = new JLabel(new ImageIcon(bgPath));
            bgLabel.setSize(400, 300);
            bgLabel.setLocation(-10, -6);
            add(bgLabel);
        }
    }

    // ── 用户管理──
    private static boolean checkUserExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username)) return true;
            }
        } catch (IOException e) { }
        return false;
    }

    private static boolean writeUserToFile(String name, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(name + "," + password);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean validateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password))
                    return true;
            }
        } catch (IOException e) { }
        return false;
    }
}
