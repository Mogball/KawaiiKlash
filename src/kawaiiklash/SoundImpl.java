package kawaiiklash;

import org.newdawn.slick.openal.Audio;

/**
 * A simple wrapper class for the Slick2D audio.
 *
 * @author Jeff Niu
 */
public class SoundImpl implements Sound {

    private float pitch;
    private float gain;
    private final Audio audio;
    private final String name;

    public SoundImpl(Audio audio, String name) {
        this.audio = audio;
        this.name = name;
        pitch = 1.0f;
        gain = 1.0f;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public float getGain() {
        return gain;
    }

    @Override
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void setGain(float gain) {
        this.gain = gain;
    }

    @Override
    public String getName() {
        return name;
    }

}
