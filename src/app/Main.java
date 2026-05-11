package app;

import ui.GameFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameFrame("连连看", 800, 1000);
        });
    }
}
