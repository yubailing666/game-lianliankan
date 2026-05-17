package ui;

import effects.EffectManager;
import model.*;
import model.Rectangle;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 棋盘渲染与交互面板 — 游戏的核心显示区域
 *
 * 职责：
 *   - 从 resource/ 目录加载棋子图片
 *   - 绘制棋盘网格（含选中高亮、格子边框）
 *   - 处理鼠标点击选择与消除判定
 *   - 消除成功后绘制连接线动画（200ms）
 *   - 胜利检测与回调触发
 */
public class BoardPanel extends JPanel {

    // ── 布局参数 ──
    int offSetX;
    int offSetY;
    int width;
    int height;
    int cellWidth;
    int cellHeight;

    // ── 棋盘数据 ──
    GameBoard gameBoard;
    int totalRow;
    int totalCol;

    // ── 图片资源 ──
    List<Image> imageList = new ArrayList<>();
    Image[] scaledImages;

    // ── 游戏状态 ──
    StatusPanel statusPanel;
    boolean started;                    // 是否已开始（START 按钮控制）
    boolean animating;                  // 是否正在播放消除动画
    Position firstSelected = null;      // 第一次点击选中的位置
    Position secondSelected = null;     // 第二次点击选中的位置

    // ── 连接线绘制 ──
    List<Line> lineList = new ArrayList<>();
    boolean lineVisible;

    // ── 回调 ──
    Runnable onWinCallback;
    Runnable onFishFeed;

    // ── 特效系统 ──
    private EffectManager effectManager;

    // ════════════════════════════════════════════════════
    // 构造与初始化
    // ════════════════════════════════════════════════════

