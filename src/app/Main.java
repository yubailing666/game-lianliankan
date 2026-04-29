package app;

import model.Cell;
import model.GameBoard;
import model.Position;
import ui.BoardPanel;
import ui.GameFrame;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String USER_FILE = System.getProperty("user.dir") + File.separator + "user.txt";
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Login");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(400,250);
            loginFrame.setLayout(null);


            JTextField Account = new JTextField();
            Account.setText("请输入账号");
            Account.setSize(170,35);
            Account.setLocation(110,80);
            Account.setOpaque(false);

            JTextField Password = new JTextField();
            Password.setText("请输入密码");
            Password.setSize(170,35);
            Password.setLocation(110,115);
            Password.setOpaque(false);

            JButton login = new JButton("登录");
            login.setLocation(110,150);
            login.setSize(80,30);
            login.setOpaque(false);
            login.setContentAreaFilled(false);
            login.setBorderPainted(true);

            JButton register = new JButton("注册");
            register.setLocation(200,150);
            register.setSize(80,30);
            register.setOpaque(false);
            register.setContentAreaFilled(false);
            register.setBorderPainted(true);

            register.addActionListener(e -> {
                String username = Account.getText();
                String password = Password.getText();
                if (username.equals("请输入账号") || username.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "请输入账号！");
                    return;
                }
                if (password.equals("请输入密码") || password.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "请输入密码！");
                    return;
                }
                if(checkUserExists(username)){
                    JOptionPane.showMessageDialog(loginFrame, "用户已存在！");
                    return;
                }
                if(writeUserToFile(username,password)){
                    JOptionPane.showMessageDialog(loginFrame, "注册成功！");
                    Account.setText("请输入账号");
                    Password.setText("请输入密码");
                }else{
                    JOptionPane.showMessageDialog(loginFrame, "注册失败！");
                }

            });
            loginFrame.add(login);
            loginFrame.add(register);
            loginFrame.add(Account);
            loginFrame.add(Password);
            loginFrame.setVisible(true);
            String bgPath = "game-lianliankan" + File.separator + "resource" + File.separator + "background.png";
            File bgFile = new File(bgPath);
            if (bgFile.exists()) {
                JLabel backgroundLabel = new JLabel(new ImageIcon(bgPath));
                backgroundLabel.setSize(400, 250);
                backgroundLabel.setLocation(-10, -6);
                loginFrame.add(backgroundLabel);
            } else {
                System.err.println("警告: 背景图片不存在: " + bgFile.getAbsolutePath());
            }
            login.addActionListener(e -> {
                String username = Account.getText();
                String password = Password.getText();
                if(validateUser(username, password)) {
                    loginFrame.dispose();
                    GameFrame frame = new GameFrame("连连看", 800, 1000);
                    frame.repaint();
                }else{
                    JOptionPane.showMessageDialog(loginFrame, "账号或密码错误！");
                }
            });
        });
    }
    private static boolean checkUserExists(String username){
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))){
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split(",");
                if(parts.length == 2 && parts[0].equals(username)){
                    return true;
                }
            }
        } catch (IOException e){

        }
        return false;
    }
    private static boolean writeUserToFile(String name, String password){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))){
            writer.write(name + "," + password);
            writer.newLine();
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static boolean validateUser(String username, String password){
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))){
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split(",");
                if(parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)){
                    return true;
                }
            }
        } catch (IOException e) {

        }
        return false;
    }
}
