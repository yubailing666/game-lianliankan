package utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * 背景音乐管理器 — 保证切歌时当前曲目播完才切
 *
 * 用法:
 *   MusicManager.play("splash")   // 播放 splash.wav 并循环
 *   MusicManager.switchTo("login") // 等当前播完→无缝切换到 login
 */
public class MusicManager {

    public static final String DIR = "resource" + File.separator + "music" + File.separator;
    public static final String FALLBACK_DIR = "D:" + File.separator + "game-lianliankan" + File.separator + "resource" + File.separator + "music" + File.separator;

    private static Clip currentClip;
    private static String pendingTrack;
    private static boolean switching;

    /** 开始播某首（或切换） */
    public static void play(String name) {
        if (currentClip != null && currentClip.isRunning()) {
            // 有人正在播 → 标记切换，等它放完
            pendingTrack = name;
            switching = true;
            // 停止循环，让当前这次放完就停
            currentClip.loop(0);
        } else {
            loadAndPlay(name);
        }
    }

    /** 直接停 */
    public static void stop() {
        pendingTrack = null;
        switching = false;
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }

    // ── 内部 ──

    private static void loadAndPlay(String name) {
        try {
            if (currentClip != null) {
                currentClip.close();
                currentClip = null;
            }

            File file = new File(DIR + name + ".wav");
            if (!file.exists()) {
                file = new File(FALLBACK_DIR + name + ".wav");
            }
            if (!file.exists()) {
                System.err.println("音乐文件不存在: " + file.getAbsolutePath());
                return;
            }

            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            currentClip = AudioSystem.getClip();

            // 监听播放结束事件
            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (switching && pendingTrack != null) {
                        // 当前播完 → 切到下一首
                        String next = pendingTrack;
                        pendingTrack = null;
                        switching = false;
                        loadAndPlay(next);
                    } else if (!switching) {
                        // 正常循环
                        currentClip.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }
            });

            currentClip.open(stream);
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            switching = false;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("播放音乐失败: " + name);
            e.printStackTrace();
        }
    }
}
