package kawaiiklash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * The sprite loader is a class designed specifically to read sprite data
 * files and load the images into sprite sheets.
 *
 * @author Jeff Niu
 */
public class SpriteLoader {

    /**
     * The single instance of this class.
     */
    private static SpriteLoader loader;

    /**
     * Get the single instance of this class.
     *
     * @return
     */
    public static SpriteLoader get() {
        if (loader == null) {
            loader = new SpriteLoader();
        }
        return loader;
    }

    /**
     * Determine whether or not one string exists in another, where the
     * first string has no spaces and the second string is a list of words
     * separated by spaces. This cannot be replaced with
     * {@link java.lang.String#contains(java.lang.CharSequence)} because
     * there is a chance that the word exists inside another word. This
     * method is with the method below to determine the number of unique
     * sprite sheets. The naming convention is
     * "sheetName_[frameNumber].fileExtension".
     *
     * @param s
     * @param t
     * @return
     */
    private static boolean hasSheetName(String s, String t) {
        Scanner scnr = new Scanner(s);
        while (scnr.hasNext()) {
            if (scnr.next().equals(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get an array of all the unique sheet names so that the images can be
     * split into sprite sheets.
     *
     * @param cfgs
     * @return
     */
    private static String[] getSheetNames(List<SpriteConfiguration> cfgs) {
        StringBuilder sb = new StringBuilder(cfgs.size());
        int n = 0;
        for (SpriteConfiguration cfg : cfgs) {
            if (!hasSheetName(sb.toString(), cfg.getSheetName())) {
                sb.append(cfg.getSheetName()).append(" ");
                n++;
            }
        }
        String[] names = new String[n];
        Scanner scnr = new Scanner(sb.toString());
        for (int i = 0; i < n; i++) {
            names[i] = scnr.next();
        }
        return names;
    }

    /**
     * Count the number of frames in a certain sprite sheet given the
     * sprite sheet name.
     *
     * @param cfgs
     * @param name
     * @return
     */
    private static int numberOfFrames(List<SpriteConfiguration> cfgs, String name) {
        int n = 0;
        for (SpriteConfiguration cfg : cfgs) {
            if (cfg.getSheetName().equals(name)) {
                n++;
            }
        }
        return n;
    }

    /**
     * This can be used to get the file reference of an image file given
     * the file reference of the data file. Since only the image file name
     * is given but the data file is in the same folder, the reference path
     * for the images can still be made.
     *
     * @param src
     * @param ref
     * @return
     */
    public static String filePathSwap(String src, String ref) {
        return src.substring(0, src.lastIndexOf('/') + 1) + ref;
    }

    /**
     * Deep copy a sprite sheet list.
     *
     * @param src
     * @return
     */
    private static List<SpriteSheet> copyList(List<SpriteSheet> src) {
        List<SpriteSheet> dest = new ArrayList<>(src.size());
        for (SpriteSheet sheet : src) {
            dest.add(sheet.copy());
        }
        return dest;
    }

    /**
     * A cache that is used so that, if the same sprite sheet set were
     * loader, then we do not have to go through the entire process.
     */
    private final HashMap<String, List<SpriteSheet>> cache;

    /**
     * Instantiate the sprite loader.
     */
    private SpriteLoader() {
        cache = new HashMap<>(500);
    }

    /**
     * A method that loads an image based on a reference using the
     * Slick2D's image capabilities.
     *
     * @param ref
     * @return
     */
    private Image loadImage(String ref) {
        try {
            return new Image(ref);
        } catch (SlickException ex) {
            return null;
        }
    }

    /**
     * Given the reference for a sprite sheet data file, load all the
     * sprite sheet associated with it.
     *
     * @param ref
     * @return
     */
    public List<SpriteSheet> loadSprites(String ref) {
        if (ref == null) {
            return null;
        }
        if (cache.get(ref) != null) {
            return copyList(cache.get(ref));
        }
        Parser parser = Parser.get();
        List<SpriteConfiguration> spriteConfigs = parser.readDataXML(ref);
        List<SpriteSheet> sheets = new ArrayList<>(spriteConfigs.size());
        String[] uniqueNames = getSheetNames(spriteConfigs);
        for (String uniqueName : uniqueNames) {
            int frames = numberOfFrames(spriteConfigs, uniqueName);
            int[] offsetX = new int[frames];
            int[] offsetY = new int[frames];
            int[] delay = new int[frames];
            Image[] imgs = new Image[frames];
            int lastFrame = 0;
            for (Object carryConfig : spriteConfigs) {
                SpriteConfiguration spriteConfig = (SpriteConfiguration) carryConfig;
                if (spriteConfig.getSheetName().equals(uniqueName)) {
                    imgs[lastFrame] = loadImage(filePathSwap(ref, spriteConfig.getRef()));
                    offsetX[lastFrame] = spriteConfig.getOffsetX();
                    offsetY[lastFrame] = spriteConfig.getOffsetY();
                    delay[lastFrame] = spriteConfig.getDelay();
                    lastFrame++;
                }
            }
            sheets.add(new SpriteSheetImpl(imgs, delay, offsetX, offsetY, uniqueName));
        }
        cache.put(ref, sheets);
        return copyList(sheets);
    }

    public void preloadSprite(String dataRef) {
        if (dataRef != null) {
            loadSprites(dataRef);
        }
    }

}
