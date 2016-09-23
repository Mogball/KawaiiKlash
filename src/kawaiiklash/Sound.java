package kawaiiklash;

import org.newdawn.slick.openal.Audio;

/**
 * A sound interface describes anything that can be played as audio. It is
 * really a wrapper class for the Slick2D audio.
 *
 * @author Jeff Niu
 */
public interface Sound {

    Audio getAudio();

    void setPitch(float pitch);

    void setGain(float gain);

    float getPitch();

    float getGain();

    String getName();

    default void playEffect(float pitch, float gain, boolean loop) {
        getAudio().playAsSoundEffect(pitch * getPitch(), gain * getGain(), loop);
    }

    default void playMusic(float pitch, float gain, boolean loop) {
        getAudio().playAsMusic(pitch * getPitch(), gain * getGain(), loop);
    }

}
