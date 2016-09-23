package kawaiiklash;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static kawaiiklash.Utility.fail;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Like the sprite loader except it loads sounds. It is a singleton.
 *
 * @author Jeff Niu
 */
public class SoundLoader {

    private static SoundLoader loader;

    public static SoundLoader get() {
        if (loader == null) {
            loader = new SoundLoader();
        }
        return loader;
    }

    private static String getName(String file) {
        return file.substring(0, file.indexOf('.'));
    }

    private SoundLoader() {
    }

    private Audio loadAudio(String ref) {
        InputStream in = ResourceLoader.getResourceAsStream(ref);
        Audio sound;
        try {
            sound = AudioLoader.getAudio("WAV", in);
        } catch (IOException ex) {
            sound = null;
            fail(ex);
        }
        return sound;
    }

    public List<Sound> loadSounds(String ref) {
        if (ref == null) {
            return null;
        }
        Parser parser = Parser.get();
        List<String> files = parser.getAudioFiles(ref);
        List<Sound> sounds = new ArrayList<>(files.size());
        for (String file : files) {
            Audio audio = loadAudio(SpriteLoader.filePathSwap(ref, file));
            String name = getName(file);
            Sound sound = new SoundImpl(audio, name);
            sounds.add(sound);
        }
        return sounds;
    }

    public void preloadSound(String ref) {
        loadSounds(ref);
    }

}
