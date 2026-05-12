package ui;

import utils.SaveManager;

import javax.security.auth.Refreshable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SaveLoadDialog extends JDialog {
    private String username;
    private String mode;
    private GamePanel gamePanel;
    private JTable table;
    private DefaultTableModel tableModel;
    public SaveLoadDialog(JFrame parent, GamePanel gamePanel,String username, String mode) {
        super(parent, "Save/Load", true);
        this.gamePanel = gamePanel;
        this.username = username;
        this.mode = mode;
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JLabel titlelable = new JLabel("Save/Load" + username + " (" + mode + ")", SwingConstants.CENTER);
        titlelable.setFont(new Font("Arial", Font.BOLD, 20));
        add(titlelable, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        JPanel btnPanel = createButtonPanel();
        add(btnPanel, BorderLayout.SOUTH);
        refreshTable();
    }

        private JPanel createTablePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            String[] columns = {"#", "分数", "剩余时间", "用时","状态"};
            tableModel = new DefaultTableModel(columns, 0){
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            table = new JTable(tableModel);
            table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            table.setRowHeight(30);
            table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);
            return panel;
        }

        private JPanel createButtonPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));
            JButton saveBtn = new JButton("Save to the selected part");
            saveBtn.setBackground(Color.GREEN);
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setFocusPainted(false);
            saveBtn.addActionListener(e -> handleSave());

            JButton loadBtn = new JButton("Load from the selected part");
            loadBtn.setBackground(Color.BLUE);
            loadBtn.setForeground(Color.WHITE);
            loadBtn.setFocusPainted(false);
            loadBtn.addActionListener(e -> handleLoad());

            JButton deleteBtn = new JButton("Delete the selected part");
            deleteBtn.setBackground(Color.RED);
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.addActionListener(e -> handleDelete());

            JButton refreshBtn = new JButton("Refresh");
            refreshBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            refreshBtn.addActionListener(e -> refreshTable());

            JButton closeBtn = new JButton("Close");
            closeBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            closeBtn.addActionListener(e -> dispose());

            panel.add(saveBtn);
            panel.add(loadBtn);
            panel.add(deleteBtn);
            panel.add(refreshBtn);
            panel.add(closeBtn);

            return panel;
    }
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (int slot = 1; slot <= 9 ; slot++) {
            if(SaveManager.hasSave(username, mode, slot)){
                SaveManager.SaveData data = SaveManager.loadGame(SaveManager.getSaveFilePath(username, mode, slot));
                tableModel.addRow(new Object[]{slot, data.score, data.remainingSeconds, data.elapsedSeconds, "已保存"});
            } else {
                tableModel.addRow(new Object[]{slot, "", "", "", "NO_USE"});
            }
        }
    }

    private void handleSave(){
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a slot to save to.");
            return;
        }

        int slot = (int) tableModel.getValueAt(selectedRow, 0);

        if(SaveManager.hasSave(username, mode, slot)){
            int confirm = JOptionPane.showConfirmDialog(this, "This slot already has a save. Do you want to overwrite it?", "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        boolean success = gamePanel.saveGame(slot);
        if(success){
            JOptionPane.showMessageDialog(this, "Save successful.");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed.");
        }
    }

    private void handleLoad(){
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {  // 修复：检查是否选中
            JOptionPane.showMessageDialog(this, "请先选择一个存档！");
            return;
        }
        int slot = (int) tableModel.getValueAt(selectedRow, 0);
        if(!SaveManager.hasSave(username,mode,slot)){
            JOptionPane.showMessageDialog(this, "This slot does not have a save.");
            return;
        }
        if(gamePanel.boardPanelIsStarted()){
            int confirm = JOptionPane.showConfirmDialog(this, "This slot already has a save. Do you want to overwrite it?", "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        boolean success = gamePanel.loadGame(slot);
        if(success){
            JOptionPane.showMessageDialog(this, "Load successful.");
           dispose();
        } else{
            JOptionPane.showMessageDialog(this, "Load failed.");
        }
    }

    private void handleDelete(){
        int selectedRow = table.getSelectedRow();
        int slot = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 4);
        if(status.equals("NO_USE")){
            JOptionPane.showMessageDialog(this, "This slot does not have a save.");
            return;
        }
        if(!SaveManager.hasSave(username,mode,slot)){
            JOptionPane.showMessageDialog(this, "This slot does not have a save.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this save?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            boolean success = SaveManager.deleteSave(username, mode, slot);
            if(success){
                JOptionPane.showMessageDialog(this, "Delete successful.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        }
    }
}