    public BoardPanel(GameBoard gameBoard, StatusPanel statusPanel,
                      int offSetX, int offSetY, int width, int height) {
        this.statusPanel = statusPanel;
        this.offSetX = offSetX;
        this.offSetY = offSetY;

        setBounds(offSetX, offSetY, width, height);
        setBackground(new Color(0x6b5b45));
        setOpaque(true);

        this.totalRow = gameBoard.getRowCnt();
        this.totalCol = gameBoard.getColCnt();
        this.width = width;
        this.height = height;
        this.gameBoard = gameBoard;

        setPreferredSize(new Dimension(this.width, this.height));
        // 格子尺寸按面板大小计算，整数除法余数自然居中
        this.cellWidth = this.width / totalCol;
        this.cellHeight = this.height / totalRow;
        this.offSetX = (this.width - this.cellWidth * totalCol) / 2;
        this.offSetY = (this.height - this.cellHeight * totalRow) / 2;

        // ── 加载棋子图片资源 ──
        File dir = new File("resource");
        if (!dir.exists()) {
            dir = new File("D:" + File.separator + "game-lianliankan" + File.separator + "resource");
        }
        File[] files = dir.listFiles();
        if (files != null) {
            // 按文件名中的数字升序排序（0.png < 2.png < 10.png）
            // 非数字文件（background.png）排在最后
            Arrays.sort(files, (a, b) -> {
                int na = parseNumericPrefix(a.getName());
                int nb = parseNumericPrefix(b.getName());
                return Integer.compare(na, nb);
            });
            for (File file : files) {
                if (file.getName().endsWith(".png")) {
                    ImageIcon icon = new ImageIcon(file.getPath());
                    imageList.add(icon.getImage());
                }
            }
        }

        // 预缩放到格子大小（同步缩放，消除每帧缩放开销 + 懒加载空白 bug）
        scaledImages = new Image[imageList.size()];
        for (int i = 0; i < imageList.size(); i++) {
            BufferedImage bi = new BufferedImage(cellWidth, cellHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(imageList.get(i), 0, 0, cellWidth, cellHeight, null);
            g2d.dispose();
            scaledImages[i] = bi;
        }

        // ── 鼠标点击监听 ──
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        // ── 初始化特效管理器并启动动画定时器 ──
        effectManager = new EffectManager();
        Timer effectTimer = new Timer(16, e -> {
            effectManager.update();
            if (effectManager.hasActiveEffects()) {
                repaint();
            }
        });
        effectTimer.start();
    }

    // ════════════════════════════════════════════════════
    // 回调注册
    // ════════════════════════════════════════════════════

    public void setOnWinCallback(Runnable callback) {
        this.onWinCallback = callback;
    }

    public void setOnFishFeed(Runnable callback) {
        this.onFishFeed = callback;
    }

    // ════════════════════════════════════════════════════
    // 游戏控制
    // ════════════════════════════════════════════════════

    /** 激活棋盘交互（START 按钮调用） */
    public void startGame() {
        started = true;
    }

    /** 替换棋盘（RESTART / 设置变更时调用） */
    public void setGameBoard(GameBoard newBoard) {
        this.gameBoard = newBoard;
        this.totalRow = newBoard.getRowCnt();
        this.totalCol = newBoard.getColCnt();
        this.started = false;
        this.firstSelected = null;
        this.secondSelected = null;
        this.lineList.clear();
        effectManager.clearAll();
        repaint();
    }

    /** 刷新 StatusPanel 上的配对进度信息 */
    public void refreshPairInfo() {
        int totalPairs = gameBoard.getTotalPairs();
        int clearedPairs = gameBoard.getClearedPairs();
        int remainingPairs = gameBoard.getRemainingPairs();
        statusPanel.updatePairInfo(remainingPairs, clearedPairs, totalPairs);
    }

    // ════════════════════════════════════════════════════
    // 坐标映射
    // ════════════════════════════════════════════════════

    /** 像素坐标 → 棋盘行列坐标（超出边界返回 null） */
    public Position getPositionByPoint(int x, int y) {
        int col = (x - offSetX) / cellWidth;
        int row = (y - offSetY) / cellHeight;
        if (row < 0 || row >= totalRow || col < 0 || col >= totalCol) {
            return null;
        }
        return new Position(row, col);
    }

    /** 获取某个棋盘格子在屏幕上的像素矩形 */
    public Rectangle getRectangle(Position position) {
        int x = offSetX + position.getCol() * cellWidth;
        int y = offSetY + position.getRow() * cellHeight;
        return new Rectangle(x, y, cellWidth, cellHeight);
    }

    // ════════════════════════════════════════════════════
    // 连接线绘制
    // ════════════════════════════════════════════════════

    /** 显示消除连接线 */
    public void showLine(List<Position> path) {
        lineList.clear();
        lineList.add(new Line(path));
        lineVisible = true;
        repaint();
    }

    /** 清除连接线 */
    public void clearLine() {
        lineVisible = false;
        lineList.clear();
        repaint();
    }

    // ════════════════════════════════════════════════════
    // 点击处理（核心交互逻辑）
    // ════════════════════════════════════════════════════

    /**
     * 处理鼠标点击棋盘
     *
     * 流程：
     *   1. 如果未开始或正在动画 → 忽略
     *   2. 第一次选中 → 高亮该格子
     *   3. 第二次选中不同格子 → 判断是否可以消除
     *      - 图标不同 → 取消选中
     *      - 图标相同且可连接 → 显示连线动画 → 200ms 后消除
     *      - 图标相同但不可连接 → 取消选中
     */
    public void handleClick(int x, int y) {
        if (!started) return;
        if (animating) return;

        Position pos = getPositionByPoint(x, y);
        if (pos == null) return;

        Cell clickedCell = gameBoard.getCell(pos.getRow(), pos.getCol());
        if (clickedCell == null || clickedCell.isEmpty()) return;

        // ── 第一次选中 ──
        if (firstSelected == null) {
            gameBoard.clearAllChosen();
            clickedCell.setChosen(true);
            firstSelected = pos;
            repaint();
            return;
        }

        // ── 点击同一位置 → 取消选中 ──
        if (firstSelected.equals(pos)) {
            clickedCell.setChosen(false);
            firstSelected = null;
            secondSelected = null;
            repaint();
            return;
        }

        // ── 第二次选中 ──
        secondSelected = pos;
        Cell firstCell = gameBoard.getCell(firstSelected.getRow(), firstSelected.getCol());
        Cell secondCell = gameBoard.getCell(secondSelected.getRow(), secondSelected.getCol());

        // 图标不同 → 取消选中
        if (firstCell.getIconIndex() != secondCell.getIconIndex()) {
            gameBoard.clearAllChosen();
            firstCell.setChosen(false);
            secondCell.setChosen(false);
            firstSelected = null;
            secondSelected = null;
            repaint();
            return;
        }

        // 图标相同且可连接 → 消除动画
        if (Utils.canLinkAB(gameBoard, firstSelected, secondSelected)) {
            secondCell.setChosen(true);
            repaint();
            animating = true;

            List<Position> path = Utils.findPath(gameBoard, firstSelected, secondSelected);
            showLine(path);

            // 200ms 后消除
            Timer timer = new Timer(200, e -> {
                // 触发破碎特效（从两个棋子中点爆发）
                effectManager.createShatterEffect(firstSelected, secondSelected, cellWidth, cellHeight, firstCell.getIconIndex());

                firstCell.setEmpty(true);
                secondCell.setEmpty(true);
                statusPanel.addScore(10);

                if (onFishFeed != null) onFishFeed.run();
                refreshPairInfo();

                // 胜利检测
                if (gameBoard.isAllCleared()) {
                    statusPanel.winGame();
                    if (onWinCallback != null) {
                        onWinCallback.run();
                    }
                    JOptionPane.showMessageDialog(BoardPanel.this, "你赢了！");
                }

                // 恢复状态
                firstCell.setChosen(false);
                secondCell.setChosen(false);
                lineVisible = false;
                lineList.clear();
                firstSelected = null;
                secondSelected = null;
                animating = false;
                repaint();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // 不可连接 → 取消选中
            gameBoard.clearAllChosen();
            firstCell.setChosen(false);
            secondCell.setChosen(false);
            firstSelected = null;
            secondSelected = null;
            repaint();
        }
    }

    // ════════════════════════════════════════════════════
    // 自定义绘制
    // ════════════════════════════════════════════════════
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // ── 绘制棋盘格子 ──
        for (int i = 0; i < gameBoard.getRowCnt(); i++) {
            for (int j = 0; j < gameBoard.getColCnt(); j++) {
                Rectangle rec = getRectangle(new Position(i, j));
                int iconIdx = gameBoard.getCell(i, j).getIconIndex();
                // iconIdx > 0：跳过 border（0.png），真正的棋子从 1.png 开始
                if (iconIdx > 0 && iconIdx < scaledImages.length) {
                    g2.drawImage(scaledImages[iconIdx],
                        rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight(),
                        this
                );
                }  // end: if (iconIdx > 0)

                // 选中高亮框
                if (gameBoard.getCell(i, j).getIsChosen()) {
                    g2.setColor(new Color(0xe8c87a));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect(rec.getX() + 1, rec.getY() + 1,
                            rec.getWidth() - 3, rec.getHeight() - 3);
                } else {
                    g2.setColor(new Color(122, 106, 85));
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRect(rec.getX(), rec.getY(),
                            rec.getWidth() - 1, rec.getHeight() - 1);
                }
            }
        }

        // ── 绘制连接线 ──
        g2.setColor(new Color(0xe8c87a));
        g2.setStroke(new BasicStroke(3));
        if (lineVisible) {
            for (Line line : lineList) {
                List<Position> path = line.getPath();
                for (int pIdx = 0; pIdx < path.size() - 1; pIdx++) {
                    Rectangle rec1 = getRectangle(path.get(pIdx));
                    Rectangle rec2 = getRectangle(path.get(pIdx + 1));
                    g2.drawLine(
                            (int) rec1.getCenterPosition().getX(),
                            (int) rec1.getCenterPosition().getY(),
                            (int) rec2.getCenterPosition().getX(),
                            (int) rec2.getCenterPosition().getY()
                    );
                }
            }
        }

        // ── 绘制破碎特效（在最上层） ──
        effectManager.draw(g2);
    }
    public GameBoard getGameBoard() {
        return gameBoard;
    }
    public boolean isStarted() {
        return started;
    }
    public void setStarted(boolean started) {
        this.started = started;
    }
    public void restoreFromSave(GameBoard saveboard){
        this.gameBoard = saveboard;
        this.totalRow = saveboard.getRowCnt();
        this.totalCol = saveboard.getColCnt();
        this.firstSelected = null;
        this.secondSelected = null;
        this.lineList.clear();
        repaint();
    }

    /** 从文件名提取开头的数字（0.png→0, background.png→Integer.MAX_VALUE） */
    private static int parseNumericPrefix(String name) {
        StringBuilder digits = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            } else if (digits.length() > 0) {
                break;  // 数字结束后停止
            }
        }
        if (digits.length() == 0) return Integer.MAX_VALUE;  // 非数字文件排最后
        try {
            return Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}
