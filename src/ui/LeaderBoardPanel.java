package ui;

import model.LeaderBoard;
import model.LeaderRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaderBoardPanel extends JDialog {
    public LeaderBoard leaderBoard;
    public LeaderBoardPanel(JFrame parent, LeaderBoard leaderBoard) {
        super(parent, "排行榜", true);
        this.leaderBoard = leaderBoard;
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("排行榜 TOP 5", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tablesPanel.add(createTablePanel("简单模式", leaderBoard.getTopRecords("简单模式")));
        tablesPanel.add(createTablePanel("困难模式", leaderBoard.getTopRecords("困难模式")));
        add(tablesPanel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("关闭");
        closeBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createTablePanel(String title, List<LeaderRecord> records) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        String[] columns = {"#", "玩家", "分数", "用时"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
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
        table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}

