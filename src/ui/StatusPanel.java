package ui;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    JLabel scoreLabel;
    JLabel scoreValue;
    JLabel statusLabel;
    JLabel timeLabel;
    JLabel timeuseLabel;
    JLabel timecountLabel;
    JLabel comboLabel;
    JLabel remainingPairLabel;
    JLabel progressLabel;
    Timer timer;
    Timer comboTimer;
    int totalSeconds;
    int countSeconds;
    int secondsUsed;
    int minutesUsed;
    int seconds;
    int minutes;
    int score;
    int comboCount;
    long lastEliminationTime;
    static final long COMBO_TIMEOUT = 3000;


    public StatusPanel(int offSetX, int offSetY, int width, int height) {
        this.setBounds(offSetX, offSetY, width, height);
        setBackground(new Color(0x5c4a3a));
        setOpaque(true);

        Font cjkFont = new Font("Microsoft YaHei", Font.BOLD, 16);
        Font numFont = new Font("Arial", Font.BOLD, 48);
        Font scoreNumFont = new Font("Arial", Font.BOLD, 28);
        Font comboFont = new Font("Microsoft YaHei", Font.BOLD, 16);

        scoreLabel = new JLabel("分数", SwingConstants.CENTER);
        scoreLabel.setFont(cjkFont);
        scoreLabel.setForeground(new Color(0xa09070));
        scoreValue = new JLabel("0", SwingConstants.CENTER);
        scoreValue.setFont(scoreNumFont);
        scoreValue.setForeground(new Color(0xe8c87a));

        statusLabel = new JLabel("剩余时间", SwingConstants.CENTER);
        statusLabel.setFont(cjkFont);
        statusLabel.setForeground(new Color(0xa09070));
        timeLabel = new JLabel("00:00", SwingConstants.CENTER);
        timeLabel.setFont(numFont);
        timeLabel.setForeground(new Color(0xf5e6c8));

        timeuseLabel = new JLabel("已经用时", SwingConstants.CENTER);
        timeuseLabel.setFont(cjkFont);
        timeuseLabel.setForeground(new Color(0xa09070));
        timecountLabel = new JLabel("00:00", SwingConstants.CENTER);
        timecountLabel.setFont(numFont);
        timecountLabel.setForeground(new Color(0x8a7a65));

        comboLabel = new JLabel("", SwingConstants.CENTER);
        comboLabel.setFont(comboFont);
        comboLabel.setForeground(new Color(255, 69, 0));
        comboLabel.setOpaque(true);
        comboLabel.setBackground(new Color(255, 255, 220));
        comboLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 2),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        comboLabel.setVisible(false);

        totalSeconds = 120;
        countSeconds = 0;
        comboCount = 0;
        lastEliminationTime = 0;

        comboTimer = new Timer((int) COMBO_TIMEOUT, e -> {
            comboLabel.setVisible(false);
            comboCount = 0;
            comboTimer.stop();
        });
        comboTimer.setRepeats(false);

        timer = new Timer(1000, e -> {
            totalSeconds--;
            countSeconds++;
            seconds = totalSeconds % 60;
            minutes = totalSeconds / 60;
            secondsUsed = countSeconds % 60;
            minutesUsed = countSeconds / 60;
            if(totalSeconds==0){
                timer.stop();
                JOptionPane.showMessageDialog(null, "你输了！");
            }
            timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
            timecountLabel.setText(String.format("%02d:%02d", minutesUsed, secondsUsed));
        });

        setLayout(new GridLayout(1, 4));

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(new Color(0x5c4a3a));
        left.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JPanel scoreBox = new JPanel(new BorderLayout());
        scoreBox.setOpaque(false);
        scoreBox.add(scoreLabel, BorderLayout.NORTH);
        scoreBox.add(scoreValue, BorderLayout.CENTER);

        left.add(scoreBox, BorderLayout.CENTER);
        left.add(comboLabel, BorderLayout.SOUTH);

        JPanel middle = new JPanel(new GridBagLayout());
        middle.setBackground(new Color(0x5c4a3a));
        JPanel middleInner = new JPanel(new GridLayout(2, 1));
        middleInner.setBackground(new Color(0x5c4a3a));
        middleInner.add(statusLabel);
        middleInner.add(timeLabel);
        middle.add(middleInner);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(0x5c4a3a));
        JPanel rightInner = new JPanel(new GridLayout(2, 1));
        rightInner.setBackground(new Color(0x5c4a3a));
        rightInner.add(timeuseLabel);
        rightInner.add(timecountLabel);
        right.add(rightInner);
        
        Font pairFont = new Font("Microsoft YaHei",Font.BOLD,18);
        remainingPairLabel = new JLabel("剩余可消除：0对",SwingConstants.CENTER);
        remainingPairLabel.setFont(pairFont);
        remainingPairLabel.setForeground(new Color(0xf5e6c8));
        progressLabel = new JLabel("关卡进度：0%",SwingConstants.CENTER);
        progressLabel.setFont(pairFont);
        progressLabel.setForeground(new Color(0xf5e6c8));
        JPanel pairPanel = new JPanel(new GridBagLayout());
        pairPanel.setBackground(new Color(0x5c4a3a));
        JPanel pairInner = new JPanel(new GridLayout(2,1));
        pairInner.setBackground(new Color(0x5c4a3a));
        pairInner.add(remainingPairLabel);
        pairInner.add(progressLabel);
        pairPanel.add(pairInner);

        this.add(left);
        this.add(middle);
        this.add(pairPanel);
        this.add(right);
    }
    public void updatePairInfo(int remainingPairs, int clearedPairs, int totalPairs){
        remainingPairLabel.setText("剩余可消除：" + remainingPairs + "对");
        int progress = clearedPairs * 100 / totalPairs;
        progressLabel.setText("关卡进度: "+ progress + "%");
    }
    
    public void startGame() {
        statusLabel.setText("进行中");
        timer.start();
    }

    public void addScore(int points) {
        long currTime = System.currentTimeMillis();
        
        if (lastEliminationTime == 0 || (currTime - lastEliminationTime) > COMBO_TIMEOUT) {
            comboCount = 1;
        } else {
            comboCount++;
        }
        
        lastEliminationTime = currTime;
        
        int bonusPoints = points;
        if (comboCount >= 3) {
            bonusPoints = points * (comboCount - 1);
            showComboMessage(comboCount, bonusPoints);
        }
        
        score += bonusPoints;
        scoreValue.setText(String.valueOf(score));
        
        if (comboCount >= 3) {
            comboTimer.restart();
        }
    }

    private void showComboMessage(int comboCount, int bonusPoints) {
        String message = String.format(">>>COMBO x%d! +%d分<<<", comboCount, bonusPoints);
        
        comboLabel.setText(message);
        comboLabel.setVisible(true);
        
        // 强制刷新整个面板
        this.revalidate();
        this.repaint();

        // 启动闪烁动画
        AnimationThread anim = new AnimationThread(comboLabel);
        anim.start();
    }

    public void winGame() {
        timer.stop();
        statusLabel.setText("你赢了！");
    }

    public void resetGame() {
        comboCount = 0;
        lastEliminationTime = 0;
        comboLabel.setVisible(false);
        comboTimer.stop();
        totalSeconds = 120;
        countSeconds = 0;
        score = 0;
        scoreValue.setText("0");
        timeLabel.setText("02:00");
        timecountLabel.setText("00:00");
        statusLabel.setText("按 Start 开始");
        timer.stop();
        remainingPairLabel.setText("剩余可消除: 0对");
        progressLabel.setText("关卡进度: 0%");
     }
    
    static class AnimationThread extends Thread {
        JLabel label;
        public AnimationThread(JLabel label) {
            this.label = label;
        }
        @Override
        public void run(){
            try{
                Color[] rainbowColors = {
                    Color.RED,
                    new Color(255, 127, 0),
                    Color.YELLOW,
                    Color.GREEN,
                    Color.CYAN,
                    Color.BLUE,
                    new Color(148, 0, 211)
                };
                
                for (int cycle = 0; cycle < 3; cycle++) {
                    for (int i = 0; i < rainbowColors.length; i++) {
                        final Color c = rainbowColors[i];
                        SwingUtilities.invokeLater(() -> label.setForeground(c));
                        Thread.sleep(100);
                    }
                }
                SwingUtilities.invokeLater(() -> label.setForeground(new Color(255, 165, 0)));
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
