package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 侧栏像素猫 — 消除棋子喂鱼 → 橘猫从小胖到大
 *
 * 三个阶段: 🐱 → 🐈 → 🐈‍⬛（但全是同一只橘猫，只是变大变胖）
 * 使用像素手绘 + 呼吸动画
 */
public class CatPanel extends JPanel implements ActionListener {

    // ─── 颜色 ───
    private static final Color[] PAL = {
        null,                        // 0: 透明
        new Color(0x3A2210),         // 1: 轮廓
        new Color(0xE09145),         // 2: 橘色毛
        new Color(0xC07530),         // 3: 深色虎纹
        new Color(0xF5D6A8),         // 4: 肚皮/脸浅色
        new Color(0xE8A0A0),         // 5: 鼻子粉
        new Color(0x1A0A00),         // 6: 眼睛细缝
        new Color(0xFFF8EC),         // 7: 高光
        new Color(0xF0B8B8),         // 8: 耳内粉
    };

    // ─── 像素数据 ───
    // 格式: int[阶段数][帧数][行][列]

    // 幼年 16×16
    private static final int[][][] KT = {{
        // F0 吐气
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0},
        {0,0,0,0,1,8,2,1,1,2,8,1,0,0,0,0},
        {0,0,0,1,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,0,1,2,4,4,1,1,4,4,2,1,0,0,0},
        {0,0,1,2,2,4,5,4,4,5,4,2,2,1,0,0},
        {0,0,1,2,2,2,4,4,4,4,2,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,2,2,3,2,2,2,2,2,3,2,2,2,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,0,1,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,0,0,1,1,2,2,2,2,1,1,0,0,0,0},
        {0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        // F1 吸气（略鼓）
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0},
        {0,0,0,0,1,8,2,1,1,2,8,1,0,0,0,0},
        {0,0,0,1,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,0,1,2,4,4,1,1,4,4,2,1,0,0,0},
        {0,0,1,2,2,4,5,4,4,5,4,2,2,1,0,0},
        {0,0,1,2,2,2,4,4,4,4,2,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,2,2,3,2,2,2,2,2,3,2,2,2,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,0,1,1,2,2,2,2,2,2,1,1,0,0,0},
        {0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    }};

    // 成年 20×20
    private static final int[][][] AD = {{
        // F0
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0},
        {0,0,0,0,0,1,8,2,1,1,1,1,2,8,1,0,0,0,0,0},
        {0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0},
        {0,0,0,0,1,2,4,4,1,1,1,1,4,4,2,1,0,0,0,0},
        {0,0,0,1,2,2,4,5,4,4,4,4,5,4,2,2,1,0,0,0},
        {0,0,0,1,2,2,2,4,4,4,4,4,4,2,2,2,1,0,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,3,2,2,2,2,2,2,2,2,3,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,0,1,1,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0},
        {0,0,0,0,1,1,2,2,2,2,2,2,2,2,1,1,0,0,0,0},
        {0,0,0,0,0,0,1,1,1,2,2,1,1,1,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        // F1
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0},
        {0,0,0,0,0,1,8,2,1,1,1,1,2,8,1,0,0,0,0,0},
        {0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0},
        {0,0,0,0,1,2,4,4,1,1,1,1,4,4,2,1,0,0,0,0},
        {0,0,0,1,2,2,4,5,4,4,4,4,5,4,2,2,1,0,0,0},
        {0,0,0,1,2,2,2,4,4,4,4,4,4,2,2,2,1,0,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,3,2,2,2,2,2,2,2,2,3,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,0,1,1,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0},
        {0,0,0,0,1,1,2,2,2,2,2,2,2,2,1,1,0,0,0,0},
        {0,0,0,0,0,0,1,1,1,2,2,1,1,1,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    }};

