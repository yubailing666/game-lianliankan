package app;

import ui.GameFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"简单模式", "困难模式"};
            int choice = JOptionPane.showOptionDialog(null,
                    "请选择游戏难度", "连连看",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            boolean isHardMode = (choice == 1);
            new GameFrame("连连看", 800, 1000, isHardMode);
        });
    }
}
