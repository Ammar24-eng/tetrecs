package uk.ac.soton.comp1206.scene;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Multimedia {
    public static final Logger logger = LogManager.getLogger(Multimedia.class);
    private static MediaPlayer audioPlayer;
    private static MediaPlayer musicPlayer;
    private boolean soundEnable = true;


    public void playAudio(String file) {
        if(!soundEnable){
            return;
        }
        try {
            stopAudio(); // Stops any  audio
            var toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
            var media = new Media(toPlay);
            audioPlayer = new MediaPlayer(media);
            audioPlayer.play();
            logger.info("played sound:" + file);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("can't play sound");
            soundEnable = false;
        }
    }

    public void playMusic(String file) {
        stopMusic(); // Stops music
        if(!soundEnable){
            return;
        }
        try {


            stopAudio(); // Stops audio
            var toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
            var media = new Media(toPlay);
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.play();
        }catch(Exception e){
            e.printStackTrace();
            logger.error("can't play sound");
            soundEnable = false;
        }
    }

    public void stopAudio() {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
    }

    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }
}

