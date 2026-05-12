package utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * 背景音乐管理器 — 支持三首曲目的循环播放与无缝切换
 *
 * 原理：切换时先停止当前曲目循环，等当前这一次播完后自动加载并播放下一首，
 * 避免生硬截断。如果当前没有曲目在播，则直接加载新曲目。
 *
 * 用法:
 *   MusicManager.play("splash")   // 循环播放 splash.wav
 *   MusicManager.play("login")    // 等当前播完后切到 login.wav
 *   MusicManager.stop()           // 立即停止
 */
public class MusicManager {

    /** 音乐资源目录（相对于项目根目录） */
    public static final String DIR = "resource" + File.separator + "music" + File.separator;
    /** 备用音乐目录（硬编码绝对路径，应对工作目录不一致的情况） */
    public static final String FALLBACK_DIR = "D:" + File.separator + "game-lianliankan"
            + File.separator + "resource" + File.separator + "music" + File.separator;

    /** 当前正在播放的音频剪辑 */
    private static Clip currentClip;
    /** 等待当前曲目结束后切换到的目标曲目名 */
    private static String pendingTrack;
    /** 是否正在切换过程中 */
    private static boolean switching;

    // ── 公开接口 ──

    /**
     * 播放指定名称的 WAV 曲目（或排队等待当前曲目结束）
     * @param name 曲目标识（如 "splash", "login", "game"），对应 resource/music/{name}.wav
     */
    public static void play(String name) {
        if (currentClip != null && currentClip.isRunning()) {
            // 当前有曲目在播 → 标记切换，停止循环让当前这次播完
            pendingTrack = name;
            switching = true;
            currentClip.loop(0);
        } else {
            loadAndPlay(name);
        }
    }

    /** 立即停止播放并重置切换状态 */
    public static void stop() {
        pendingTrack = null;
        switching = false;
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }

    // ── 内部实现 ──

    /**
     * 加载指定曲目并开始循环播放
     * 先尝试项目相对路径，再尝试备用绝对路径
     */
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

            // 监听播放结束事件，实现无缝切换
            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (switching && pendingTrack != null) {
                        // 当前曲目播完 → 切换到排队的曲目
                        String next = pendingTrack;
                        pendingTrack = null;
                        switching = false;
                        loadAndPlay(next);
                    } else if (!switching) {
                        // 正常循环播放
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