    // 胖橘 28×24 — 翻肚皮晒太阳
    private static final int[][][] CH = {{
        // F0
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,1,1,2,2,2,2,1,1,1,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,1,1,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0,0,0,0,0},
        {0,0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0,0,0},
        {0,0,0,0,0,1,2,2,2,2,4,4,4,4,4,4,2,2,2,2,1,0,0,0,0,0,0},
        {0,0,0,0,1,2,2,2,2,4,4,5,4,4,5,4,4,2,2,2,2,1,0,0,0,0,0},
        {0,0,0,0,1,2,2,2,2,2,4,4,4,4,4,4,2,2,2,2,2,1,0,0,0,0,0},
        {0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0},
        {0,0,0,1,2,2,3,2,2,2,2,2,2,2,2,2,2,2,2,3,2,2,1,0,0,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0},
        {0,0,0,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0,0},
        {0,0,0,0,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0,0,0},
        {0,0,0,0,0,1,1,1,2,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,1,1,1,1,2,2,2,2,1,1,1,1,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        // F1
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,1,1,1,2,2,2,2,1,1,1,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,1,1,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0,0,0,0,0},
        {0,0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0,0,0},
        {0,0,0,0,0,1,2,2,2,2,4,4,4,4,4,4,2,2,2,2,1,0,0,0,0,0,0},
        {0,0,0,0,1,2,2,2,2,4,4,5,4,4,5,4,4,2,2,2,2,1,0,0,0,0,0},
        {0,0,0,0,1,2,2,2,2,2,4,4,4,4,4,4,2,2,2,2,2,1,0,0,0,0,0},
        {0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0},
        {0,0,0,1,2,2,3,2,2,2,2,2,2,2,2,2,2,2,2,3,2,2,1,0,0,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0},
        {0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0},
        {0,0,0,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0,0},
        {0,0,0,0,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0,0,0},
        {0,0,0,0,0,1,1,1,2,2,2,2,2,2,2,2,2,2,2,1,1,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,1,1,1,1,2,2,2,2,1,1,1,1,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    }};

    // ─── 阶段配置 ───
    private static final String[] STAGE_NAMES = {"小奶猫", "大橘", "胖橘"};
    private static final int[] FISH_NEED = {0, 5, 15};
    private static final int[][][][] STAGE_DATA = {KT, AD, CH};
    private static final int[] STAGE_COLS = {16, 20, 28};
    private static final int[] STAGE_ROWS = {16, 20, 24};

    // ─── 实例 ───
    private Timer timer;
    private int fishCount;
    private int frameIndex;
    private long lastFrameTime;
    private static final int FRAME_DELAY = 700;

    // 飞鱼特效
    private List<FlyFish> fishes = new ArrayList<>();
    private String yumText;
    private int yumTimer;

    // 缩放因子：目标显示大小
    private static final int PIXEL_SCALE = 8;
    private static final int CAT_DISPLAY_W = 160;  // 猫的显示宽度上限
    private static final int CAT_DISPLAY_H = 160;

    public CatPanel() {
        setBackground(new Color(0x4a3d2e));
        setOpaque(true);
        lastFrameTime = System.currentTimeMillis();
        timer = new Timer(80, this);  // ～80ms tick
        timer.start();
    }

    public void feedFish() {
        fishCount++;
        fishes.add(new FlyFish());
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // 帧切换
        long now = System.currentTimeMillis();
        if (now - lastFrameTime >= FRAME_DELAY) {
            lastFrameTime = now;
            frameIndex = (frameIndex + 1) % 2;
        }

        // 飞鱼
        List<FlyFish> done = new ArrayList<>();
        for (FlyFish f : fishes) {
            f.tick();
            if (f.done) done.add(f);
        }
        fishes.removeAll(done);
        if (!done.isEmpty()) {
            yumText = "Yum! +1";
            yumTimer = 25;
        }
        if (yumTimer > 0) yumTimer--;

        repaint();
    }

    private int getStage() {
        for (int i = STAGE_NAMES.length - 1; i >= 0; i--) {
            if (fishCount >= FISH_NEED[i]) return i;
        }
        return 0;
    }

