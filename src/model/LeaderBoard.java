package model;

import java.io.*;
import java.util.*;

public class LeaderBoard {
    public static final String FILE_NAME = System.getProperty("user.dir") + File.separator + "leaderboard.dat";
    public static final int TOP_N = 5;

    public List<LeaderRecord> records;
    public LeaderBoard(){
        records = new ArrayList<>();
        loadFromFile();
    }

    public void addRecord(LeaderRecord record){
        records.add(record);
        saveToFile();
    }
    public List<LeaderRecord> getTopRecords(String mode){
        // 1. 筛选出指定模式的记录
        List<LeaderRecord> modeRecords = new ArrayList<>();
        for (LeaderRecord r : records) {
            if (r.mode.equals(mode)) {
                modeRecords.add(r);
            }
        }
        // 2. 排序：分数降序，分数相同用时升序
        Collections.sort(modeRecords, (a, b) -> {
            if (b.score != a.score) {
                return b.score - a.score;
            }
            return a.timeUsed - b.timeUsed;
        });
        // 3. 取前 TOP_N 条
        List<LeaderRecord> topRecords = new ArrayList<>();
        int count = Math.min(TOP_N, modeRecords.size());
        for (int i = 0; i < count; i++) {
            topRecords.add(modeRecords.get(i));
        }
        return topRecords;
    }
    public void loadFromFile(){
        File file = new File(FILE_NAME);
        if(!file.exists()) return;
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while((line = reader.readLine())!=null){
                LeaderRecord record = LeaderRecord.fromLine(line.trim());
                if(record != null){
                    records.add(record);
                }
            }
        } catch (IOException e) {}
    }
    public void saveToFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))){
            for (LeaderRecord r : records) {
                writer.write(r.toLine());
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
