package ui;

import model.LeaderBoard;
import model.LeaderRecord;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 排行榜弹窗 — 分屏展示简单/困难两种模式的前 5 名
 *
 * 左表：简单模式 TOP 5
 * 右表：困难模式 TOP 5
 * 字段：排名 #、玩家名、分数、用时（MM:SS）
 */
public class LeaderBoardPanel extends JDialog {

    public LeaderBoard leaderBoard;

    public LeaderBoardPanel(JFrame parent, LeaderBoard leaderBoard) {
        super(parent, "排行榜", true);
        this.leaderBoard = leaderBoard;

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0xf4f0e8));

        // ── 标题 ──
        JLabel titleLabel = new JLabel("排行榜 TOP 5", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x3a3530));
        add(titleLabel, BorderLayout.NORTH);

        // ── 双表并排 ──
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tablesPanel.add(createTablePanel("简单模式", leaderBoard.getTopRecords("简单模式")));
        tablesPanel.add(createTablePanel("困难模式", leaderBoard.getTopRecords("困难模式")));
        add(tablesPanel, BorderLayout.CENTER);

        // ── 关闭按钮 ──
        JButton closeBtn = new JButton("关闭");
        closeBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        closeBtn.setBackground(new Color(0xd4a04a));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建单张排行榜表格面板
     * @param title   表格标题（如"简单模式"）
     * @param records 已排序的前 N 条记录
     */
    private JPanel createTablePanel(String title, List<LeaderRecord> records) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xf4f0e8));
        panel.setBorder(BorderFactory.createTitledBorder(
            null, title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Microsoft YaHei", Font.BOLD, 14), new Color(0x3a3530)));

        String[] columns = {"#", "玩家", "分数", "用时"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        if (records.isEmpty()) {
            model.addRow(new Object[]{"", "暂无记录", "", ""});
        } else {
            for (int i = 0; i < records.size(); i++) {
                LeaderRecord r = records.get(i);
                model.addRow(new Object[]{i + 1, r.userName, r.score, r.getTimeFormatted()});
            }
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setForeground(new Color(0x3a3530));
        table.setBackground(new Color(0xf4f0e8));
        table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        table.getTableHeader().setForeground(new Color(0x3a3530));
        table.getTableHeader().setBackground(new Color(0xece6dc));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}