    private BufferedImage renderCat(int stage, int frame) {
        int[][][] data = STAGE_DATA[stage];
        if (frame >= data.length) frame = 0;
        int[][] grid = data[frame];
        int rows = STAGE_ROWS[stage];
        int cols = STAGE_COLS[stage];

        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int idx = grid[y][x];
                if (idx == 0) continue;
                Color c = PAL[idx];
                if (c == null) continue;
                img.setRGB(x, y, c.getRGB());
            }
        }
        return img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        // 背景渐变
        g2.setPaint(new GradientPaint(0, 0, new Color(0x5c4a3a), 0, h, new Color(0x3d2e1e)));
        g2.fillRect(0, 0, w, h);
        g2.setColor(new Color(122, 106, 85, 150));
        g2.fillRect(0, 0, 2, h);

        // ─── 绘制像素猫 ───
        int stage = getStage();
        BufferedImage catImg = renderCat(stage, frameIndex);
        int cw = catImg.getWidth();
        int ch = catImg.getHeight();

        // 按比例缩放到适应 160×160 区域内
        float scale = Math.min((float) CAT_DISPLAY_W / cw, (float) CAT_DISPLAY_H / ch);
        int dw = (int) (cw * scale);
        int dh = (int) (ch * scale);
        int dx = (w - dw) / 2;
        int dy = 30;

        // 使用 NEAREST_NEIGHBOR 保持像素感
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(catImg, dx, dy, dw, dh, null);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 阶段名
        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        g2.setColor(new Color(0xf5e6c8));
        String name = STAGE_NAMES[stage];
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(name, (w - fm.stringWidth(name)) / 2f, dy + dh + 20);

        // 鱼数
        g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        g2.setColor(new Color(0xc4b091));
        String fishTxt = "🐟 " + fishCount + " 条";
        fm = g2.getFontMetrics();
        g2.drawString(fishTxt, (w - fm.stringWidth(fishTxt)) / 2f, dy + dh + 44);

        // 进度条
        int barW = w - 30;
        int barH = 8;
        int bx = 15, by = dy + dh + 55;
        g2.setColor(new Color(0x3d2e1e));
        g2.fillRoundRect(bx, by, barW, barH, 4, 4);
        if (stage < STAGE_NAMES.length - 1) {
            int has = fishCount - FISH_NEED[stage];
            int need = FISH_NEED[stage + 1] - FISH_NEED[stage];
            int fill = (int) (barW * Math.min(1, (double) has / need));
            g2.setColor(new Color(0xe8c87a));
            g2.fillRoundRect(bx, by, fill, barH, 4, 4);
        } else {
            g2.setColor(new Color(0xe8c87a));
            g2.fillRoundRect(bx, by, barW, barH, 4, 4);
        }

        // 等级文字
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(0x8a7a65));
        String lv = "Lv." + (stage + 1) + " / " + STAGE_NAMES.length;
        fm = g2.getFontMetrics();
        g2.drawString(lv, (w - fm.stringWidth(lv)) / 2f, by + barH + 14);

        // ─── 飞鱼特效 ───
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        for (FlyFish f : fishes) {
            g2.drawString("🐟", f.x, f.y);
        }

        // Yum!
        if (yumTimer > 0) {
            float alpha = Math.min(1f, yumTimer / 15f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.setColor(new Color(0xe8c87a));
            fm = g2.getFontMetrics();
            g2.drawString(yumText, (w - fm.stringWidth(yumText)) / 2f, dy - 10);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    // ─── 飞鱼 ───
    private class FlyFish {
        float x, y, p;
        boolean done;
        FlyFish() {
            x = -30;
            y = 80 + (float) (Math.random() * 60);
        }
        void tick() {
            p += 0.05f;
            if (p >= 1) { done = true; return; }
            float t = p < 0.5 ? 2 * p * p : (float) (1 - Math.pow(-2 * p + 2, 2) / 2);
            x = -30 + (90) * t;
            y -= 2.5f;
            y += (float) Math.sin(p * Math.PI) * 20;
        }
    }
}
