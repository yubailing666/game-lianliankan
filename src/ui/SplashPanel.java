package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class SplashPanel extends JPanel implements ActionListener {

    private GameFrame parent;
    private Timer timer;

    private float bobAngle;
    private float lineSway;
    private float fishAngle;
    private float fishBob;
    private float titleAlpha;
    private boolean fishJumping;
    private int jumpTimer;
    private float pulse;

    public SplashPanel(GameFrame parent) {
        this.parent = parent;
        setLayout(null);
        timer = new Timer(16, this);
        timer.start();
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                parent.showPage("login");
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        bobAngle += 0.03f;
        lineSway = (float) Math.sin(bobAngle * 0.5) * 4;
        fishAngle += 0.025f;

        if (!fishJumping && Math.random() < 0.005) {
            fishJumping = true;
            jumpTimer = 0;
        }
        if (fishJumping) {
            jumpTimer++;
            fishBob = (float) Math.sin(jumpTimer * 0.15f) * 30;
            if (jumpTimer > 20) {
                fishJumping = false;
                fishBob = 0;
            }
        }

        if (titleAlpha < 1) titleAlpha += 0.02f;
        if (titleAlpha > 1) titleAlpha = 1;
        pulse = (float) Math.min(1.0, 0.3 + 0.7 * Math.abs(Math.sin(System.currentTimeMillis() * 0.003)));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // 1. 暖棕渐变背景
        g2.setPaint(new GradientPaint(0, 0, new Color(0x5c4a3a), 0, h, new Color(0x3d2e1e)));
        g2.fillRect(0, 0, w, h);

        // 2. 水面
        int waterY = h * 2 / 3;
        g2.setPaint(new GradientPaint(0, waterY, rgba(0x4a6b5c, 180), 0, h, rgba(0x2d5a4a, 200)));
        g2.fillOval(-50, waterY - 20, w + 100, h - waterY + 60);

        // 水面波浪
        g2.setStroke(new BasicStroke(2));
        g2.setColor(rgba(0x5a8a7a, 100));
        for (int x = 0; x < w; x += 4) {
            int yy = waterY - 10 + (int) (Math.sin((x + bobAngle * 50) * 0.03) * 6);
            g2.drawLine(x, yy, x + 2, yy);
        }

        // 3. 鱼线 + 浮标
        int dockX = w / 2 + 40;
        int dockY = 30;
        int bobberY = waterY - 20 + (int) (Math.sin(bobAngle) * 8);
        int bobberX = dockX + (int) lineSway;

        g2.setStroke(new BasicStroke(3));
        g2.setColor(new Color(0x8D6E63));
        g2.drawLine(dockX, dockY + 40, dockX - 60, dockY);

        g2.setStroke(new BasicStroke(1));
        g2.setColor(rgba(0xBCAAA4, 180));
        g2.drawLine(dockX - 60, dockY, bobberX, bobberY);

        int bobR = 7;
        g2.setColor(new Color(0xE53935));
        g2.fillOval(bobberX - bobR, bobberY - bobR, bobR * 2, bobR * 2);
        g2.setColor(Color.WHITE);
        g2.fillOval(bobberX - bobR / 2, bobberY - bobR / 2 - 2, bobR, bobR);
        g2.setColor(rgba(0xE53935, 50));
        g2.fillOval(bobberX - bobR, bobberY + 10, bobR * 2, bobR * 2);

        // 4. 鱼
        int fishX = (int) (w / 2 + Math.cos(fishAngle) * 80);
        int fishY = waterY + 30 + (int) (Math.sin(fishAngle * 1.3) * 20) - (int) fishBob;

        g2.translate(fishX, fishY);
        g2.rotate(Math.cos(fishAngle) > 0 ? 0.3 : -Math.PI - 0.3);
        g2.setColor(new Color(0xFF8A65));
        g2.fillOval(-8, -4, 16, 8);
        g2.fillPolygon(new int[]{8, 14, 14}, new int[]{0, -4, 4}, 3);
        g2.setColor(Color.WHITE);
        g2.fillOval(-3, -2, 4, 4);
        g2.setColor(Color.BLACK);
        g2.fillOval(-2, -1, 2, 2);
        g2.setTransform(new AffineTransform());

        if (fishJumping) {
            g2.setColor(rgba(0x80CBC4, 150));
            int spX = (int) (w / 2 + Math.cos(fishAngle) * 80);
            int spY = waterY + 30 + (int) (Math.sin(fishAngle * 1.3) * 20);
            g2.fillOval(spX - 8, spY - 4, 16, 8);
            g2.fillOval(spX - 5, spY + 4, 10, 6);
            g2.fillOval(spX + 4, spY + 2, 8, 8);
        }

        // 5. 码头 + 猫
        g2.setColor(new Color(0x6D4C41));
        g2.fillRect(dockX - 10, h * 2 / 3 - 80, 20, 80);
        g2.setColor(new Color(0x5D4037));
        g2.fillRect(dockX - 25, h * 2 / 3 - 85, 50, 10);
        g2.setColor(rgba(0x4E342E, 100));
        for (int i = 0; i < 4; i++) {
            g2.fillRect(dockX - 25, h * 2 / 3 - 85 + i * 3, 50, 1);
        }
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        g2.drawString("😸", dockX - 20, h * 2 / 3 - 95);

        // 6. 标题
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
        g2.setFont(new Font("Arial", Font.BOLD, 42));
        g2.setColor(new Color(0xf5e6c8));
        g2.drawString("HAJIMI", w / 2 - 90, 90);
        g2.setColor(new Color(0xe8c87a));
        g2.drawString("MATCH", w / 2 - 95, 135);
        g2.setColor(rgba(0x7a6a55, 150));
        g2.fillRect(w / 2 - 60, 148, 120, 2);
        g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        g2.setColor(new Color(0xc4b091));
        g2.drawString("🐟 喂鱼 · 🐱 撸猫 · 🌱 成长", w / 2 - 90, 175);

        // 7. 呼吸点击提示
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
        g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        g2.setColor(new Color(0x8a7a65));
        g2.drawString("👆 点击开始", w / 2 - 45, h - 40);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }

    // 工具：hex 颜色 + alpha
    private Color rgba(int hex, int alpha) {
        return new Color(
            (hex >> 16) & 0xFF,
            (hex >> 8) & 0xFF,
            hex & 0xFF,
            alpha
        );
    }
}
