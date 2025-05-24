package uet.oop.space_invaders.spaceinvaders;

import javax.sound.sampled.*;

public class SoundEffect {
    private Clip clip;

    public SoundEffect(String resourcePath) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(resourcePath));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip == null) return;
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }
}
