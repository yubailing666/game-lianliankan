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
    Timer timer;
    int totalSeconds;
    int countSeconds;
    int secondsUsed;
    int minutesUsed;
    int seconds;
    int minutes;

    public StatusPanel(int offSetX, int offSetY, int width, int height) {
        this.setBounds(offSetX, offSetY, width, height);

        Font cjkFont = new Font("Microsoft YaHei", Font.BOLD, 18);
        Font numFont = new Font("Arial", Font.BOLD, 48);

        scoreLabel = new JLabel("分数", SwingConstants.CENTER);
        scoreLabel.setFont(cjkFont);
        scoreValue = new JLabel("0", SwingConstants.CENTER);
        scoreValue.setFont(numFont);

        statusLabel = new JLabel("剩余时间", SwingConstants.CENTER);
        statusLabel.setFont(cjkFont);
        timeLabel = new JLabel("00:00", SwingConstants.CENTER);
        timeLabel.setFont(numFont);

        timeuseLabel = new JLabel("已经用时", SwingConstants.CENTER);
        timeuseLabel.setFont(cjkFont);
        timecountLabel = new JLabel("00:00", SwingConstants.CENTER);
        timecountLabel.setFont(numFont);

        totalSeconds = 120;
        countSeconds = 0;
        timer = new Timer(1000, e -> {
            totalSeconds--;
            countSeconds++;
            seconds = totalSeconds % 60;
            minutes = totalSeconds / 60;
            secondsUsed = countSeconds % 60;
            minutesUsed = countSeconds / 60;
            timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
            timecountLabel.setText(String.format("%02d:%02d", minutesUsed, secondsUsed));
        });
        // timer.start(); // 改为按 start 按钮启动

        setLayout(new GridLayout(1, 3));

        // GridBagLayout

        // 左：分数
        JPanel left = new JPanel(new GridBagLayout());
        JPanel leftInner = new JPanel(new GridLayout(2, 1));
        leftInner.add(scoreLabel);
        leftInner.add(scoreValue);
        left.add(leftInner);

        // 中：剩余时间
        JPanel middle = new JPanel(new GridBagLayout());
        JPanel middleInner = new JPanel(new GridLayout(2, 1));
        middleInner.add(statusLabel);
        middleInner.add(timeLabel);
        middle.add(middleInner);

        // 右：已经用时
        JPanel right = new JPanel(new GridBagLayout());
        JPanel rightInner = new JPanel(new GridLayout(2, 1));
        rightInner.add(timeuseLabel);
        rightInner.add(timecountLabel);
        right.add(rightInner);

        this.add(left);
        this.add(middle);
        this.add(right);
    }
    public void startGame() {
        statusLabel.setText("进行中");
        timer.start();
    }
}
