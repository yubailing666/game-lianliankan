package ui;

import utils.MusicManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * 侧栏猫面板 — 猫从小到大 + 吃鱼动画
 *
 * 每消除一对，调 feedFish() → 鱼飞到猫嘴里 → 猫长大
 */
public class CatPanel extends JPanel implements ActionListener {

    // 猫成长阶段 (emoji, 名字, 所需鱼数)
    private static final String[][] CAT_STAGES = {
        {"\uD83D\uDC31", "小奶猫",     "0"},
        {"\uD83D\uDC31", "小野猫",     "3"},
        {"\uD83D\uDC08", "家猫",       "6"},
        {"\uD83D\uDC08", "大猫",       "10"},
        {"\uD83D\uDC05", "虎斑猫",     "15"},
        {"\uD83D\uDC05", "山猫",       "21"},
        {"\uD83D\uDC3F", "黑豹",       "28"},
        {"\uD83D\uDC3F", "雪豹",       "36"},
        {"\uD83E\uDD81", "小狮子",     "45"},
        {"\uD83E\uDD81", "万兽之王",   "55"},
    };

    private static final Font CAT_FONT = new Font("Segoe UI", Font.PLAIN, 60);
    private static final Font NAME_FONT = new Font("Microsoft YaHei", Font.BOLD, 14);
    private static final Font STAT_FONT = new Font("Microsoft YaHei", Font.PLAIN, 12);

    private Timer timer;
    private int fishCount;
    private List<FlyingFish> flyingFish = new ArrayList<>();
    private float catBounce;      // 猫吃完鱼弹跳
    private int bounceTimer;
    private String yumText;
    private int yumTimer;

    public CatPanel() {
        setPreferredSize(new Dimension(200, 0));
        setBackground(new Color(0x4a3d2e));
        setOpaque(true);
        timer = new Timer(16, this);
        timer.start();
    }

    /** 外部调：消除一对就喂一条鱼 */
    public void feedFish() {
        fishCount++;
        // 鱼从右边飞进来
        flyingFish.add(new FlyingFish());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 鱼飞行
        List<FlyingFish> done = new ArrayList<>();
        for (FlyingFish ff : flyingFish) {
            ff.tick();
            if (ff.done) done.add(ff);
        }
        flyingFish.removeAll(done);

        // 有鱼抵达 → 触发猫弹跳
        for (FlyingFish ff : done) {
            catBounce = 1.3f;
            bounceTimer = 12;
            yumText = "\uD83D\uDC4D +1";
            yumTimer = 30;
        }

        if (bounceTimer > 0) {
            bounceTimer--;
            catBounce = 1.0f + 0.3f * (bounceTimer / 12f);
        }

        if (yumTimer > 0) yumTimer--;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // 背景渐变
        g2.setPaint(new GradientPaint(0, 0, new Color(0x5c4a3a), 0, h, new Color(0x3d2e1e)));
        g2.fillRect(0, 0, w, h);

        // 左边界装饰线
        g2.setColor(new Color(0x7a6a55, 150));
        g2.fillRect(0, 0, 3, h);

        // ── 当前阶段 ──
        int stage = getStage();
        String emoji = CAT_STAGES[stage][0];
        String name = CAT_STAGES[stage][1];
        int nextFish = Integer.parseInt(CAT_STAGES[Math.min(stage + 1, CAT_STAGES.length - 1)][2]);

        // 猫（带弹跳缩放）
        AffineTransform saved = g2.getTransform();
        float sc = catBounce;
        g2.translate(w / 2, h / 3);
        g2.scale(sc, sc);
        g2.setFont(CAT_FONT);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(emoji, -fm.stringWidth(emoji) / 2, fm.getAscent() / 2);
        g2.setTransform(saved);

        // 名字
        g2.setFont(NAME_FONT);
        g2.setColor(new Color(0xf5e6c8));
        fm = g2.getFontMetrics();
        g2.drawString(name, (w - fm.stringWidth(name)) / 2, h / 2);

        // 鱼数 / 进度条
        int barW = w - 30;
        int barH = 10;
        int barX = 15;
        int barY = h / 2 + 15;
        int curLevelFish = fishCount % 3;
        g2.setColor(new Color(0x4a3d2e));
        g2.fillRoundRect(barX, barY, barW, barH, 5, 5);
        g2.setColor(new Color(0xe8c87a));
        int fillW = (int) (barW * Math.min(1.0, (double)(fishCount - getStageStartFish()) / (nextFish - getStageStartFish())));
        if (stage < CAT_STAGES.length - 1) {
            g2.fillRoundRect(barX, barY, Math.max(fillW, 0), barH, 5, 5);
        } else {
            g2.fillRoundRect(barX, barY, barW, barH, 5, 5); // max level, full bar
        }

        // 鱼计数
        g2.setFont(STAT_FONT);
        g2.setColor(new Color(0xc4b091));
        String fishLabel = "\uD83D\uDC1F " + fishCount + " 条";
        g2.drawString(fishLabel, (w - g2.getFontMetrics().stringWidth(fishLabel)) / 2, barY + barH + 20);

        // 阶段提示
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(0x8a7a65));
        String lv = "Lv." + (stage + 1) + " / " + CAT_STAGES.length;
        g2.drawString(lv, (w - g2.getFontMetrics().stringWidth(lv)) / 2, h - 15);

        // ── 飞鱼 ──
        for (FlyingFish ff : flyingFish) {
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            g2.drawString("\uD83D\uDC1F", (int) ff.x, (int) ff.y);
        }

        // ── Yum! 文字 ──
        if (yumTimer > 0) {
            float alpha = Math.min(1f, yumTimer / 15f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(new Color(0xe8c87a));
            g2.drawString(yumText, w / 2 - 15, h / 2 - 50);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // 底部装饰
        g2.setColor(new Color(0x5c4a3a, 100));
        g2.fillRect(0, h - 3, w, 3);
    }

    private int getStage() {
        int total = 0;
        for (int i = 0; i < CAT_STAGES.length; i++) {
            int need = Integer.parseInt(CAT_STAGES[i][2]);
            if (fishCount < need) return i > 0 ? i - 1 : 0;
            total = need;
        }
        return CAT_STAGES.length - 1;
    }

    private int getStageStartFish() {
        int stage = getStage();
        return Integer.parseInt(CAT_STAGES[stage][2]);
    }

    // ── 飞鱼内部类 ──
    private class FlyingFish {
        float x, y;
        float targetX, targetY;
        float progress;
        boolean done;

        FlyingFish() {
            x = -40;
            y = (float) (100 + Math.random() * 200);
            targetX = getWidth() / 2f;
            targetY = getHeight() / 3f;
        }

        void tick() {
            progress += 0.04f;
            if (progress >= 1) {
                done = true;
                return;
            }
            // 缓入缓出
            float t = progress < 0.5f
                ? 2 * progress * progress
                : 1 - (float) Math.pow(-2 * progress + 2, 2) / 2;
            x = -40 + (targetX + 40) * t;
            y = y + (targetY - y) * 0.06f;  // 追踪
            // 加一点弧线
            y -= (float) Math.sin(progress * Math.PI) * 30;
        }
    }
}
