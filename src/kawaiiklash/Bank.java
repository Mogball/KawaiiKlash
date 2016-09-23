package kawaiiklash;

import java.lang.reflect.Field;

/**
 * The {@code Bank} class is a centralized utility class whose specific
 * purpose is to store the reference paths for all the {@code Sprite}s and
 * {@code Sound}s. In addition, it has several methods to aid in the
 * retrieval of such references.
 *
 * @author Jeff Niu
 */
public class Bank {

    /**
     * Placeholder area for test loading sprites and sounds
     */
    static {
        SpriteLoader sprite = SpriteLoader.get();
        SoundLoader sound = SoundLoader.get();
    }

    /**
     * Retrieve the {@code Sprite} reference of an object. The method will
     * proceed under the assumption that the class field containing the
     * reference has the same name, case sensitive, as the object class.
     *
     * @param obj the object
     * @return the sprite data.xml reference
     */
    public static String getSpriteRef(Object obj) {
        return getSpriteRef(obj.getClass().getSimpleName());
    }

    /**
     * Retrieve the {@code Sound} reference of an object.
     *
     * @see #getSpriteRef(java.lang.Object)
     * @param obj the object
     * @return the sprite data.xml reference
     */
    public static String getSoundRef(Object obj) {
        return getSoundRef(obj.getClass().getSimpleName());
    }

    /**
     * Retrieve the {@code Sprite} reference given the object name.
     *
     * @param name
     * @return
     */
    public static String getSpriteRef(String name) {
        return getRef(name, Bank.Sprite.class);
    }

    /**
     * Retrieve the {@code Sound} reference given the object name.
     *
     * @param name
     * @return
     */
    public static String getSoundRef(String name) {
        return getRef(name, Bank.Sound.class);
    }

