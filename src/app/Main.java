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
            Account.setSize(200,40);
            Account.setLocation(100,30);

            JTextField Password = new JTextField();
            Password.setText("请输入密码");
            Password.setSize(200,40);
            Password.setLocation(100,90);

            JButton login = new JButton("登录");
            login.setLocation(100,150);
            login.setSize(100,40);

            JButton register = new JButton("注册");
            register.setLocation(200,150);
            register.setSize(100,40);

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

