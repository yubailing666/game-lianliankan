package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 侧栏猫面板 — 消除棋子喂鱼 → 猫长大
 *
 * 等级: 🐱(0) → 🐈(6) → 🐅(15) → 🐱‍👤(28) → 🦁(45+)
 */
public class CatPanel extends JPanel implements ActionListener {

    private static final String[][] STAGES = {
        {"\uD83D\uDC31", "小奶猫"},
        {"\uD83D\uDC31", "小野猫"},
        {"\uD83D\uDC08", "家猫"},
        {"\uD83D\uDC08", "大猫"},
        {"\uD83D\uDC05", "虎斑"},
        {"\uD83D\uDC05", "山猫"},
        {"\uD83D\uDC3F", "黑豹"},
        {"\uD83D\uDC3F", "雪豹"},
        {"\uD83E\uDD81", "狮子"},
        {"\uD83E\uDD81", "狮王"},
    };
    private static final int[] FISH_NEED = {0,3,6,10,15,21,28,36,45,55};

    private static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 56);
    private static final Font NAME_FONT = new Font("Microsoft YaHei", Font.BOLD, 13);
    private static final Font TXT_FONT = new Font("Microsoft YaHei", Font.PLAIN, 11);

    private Timer timer;
    private int fishCount;
    private List<FlyFish> fishes = new ArrayList<>();
    private float bounce;      // 猫吃完鱼弹跳
    private int bounceTmr;
    private String yumText;
    private int yumTmr;

    public CatPanel() {
        setBackground(new Color(0x4a3d2e));
        setOpaque(true);
        timer = new Timer(16, this);
        timer.start();
    }

    public void feedFish() {
        fishCount++;
        fishes.add(new FlyFish());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<FlyFish> done = new ArrayList<>();
        for (FlyFish f : fishes) {
            f.tick();
            if (f.done) done.add(f);
        }
        fishes.removeAll(done);

        if (!done.isEmpty()) {
            bounce = 1.25f;
            bounceTmr = 10;
            yumText = "Yum! +1";
            yumTmr = 25;
        }
        if (bounceTmr > 0) { bounceTmr--; bounce = 1f + 0.25f * (bounceTmr / 10f); }
        if (yumTmr > 0) yumTmr--;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        // 深色渐变背景
        g2.setPaint(new GradientPaint(0,0,new Color(0x5c4a3a), 0,h,new Color(0x3d2e1e)));
        g2.fillRect(0,0,w,h);
        // 左边线
        g2.setColor(new Color(122,106,85,150));
        g2.fillRect(0,0,2,h);

        // ── 猫 ──
        int stage = getStage();
        String emoji = STAGES[stage][0];
        String name = STAGES[stage][1];

        AffineTransform save = g2.getTransform();
        g2.translate(w/2f, h/4f);
        g2.scale(bounce, bounce);
        g2.setFont(EMOJI_FONT);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(0xf5e6c8));
        g2.drawString(emoji, -fm.stringWidth(emoji)/2f, fm.getAscent()/2f);
        g2.setTransform(save);

        // 名字
        g2.setFont(NAME_FONT);
        g2.setColor(new Color(0xf5e6c8));
        fm = g2.getFontMetrics();
        g2.drawString(name, (w - fm.stringWidth(name))/2f, h/2f);

        // ── 鱼数和等级 ──
        g2.setFont(TXT_FONT);
        g2.setColor(new Color(0xc4b091));
        String fishTxt = "\uD83D\uDC1F " + fishCount + " 条";
        fm = g2.getFontMetrics();
        g2.drawString(fishTxt, (w - fm.stringWidth(fishTxt))/2f, h/2f + 30);

        // 进度条
        int barW = w - 30;
        int barH = 8;
        int bx = 15, by = h/2 + 42;
        g2.setColor(new Color(0x3d2e1e));
        g2.fillRoundRect(bx, by, barW, barH, 4, 4);
        if (stage < STAGES.length - 1) {
            int has = fishCount - FISH_NEED[stage];
            int need = FISH_NEED[stage+1] - FISH_NEED[stage];
            int fill = (int)(barW * Math.min(1, (double)has / need));
            g2.setColor(new Color(0xe8c87a));
            g2.fillRoundRect(bx, by, fill, barH, 4, 4);
        } else {
            g2.setColor(new Color(0xe8c87a));
            g2.fillRoundRect(bx, by, barW, barH, 4, 4);
        }

        // 等级标签
        String lv = "Lv." + (stage + 1);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(0x8a7a65));
        fm = g2.getFontMetrics();
        g2.drawString(lv, (w - fm.stringWidth(lv))/2f, h - 10);

        // ── 飞鱼 ──
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        for (FlyFish f : fishes) {
            g2.drawString("\uD83D\uDC1F", f.x, f.y);
        }

        // ── Yum文字 ──
        if (yumTmr > 0) {
            float a = Math.min(1f, yumTmr / 15f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.setColor(new Color(0xe8c87a));
            fm = g2.getFontMetrics();
            g2.drawString(yumText, (w - fm.stringWidth(yumText))/2f, h/2f - 45);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private int getStage() {
        for (int i = STAGES.length - 1; i >= 0; i--) {
            if (fishCount >= FISH_NEED[i]) return i;
        }
        return 0;
    }

    // ── 飞鱼 ──
    private class FlyFish {
        float x, y, p;
        boolean done;
        float tx, ty;
        FlyFish() {
            x = -30; y = 80 + (float)(Math.random() * 120);
            tx = getWidth() / 2f;
            ty = getHeight() / 4f;
        }
        void tick() {
            if (tx == 0) { done = true; return; }
            p += 0.05f;
            if (p >= 1) { done = true; return; }
            float t = p < 0.5 ? 2*p*p : 1 - (float)Math.pow(-2*p+2,2)/2;
            x = -30 + (tx + 30) * t;
            y -= 2.5f;
            y += (float)Math.sin(p * Math.PI) * 20;
        }
    }
}