    /**
     * Hereafter are defined two classes: {@link Bank.Sound} and
     * {@link Bank.Sprite}. Each class is a utility class whose fields
     * contain the references for all the sprites and sounds. The field
     * name corresponds to the name of the class that will make the most
     * use of the sprites or sounds. This method uses reflection to
     * dynamically retrieve the value of the field based on the name of the
     * field.
     *
     * @param name the name of the field with the desired reference
     * @param bank the inner utility class from which to retrieve
     * @return the reference
     */
    private static String getRef(String name, Class<?> bank) {
        Field reference;
        try {
            reference = bank.getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException ex) {
            reference = null;
        }
        String path = null;
        try {
            if (reference != null) {
                path = (String) reference.get(null);
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            path = null;
        }
        return path;
    }

    /**
     * Prevents the class from being instantiated.
     */
    private Bank() {
    }

    /**
     * Contains all {@code Sprite} references.
     */
    private class Sprite {

        // UserInterface Sprite references
        public static final String PlayButton = "sprites/userInterface/menu/playButton/data.xml";
        public static final String ExitButton = "sprites/userInterface/menu/exitButton/data.xml";
        public static final String EditButton = "sprites/userInterface/menu/editButton/data.xml";
        public static final String ChatField = "sprites/userInterface/menu/chatBar/data.xml";
        public static final String DecideButton = "sprites/userInterface/selection/decide/data.xml";
        public static final String BackButton = "sprites/userInterface/selection/back/data.xml";
        public static final String HeroDisplay = "sprites/userInterface/selection/hero/data.xml";
        public static final String MageDisplay = "sprites/userInterface/selection/mage/data.xml";
        public static final String HermitDisplay = "sprites/userInterface/selection/hermit/data.xml";

        // Player Sprite references.
        public static final String Player = "sprites/players/player/data.xml";
        public static final String Hero = "sprites/players/hero/data.xml";
        public static final String Mage = "sprites/players/mage/data.xml";
        public static final String Hermit = "sprites/players/hermit/data.xml";
        public static final String Cory = "sprites/players/cory/data.xml";
        public static final String Tombstone = "sprites/players/tombstone/data.xml";

        // Utility Sprite references
        public static final String Portal = "sprites/utility/portal/data.xml";
        public static final String Spawner = "sprites/utility/spawner/data.xml";

        // Tile Sprite references.
        public static final String Brick = "sprites/tiles/brick/data.xml";

        // Monster Sprite references
        public static final String OrangeMushroom = "sprites/monsters/orangeMushroom/data.xml";
        public static final String RibbonPig = "sprites/monsters/ribbonPig/data.xml";
        public static final String BlueRibbonPig = "sprites/monsters/blueRibbonPig/data.xml";
        public static final String GreenMushroom = "sprites/monsters/greenMushroom/data.xml";
        public static final String Stump = "sprites/monsters/stump/data.xml";
        public static final String BlueMushroom = "sprites/monsters/blueMushroom/data.xml";
        public static final String StoneGolem = "sprites/monsters/stoneGolem/data.xml";
        public static final String Shroom = "sprites/monsters/shroom/data.xml";
        public static final String Pig = "sprites/monsters/pig/data.xml";

        // Advanced Monster Sprite reference
        public static final String NeoHuroid = "sprites/monsters/neoHuroid/data.xml";
        public static final String NeoHuroidRocket = "sprites/monsters/neoHuroid/rocket/data.xml";
        public static final String DRoy = "sprites/monsters/dRoy/data.xml";
        public static final String DRoySmash = "sprites/monsters/dRoy/smash/data.xml";
        public static final String DRoyLaser = "sprites/monsters/dRoy/laser/data.xml";

        // Boss Sprite reference
        public static final String BossNeoHuroid = NeoHuroid;
        public static final String BossDRoy = DRoy;

        // Attack Sprite references
        public static final String SlashBlast = "sprites/attacks/slashBlast/data.xml";
        public static final String RadiantDriver = "sprites/attacks/radiantDriver/data.xml";
        public static final String IntrepidSlash = "sprites/attacks/intrepidSlash/data.xml";
        public static final String FlameOrb = "sprites/attacks/flameOrb/data.xml";
        public static final String ThunderBolt = "sprites/attacks/thunderBolt/data.xml";
        public static final String HolyArrow = "sprites/attacks/holyArrow/data.xml";
        public static final String ShadowSpark = "sprites/attacks/shadowSpark/data.xml";
        public static final String QuadStar = "sprites/attacks/quadStar/data.xml";
        public static final String QuintupleThrow = "sprites/attacks/quintupleThrow/data.xml";

        // Item Sprite references
        public static final String HealthPotion = "sprites/items/potions/healthPotion/data.xml";

        // Background Sprite references
        public static final String BlueSky = "sprites/backgrounds/blueSky/data.xml";
        public static final String Cloud = "sprites/backgrounds/cloud/data.xml";
        public static final String RockyMountains = "sprites/backgrounds/rockyMountains/data.xml";
        public static final String Forest = "sprites/backgrounds/forest/data.xml";
        public static final String Bushes = "sprites/backgrounds/bushes/data.xml";
        public static final String Tree = "sprites/backgrounds/tree/data.xml";
        public static final String TrainTrack = "sprites/backgrounds/trainTrack/data.xml";
        public static final String WhiteMountains = "sprites/backgrounds/whiteMountains/data.xml";
        public static final String MountainRange = "sprites/backgrounds/mountainRange/data.xml";
        public static final String Blimp = "sprites/backgrounds/blimp/data.xml";
        public static final String Mansion = "sprites/backgrounds/mansion/data.xml";

        // HeadsUpDisplay Sprite references
        public static final String HealthBar = "sprites/headsUpDisplay/healthBar/data.xml";
        public static final String HealthBarIndicator = "sprites/headsUpDisplay/healthBar/indicator/data.xml";

        /**
         * Private constructor.
         */
        private Sprite() {
        }

    }

    /**
     * Contains all {@code Sound} references.
     */
    private class Sound {

        // Player Sound references
        public static final String Player = "sounds/player/data.xml";

        // Monster Sound references
        public static final String OrangeMushroom = "sounds/monsters/orangeMushroom/data.xml";
        public static final String RibbonPig = "sounds/monsters/ribbonPig/data.xml";
        public static final String BlueRibbonPig = "sounds/monsters/blueRibbonPig/data.xml";
        public static final String GreenMushroom = "sounds/monsters/greenMushroom/data.xml";
        public static final String Stump = "sounds/monsters/stump/data.xml";
        public static final String BlueMushroom = "sounds/monsters/blueMushroom/data.xml";
        public static final String StoneGolem = "sounds/monsters/stoneGolem/data.xml";
        public static final String Shroom = "sounds/monsters/shroom/data.xml";
        public static final String Pig = "sounds/monsters/pig/data.xml";

        // Advanced Monster Sound references
        public static final String NeoHuroid = "sounds/monsters/neoHuroid/data.xml";
        public static final String NeoHuroidRocket = "sounds/monsters/neoHuroid/rocket/data.xml";
        public static final String DRoy = "sounds/monsters/dRoy/data.xml";
        public static final String DRoySmash = "sounds/monsters/dRoy/smash/data.xml";
        public static final String DRoyLaser = "sounds/monsters/dRoy/laser/data.xml";

        // Boss Sound reference
        public static final String BossNeoHuroid = NeoHuroid;
        public static final String BossDRoy = DRoy;

        // Attack Sound references
        public static final String SlashBlast = "sounds/attacks/slashBlast/data.xml";
        public static final String RadiantDriver = "sounds/attacks/radiantDriver/data.xml";
        public static final String IntrepidSlash = "sounds/attacks/intrepidSlash/data.xml";
        public static final String FlameOrb = "sounds/attacks/flameOrb/data.xml";
        public static final String ThunderBolt = "sounds/attacks/thunderBolt/data.xml";
        public static final String HolyArrow = "sounds/attacks/holyArrow/data.xml";
        public static final String ShadowSpark = "sounds/attacks/shadowSpark/data.xml";
        public static final String QuadStar = "sounds/attacks/quadStar/data.xml";
        public static final String QuintupleThrow = "sounds/attacks/quintupleThrow/data.xml";

        // Item Sound references
        public static final String Item = "sounds/items/data.xml";

        /**
         * Private constructor.
         */
        private Sound() {
        }

    }

}
