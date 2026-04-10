import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound
{
    Clip clip;
    URL sounds[] = new URL[6];

    public Sound()
    {
        sounds[0] = getClass().getResource("/Audio/SFX/Bounce.wav");
        sounds[1] = getClass().getResource("/Audio/SFX/Contact.wav");
        sounds[2] = getClass().getResource("/Audio/SFX/Pause.wav");
        sounds[3] = getClass().getResource("/Audio/SFX/Unpause.wav");
        sounds[4] = getClass().getResource("/Audio/SFX/Clash.wav");
        sounds[5] = getClass().getResource("/Audio/SFX/Dagger.wav");
    }

    public void setFile(int i, float gain)
    {
        try
        {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sounds[i]);
            
            clip = AudioSystem.getClip();
            clip.open(ais);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gain);

        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void play()
    {
        clip.start();
    }

    public void loop()
    {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop()
    {
        clip.stop();
    }
}