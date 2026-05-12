package model;

/**
 * 排行榜单条记录 — 存储一名玩家在某模式下的一次最佳成绩
 * 支持与 CSV 格式之间的序列化/反序列化（用于 leaderboard.dat）
 */
public class LeaderRecord {

    // ── 数据字段 ──

    public String userName;    // 玩家名
    public String mode;        // 游戏模式（"简单模式" / "困难模式"）
    public int score;          // 最终得分
    public int timeUsed;       // 用时（秒）

    public LeaderRecord(String userName, String mode, int score, int timeUsed) {
        this.userName = userName;
        this.mode = mode;
        this.score = score;
        this.timeUsed = timeUsed;
    }

    // ── 格式化 ──

    /** 用时格式化为 MM:SS */
    public String getTimeFormatted() {
        return String.format("%02d:%02d", timeUsed / 60, timeUsed % 60);
    }

    // ── 序列化 ──

    /** 转为 CSV 行：userName,mode,score,timeUsed */
    public String toLine() {
        return userName + "," + mode + "," + score + "," + timeUsed;
    }

    /** 从 CSV 行反序列化，格式不对返回 null */
    public static LeaderRecord fromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length == 4) {
            return new LeaderRecord(parts[0], parts[1],
                    Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        return null;
    }
}
