package model;

public class LeaderRecord {
    public String userName;
    public String mode;
    public int score;
    public int timeUsed;

    public LeaderRecord(String userName, String mode, int score, int timeUsed){
        this.userName = userName;
        this.mode = mode;
        this.score = score;
        this.timeUsed = timeUsed;
    }

    
    public String getTimeFormatted(){
        return String.format("%02d:%02d", timeUsed/60,timeUsed % 60 );
    }
    public String toLine(){
        return userName + "," + mode + "," + score + "," + timeUsed;
    }
    public static LeaderRecord fromLine(String line){
        String[] parts = line.split(",");
         if (parts.length == 4) {
            return new LeaderRecord(parts[0], parts[1],
                    Integer.parseInt(parts[2]), Integer.parseInt(parts[3])
                    );
        }
        return null;
    }
}
