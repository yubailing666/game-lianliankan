package ui;

import javax.swing.*;
import java.awt.*;

/**
 * 底部控制栏 — 四个操作按钮
 *
 *   "> START"  — 启动游戏（开启计时器 + 激活棋盘交互）
 *   "RESTART"  — 重新生成棋盘并重置状态
 *   "SETTINGS" — 打开设置对话框（可改时间和难度后重启）
 *   "RANK"     — 打开排行榜弹窗
 */
public class ControlPanel extends JPanel {

    // ── 布局参数 ──
    int offSetX;
    int offSetY;
    int width;
    int height;

    // ── UI 组件 ──
    StatusPanel statusPanel;
    JButton startButton;
    JButton restartButton;
    JButton settingsButton;
    JButton leaderBoardBtn;

    // ── 回调 ──
    Runnable onRestart;
    Runnable onLeaderBoard;

    // ── 设置参数（从 SettingsDialog 读回） ──
    int currentTimeLimit = 120;
    int currentCoreSize = 4;

    // ════════════════════════════════════════════════════
    // 回调注册
    // ════════════════════════════════════════════════════

    public void setOnRestart(Runnable callback) {
        this.onRestart = callback;
    }

    public void setOnLeaderBoard(Runnable callback) {
        this.onLeaderBoard = callback;
    }

    // ════════════════════════════════════════════════════
    // 构造
    // ════════════════════════════════════════════════════

    public ControlPanel(StatusPanel statusPanel, BoardPanel boardPanel,
                        int offSetX, int offSetY, int width, int height) {
        setLayout(null);
        setBounds(offSetX, offSetY, width, height);
        setBackground(new Color(0x5c4a3a));
        setOpaque(true);

        this.offSetX = offSetX;
        this.offSetY = offSetY;
        this.width = width;
        this.height = height;
        this.statusPanel = statusPanel;

        // ── 计算四个按钮的居中位置 ──
        int btnWidth = 130;
        int btnHeight = 40;
        int gap = 12;
        int totalW = btnWidth * 4 + gap * 3;
        int x = (width - totalW) / 2;
        int y = (height - btnHeight) / 2;

        // ── START 按钮 ──
        startButton = createStyledButton("> START", new Color(0xe8c87a), new Color(0x4a3d2e));
        startButton.setBounds(x, y, btnWidth, btnHeight);
        add(startButton);
        startButton.addActionListener(e -> {
            statusPanel.startGame();
            boardPanel.startGame();
            boardPanel.refreshPairInfo();
        });

        // ── RESTART 按钮 ──
        restartButton = createStyledButton("RESTART", new Color(0x6b5b45), new Color(0xc4b091));
        restartButton.setBounds(x + btnWidth + gap, y, btnWidth, btnHeight);
        add(restartButton);
        restartButton.addActionListener(e -> {
            if (onRestart != null) onRestart.run();
        });

        // ── SETTINGS 按钮 ──
        settingsButton = createStyledButton("SETTINGS", new Color(0x5c4a3a), new Color(0xa09070));
        settingsButton.setBounds(x + 2 * (btnWidth + gap), y, btnWidth, btnHeight);
        add(settingsButton);
        settingsButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            SettingsDialog dlg = new SettingsDialog(frame, currentTimeLimit, currentCoreSize);
            dlg.setVisible(true);
            if (dlg.isRestartRequested()) {
                currentTimeLimit = dlg.getSelectedTimeSeconds();
                currentCoreSize = dlg.getSelectedCoreSize();
                if (onRestart != null) onRestart.run();
            }
        });

        // ── RANK 按钮 ──
        leaderBoardBtn = createStyledButton("RANK", new Color(0x4a3d2e), new Color(0xe8c87a));
        leaderBoardBtn.setBounds(x + 3 * (btnWidth + gap), y, btnWidth, btnHeight);
        add(leaderBoardBtn);
        leaderBoardBtn.addActionListener(e -> {
            if (onLeaderBoard != null) onLeaderBoard.run();
        });
    }

    // ════════════════════════════════════════════════════
    // 工具
    // ════════════════════════════════════════════════════

    /** 创建统一样式的按钮（纯色背景、无边框、手型光标） */
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
