package ui;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏设置对话框 — 按钮、参数、版本信息一应俱全。
 *
 * 用法：
 * <pre>
 *   SettingsDialog dlg = new SettingsDialog(parent, currentTime, currentCoreSize);
 *   dlg.setVisible(true);                         // 阻塞直到关闭
 *   if (dlg.isRestartRequested()) {
 *       int time = dlg.getSelectedTimeSeconds();
 *       int core = dlg.getSelectedCoreSize();
 *       // 应用…
 *   }
 * </pre>
 */
public class SettingsDialog extends JDialog {

    private final JComboBox<String> timeLimitCombo;
    private final JSlider difficultySlider;
    private final JCheckBox soundCheckbox;
    private boolean restartRequested = false;

    // 时间选项与 ComboBox 索引对齐
    private static final int[] TIME_OPTIONS = {60, 90, 120, 180, -1};

    public SettingsDialog(JFrame parent, int currentTime, int currentCoreSize) {
        super(parent, "设置", true);
        setSize(420, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.weightx = 1;

        Font labelFont = new Font("Microsoft YaHei", Font.BOLD, 15);
        Font valueFont = new Font("Microsoft YaHei", Font.PLAIN, 14);

        // ── 1. 时间限制 ──
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel timeLabel = new JLabel("⏱ 时间限制");
        timeLabel.setFont(labelFont);
        mainPanel.add(timeLabel, gbc);

        timeLimitCombo = new JComboBox<>(new String[]{
                "60 秒", "90 秒", "120 秒", "180 秒", "无限制"
        });
        timeLimitCombo.setFont(valueFont);
        // 匹配当前值
        int idx = 2; // default 120
        for (int i = 0; i < TIME_OPTIONS.length; i++) {
            if (TIME_OPTIONS[i] == currentTime) { idx = i; break; }
        }
        timeLimitCombo.setSelectedIndex(idx);
        gbc.gridx = 1;
        mainPanel.add(timeLimitCombo, gbc);

        // ── 2. 难度（棋盘核心大小） ──
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel diffLabel = new JLabel("🎯 难度");
        diffLabel.setFont(labelFont);
        mainPanel.add(diffLabel, gbc);

        JPanel diffPanel = new JPanel(new BorderLayout());
        difficultySlider = new JSlider(JSlider.HORIZONTAL, 3, 6, currentCoreSize);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);
        difficultySlider.setSnapToTicks(true);
        difficultySlider.setFont(valueFont);
        diffPanel.add(difficultySlider, BorderLayout.CENTER);

        JLabel diffHint = new JLabel("越小越简单 · 越大越难（重启后生效）", SwingConstants.CENTER);
        diffHint.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        diffHint.setForeground(Color.GRAY);
        diffPanel.add(diffHint, BorderLayout.SOUTH);

        gbc.gridx = 1;
        mainPanel.add(diffPanel, gbc);

        // ── 3. 音效开关（占位） ──
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel soundLabel = new JLabel("🔊 音效");
        soundLabel.setFont(labelFont);
        mainPanel.add(soundLabel, gbc);

        soundCheckbox = new JCheckBox("开启音效");
        soundCheckbox.setFont(valueFont);
        soundCheckbox.setSelected(true);
        soundCheckbox.setEnabled(false); // 待实现
        gbc.gridx = 1;
        mainPanel.add(soundCheckbox, gbc);

        // ── 4. 重新开始 ──
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        JButton restartBtn = new JButton("🔄 重新开始");
        restartBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        restartBtn.setBackground(new Color(70, 130, 180));
        restartBtn.setForeground(Color.WHITE);
        restartBtn.setFocusPainted(false);
        restartBtn.addActionListener(e -> {
            restartRequested = true;
            dispose();
        });
        mainPanel.add(restartBtn, gbc);

        // ── 5. 版本信息 ──
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 8, 4, 8);
        JLabel versionLabel = new JLabel(
                "<html><center>🀄 连连看 v1.0<br>"
                        + "Built with Java Swing</center></html>",
                SwingConstants.CENTER
        );
        versionLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        versionLabel.setForeground(Color.DARK_GRAY);
        mainPanel.add(versionLabel, gbc);

        // ── 6. 关闭 ──
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 8, 0, 8);
        JButton closeBtn = new JButton("关闭");
        closeBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        closeBtn.addActionListener(e -> dispose());
        mainPanel.add(closeBtn, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    /** 用户是否点了「重新开始」按钮 */
    public boolean isRestartRequested() {
        return restartRequested;
    }

    /** 从 UI 读取时间限制（-1 = 无限制） */
    public int getSelectedTimeSeconds() {
        int idx = timeLimitCombo.getSelectedIndex();
        if (idx < 0 || idx >= TIME_OPTIONS.length) idx = 2;
        return TIME_OPTIONS[idx];
    }

    /** 从 UI 读取核心难度（3~6） */
    public int getSelectedCoreSize() {
        return difficultySlider.getValue();
    }
}
